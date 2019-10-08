/* 
留存用户分析
概念：昨日新增，今日还活跃

逻辑思路：昨天在新用户表中，今天在活跃用户表中 --> 今日的“次日留存用户”
-- 昨天的新用户表中，存在于今天的活跃用户表中的人  --> 今日的“次日留存用户”


*/


-- 数据建模 
--建次日留存etl信息表：记录跟活跃用户表相同的字段

create table etl_user_keepalive_nextday like etl_user_active_day;

-- etl开发
insert into table etl_user_keepalive_nextday partition(day='2017-09-22')
select
     actuser.sdk_ver 
    ,actuser.time_zone 
    ,actuser.commit_id 
    ,actuser.commit_time 
    ,actuser.pid 
    ,actuser.app_token 
    ,actuser.app_id 
    ,actuser.device_id 
    ,actuser.device_id_type 
    ,actuser.release_channel 
    ,actuser.app_ver_name 
    ,actuser.app_ver_code 
    ,actuser.os_name 
    ,actuser.os_ver 
    ,actuser.language 
    ,actuser.country 
    ,actuser.manufacture 
    ,actuser.device_model 
    ,actuser.resolution 
    ,actuser.net_type 
    ,actuser.account 
    ,actuser.app_device_id 
    ,actuser.mac 
    ,actuser.android_id 
    ,actuser.imei 
    ,actuser.cid_sn 
    ,actuser.build_num 
    ,actuser.mobile_data_type 
    ,actuser.promotion_channel 
    ,actuser.carrier 
    ,actuser.city 
    ,actuser.user_id 


from etl_user_new_day newuser join etl_user_active_day actuser
on newuser.user_id = actuser.user_id
where newuser.day='2017-09-21' and actuser.day='2017-09-22';



/*  ***************用左半连接效率略高***************************************************** */
insert into table etl_user_keepalive_nextday partition(day='2017-09-22')
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
,user_id 
from etl_user_new_day a left semi join etl_user_active_day b
on a.user_id = b.user_id and a.day='2017-09-21' and b.day='2017-09-22';

where a.day='2017-09-21' and b.day='2017-09-22'; // 注意：left semi join中，右表的引用不能出现在where条件中


/*
维度统计
*/

-- 利用多重插入语法


