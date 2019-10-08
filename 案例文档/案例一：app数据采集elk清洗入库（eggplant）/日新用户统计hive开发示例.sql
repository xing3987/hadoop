/*
日新：当日第一次出现的用户--当日的新增用户

思路： a、应该建立一个历史用户表（只存user_id）

       b、将当日的活跃用户去 比对  历史用户表， 就知道哪些人是今天新出现的用户 --> 当日新增用户
	   
	   c、将当日新增用户追加到历史用户表

*/

-- 数据建模  *******************************

-- 1 历史用户表
create table etl_user_history(user_id string);


-- 2 当日新增用户表:存所有字段（每个人时间最早的一条）,带有一个分区字段：day string;
create table etl_user_new_day like etl_user_active_day;


-- 统计实现 *********************************

-- 1 当日活跃-历史用户表 --> 新增用户表的当日分区
insert  into etl_user_new_day partition(day='2017-09-21')
SELECT sdk_ver
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
    ,LANGUAGE
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
from  etl_user_active_day a left join  etl_user_history b on a.user_id = b.user_id
where a.day='2017-09-21' and b.user_id is null;


-- 2 将当日新增用户的user_id追加到历史表
insert into table etl_user_history
select user_id from etl_user_new_day where day='2017-09-21';



/*
日新：维度统计报表

思路： a、从日新etl表中，按照维度组合，统计出各种维度组合下的新用户数量

维度：
os_name    city    release_channel   app_ver_name


维度组合统计 
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
partitioned by (day string, dim string);


-- 2 日新维度统计报表sql开发(利用多重插入语法)
from etl_user_new_day

insert into table dim_user_new_day partition(day='2017-09-21',dim='0000')
select 'all','all','all','all',count(1)
where day='2017-09-21'


insert into table dim_user_new_day partition(day='2017-09-21',dim='0001')
select 'all','all','all',app_ver_name,count(1)
where day='2017-09-21'
group by app_ver_name



insert into table dim_user_new_day partition(day='2017-09-21',dim='0010')
select 'all','all',release_channel,'all',count(1)
where day='2017-09-21'
group by release_channel


insert into table dim_user_new_day partition(day='2017-09-21',dim='0011')
select 'all','all',release_channel,app_ver_name,count(1)
where day='2017-09-21'
group by release_channel,app_ver_name


insert into table dim_user_new_day partition(day='2017-09-21',dim='0100')
select 'all',city,'all','all',count(1)
where day='2017-09-21'
group by city

-- 亲爱的们帮我搞定
;










