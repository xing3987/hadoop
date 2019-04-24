CREATE EXTERNAL TABLE ods_app_log (
user_id string
, cid_sn string
, mobile_data_type string
, os_ver string
, mac string
, resolution string
, commit_time string
, sdk_ver string
, device_id_type string
, city string
, android_id string
, device_model string
, carrier string
, promotion_channel string
, app_ver_name string
, imei string
, app_ver_code string
, pid string
, net_type string
, device_id string
, app_device_id string
, release_channel string
, country string
, time_zone string
, os_name string
, manufacture string
, commit_id string
, app_token string
, account string
, app_id string
, build_num string
, language string
) partitioned BY (
dt string
) row format delimited fields terminated BY ',' location '/eggplant';

ALTER TABLE ods_app_log ADD PARTITION (dt = '2019-04-18') location '/eggplant/2019-04-18';
ALTER TABLE ods_app_log ADD PARTITION (dt = '2019-04-19') location '/eggplant/2019-04-19';
ALTER TABLE ods_app_log ADD PARTITION (dt = '2019-04-20') location '/eggplant/2019-04-20';
ALTER TABLE ods_app_log ADD PARTITION (dt = '2019-04-21') location '/eggplant/2019-04-21';

/*
日新：当日第一次出现的用户--当日的新增用户

思路： a、应该建立一个历史用户表（只存user_id）

       b、将当日的活跃用户去 比对  历史用户表， 就知道哪些人是今天新出现的用户 --> 当日新增用户

	   c、将当日新增用户追加到历史用户表

*/

-- 数据建模  *******************************

-- 1 历史用户表
create table etl_user_history(user_id string);


-- 2 当日新增用户表:存所有字段（每个人时间最早的一条）,带有一个分区字段：dt string;
create table etl_user_new_day like ods_app_log;


-- 有分区所以不能使用a.*插入
insert  into etl_user_new_day partition(dt='2019-04-18')
select
sdk_ver
    ,time_zone
    ,commit_id
    ,commit_time
    ,pid
    ,app_token
    ,app_id
    ,device_id
    ,device_id_type
    ,release_channel
    ,app_ver_name
    ,app_ver_code
    ,os_name
    ,os_ver
    ,language
    ,country
    ,manufacture
    ,device_model
    ,resolution
    ,net_type
    ,account
    ,app_device_id
    ,mac
    ,android_id
    ,imei
    ,cid_sn
    ,build_num
    ,mobile_data_type
    ,promotion_channel
    ,carrier
    ,city
    ,a.user_id
from ods_app_log a left join etl_user_history b on a.user_id=b.user_id
where a.dt='2019-04-18' and b.user_id is null;

-- 2 将当日新增用户的user_id追加到历史表
insert into table etl_user_history
select user_id from etl_user_new_day where dt='2019-04-18';

维度组合统计 (统计出各种组合的数据用于导出到mysql中给前端直接查询使用)
0 0 0 0
0 0 0 1
0 0 1 0
0 0 1 1
0 1 0 0
0 1 0 1
0 1 1 0
0 1 1 1
1 0 0 0
1 0 0 1
1 0 1 0
1 0 1 1
1 1 0 0
1 1 0 1
1 1 1 0
1 1 1 1

*/

-- 1 日新维度统计报表--数据建模
create table dim_user_new_day(os_name string,city string,release_channel string,app_ver_name string,cnts int)
partitioned by (dt string, dim string);


-- 2 日新维度统计报表sql开发(利用多重插入语法)
from etl_user_new_day

insert into table dim_user_new_day partition(dt='2019-04-18',dim='0000')
select 'all','all','all','all',count(1)
where dt='2019-04-18'

insert into table dim_user_new_day partition(dt='2019-04-18',dim='0001')
select 'all','all','all',app_ver_name,count(1)
where dt='2019-04-18'
group by app_ver_name

insert into table dim_user_new_day partition(dt='2019-04-18',dim='0010')
select 'all','all',release_channel,'all',count(1)
where dt='2019-04-18'
group by release_channel

insert into table dim_user_new_day partition(dt='2019-04-18',dim='0011')
select 'all','all',release_channel,app_ver_name,count(1)
where dt='2019-04-18'
group by release_channel,app_ver_name

insert into table dim_user_new_day partition(dt='2019-04-18',dim='0100')
select 'all',city,'all','all',count(1)
where dt='2019-04-18'
group by city

insert into table dim_user_new_day partition(dt='2019-04-18',dim='0101')
select 'all',city,'all',app_ver_name,count(1)
where dt='2019-04-18'
group by city,app_ver_name

insert into table dim_user_new_day partition(dt='2019-04-18',dim='0110')
select 'all',city,release_channel,'all',count(1)
where dt='2019-04-18'
group by city,release_channel

insert into table dim_user_new_day partition(dt='2019-04-18',dim='0111')
select 'all',city,release_channel,app_ver_name,count(1)
where dt='2019-04-18'
group by city,release_channel,app_ver_name

insert into table dim_user_new_day partition(dt='2019-04-18',dim='1000')
select os_name,'all','all','all',count(1)
where dt='2019-04-18'
group by os_name

insert into table dim_user_new_day partition(dt='2019-04-18',dim='1001')
select os_name,'all','all',app_ver_name,count(1)
where dt='2019-04-18'
group by os_name,app_ver_name

insert into table dim_user_new_day partition(dt='2019-04-18',dim='1010')
select os_name,'all',release_channel,'all',count(1)
where dt='2019-04-18'
group by os_name,release_channel

insert into table dim_user_new_day partition(dt='2019-04-18',dim='1011')
select os_name,'all',release_channel,app_ver_name,count(1)
where dt='2019-04-18'
group by os_name,release_channel,app_ver_name

insert into table dim_user_new_day partition(dt='2019-04-18',dim='1100')
select os_name,city,'all','all',count(1)
where dt='2019-04-18'
group by os_name,city

insert into table dim_user_new_day partition(dt='2019-04-18',dim='1101')
select os_name,city,'all',app_ver_name,count(1)
where dt='2019-04-18'
group by os_name,city,app_ver_name

insert into table dim_user_new_day partition(dt='2019-04-18',dim='1110')
select os_name,city,release_channel,'all',count(1)
where dt='2019-04-18'
group by os_name,city,release_channel

insert into table dim_user_new_day partition(dt='2019-04-18',dim='1111')
select os_name,city,release_channel,app_ver_name,count(1)
where dt='2019-04-18'
group by os_name,city,release_channel,app_ver_name

