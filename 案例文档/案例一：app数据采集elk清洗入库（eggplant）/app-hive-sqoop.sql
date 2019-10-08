
-- mysql建表
CREATE TABLE dim_user_active_day (
    os_name varchar(20)
    ,city  varchar(20)
    ,release_channel  varchar(20)
    ,app_ver_name  varchar(20)
    ,cnts INT(10)
	,day  varchar(20)
    );

	
	
bin/sqoop export --connect "jdbc:mysql://hdp-04:3306/app?useUnicode=true&characterEncoding=utf-8" --username root --password root --table dim_user_active_day --export-dir /user/hive/warehouse/app.db/dim_user_active_day/day=2017-09-21/dim=0000 --input-fields-terminated-by \\001
