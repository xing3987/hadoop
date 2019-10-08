hive 窗口分析函数

0: jdbc:hive2://localhost:10000> select * from t_access;
+----------------+---------------------------------+-----------------------+--------------+--+
|  t_access.ip   |          t_access.url           | t_access.access_time  | t_access.dt  |
+----------------+---------------------------------+-----------------------+--------------+--+
| 192.168.33.3   | http://www.edu360.cn/stu        | 2017-08-04 15:30:20   | 20170804     |
| 192.168.33.3   | http://www.edu360.cn/teach      | 2017-08-04 15:35:20   | 20170804     |
| 192.168.33.4   | http://www.edu360.cn/stu        | 2017-08-04 15:30:20   | 20170804     |
| 192.168.33.4   | http://www.edu360.cn/job        | 2017-08-04 16:30:20   | 20170804     |
| 192.168.33.5   | http://www.edu360.cn/job        | 2017-08-04 15:40:20   | 20170804     |
| 192.168.33.3   | http://www.edu360.cn/stu        | 2017-08-05 15:30:20   | 20170805     |
| 192.168.44.3   | http://www.edu360.cn/teach      | 2017-08-05 15:35:20   | 20170805     |
| 192.168.33.44  | http://www.edu360.cn/stu        | 2017-08-05 15:30:20   | 20170805     |
| 192.168.33.46  | http://www.edu360.cn/job        | 2017-08-05 16:30:20   | 20170805     |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-05 15:40:20   | 20170805     |
| 192.168.133.3  | http://www.edu360.cn/register   | 2017-08-06 15:30:20   | 20170806     |
| 192.168.111.3  | http://www.edu360.cn/register   | 2017-08-06 15:35:20   | 20170806     |
| 192.168.34.44  | http://www.edu360.cn/pay        | 2017-08-06 15:30:20   | 20170806     |
| 192.168.33.46  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20   | 20170806     |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-06 15:40:20   | 20170806     |
| 192.168.33.46  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20   | 20170806     |
| 192.168.33.25  | http://www.edu360.cn/job        | 2017-08-06 15:40:20   | 20170806     |
| 192.168.33.36  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20   | 20170806     |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-06 15:40:20   | 20170806     |
+----------------+---------------------------------+-----------------------+--------------+--+

## LAG函数
select ip,url,access_time,
row_number() over(partition by ip order by access_time) as rn,
lag(access_time,1,0) over(partition by ip order by access_time)as last_access_time
from t_access;

+----------------+---------------------------------+----------------------+-----+----------------------+--+
|       ip       |               url               |     access_time      | rn  |   last_access_time   |
+----------------+---------------------------------+----------------------+-----+----------------------+--+
| 192.168.111.3  | http://www.edu360.cn/register   | 2017-08-06 15:35:20  | 1   | 0                    |
| 192.168.133.3  | http://www.edu360.cn/register   | 2017-08-06 15:30:20  | 1   | 0                    |
| 192.168.33.25  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 1   | 0                    |
| 192.168.33.3   | http://www.edu360.cn/stu        | 2017-08-04 15:30:20  | 1   | 0                    |
| 192.168.33.3   | http://www.edu360.cn/teach      | 2017-08-04 15:35:20  | 2   | 2017-08-04 15:30:20  |
| 192.168.33.3   | http://www.edu360.cn/stu        | 2017-08-05 15:30:20  | 3   | 2017-08-04 15:35:20  |
| 192.168.33.36  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 1   | 0                    |
| 192.168.33.4   | http://www.edu360.cn/stu        | 2017-08-04 15:30:20  | 1   | 0                    |
| 192.168.33.4   | http://www.edu360.cn/job        | 2017-08-04 16:30:20  | 2   | 2017-08-04 15:30:20  |
| 192.168.33.44  | http://www.edu360.cn/stu        | 2017-08-05 15:30:20  | 1   | 0                    |
| 192.168.33.46  | http://www.edu360.cn/job        | 2017-08-05 16:30:20  | 1   | 0                    |
| 192.168.33.46  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 2   | 2017-08-05 16:30:20  |
| 192.168.33.46  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 3   | 2017-08-06 16:30:20  |
| 192.168.33.5   | http://www.edu360.cn/job        | 2017-08-04 15:40:20  | 1   | 0                    |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-05 15:40:20  | 1   | 0                    |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 2   | 2017-08-05 15:40:20  |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 3   | 2017-08-06 15:40:20  |
| 192.168.34.44  | http://www.edu360.cn/pay        | 2017-08-06 15:30:20  | 1   | 0                    |
| 192.168.44.3   | http://www.edu360.cn/teach      | 2017-08-05 15:35:20  | 1   | 0                    |
+----------------+---------------------------------+----------------------+-----+----------------------+--+


## LEAD函数
select ip,url,access_time,
row_number() over(partition by ip order by access_time) as rn,
lead(access_time,1,0) over(partition by ip order by access_time)as last_access_time
from t_access;
+----------------+---------------------------------+----------------------+-----+----------------------+--+
|       ip       |               url               |     access_time      | rn  |   last_access_time   |
+----------------+---------------------------------+----------------------+-----+----------------------+--+
| 192.168.111.3  | http://www.edu360.cn/register   | 2017-08-06 15:35:20  | 1   | 0                    |
| 192.168.133.3  | http://www.edu360.cn/register   | 2017-08-06 15:30:20  | 1   | 0                    |
| 192.168.33.25  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 1   | 0                    |
| 192.168.33.3   | http://www.edu360.cn/stu        | 2017-08-04 15:30:20  | 1   | 2017-08-04 15:35:20  |
| 192.168.33.3   | http://www.edu360.cn/teach      | 2017-08-04 15:35:20  | 2   | 2017-08-05 15:30:20  |
| 192.168.33.3   | http://www.edu360.cn/stu        | 2017-08-05 15:30:20  | 3   | 0                    |
| 192.168.33.36  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 1   | 0                    |
| 192.168.33.4   | http://www.edu360.cn/stu        | 2017-08-04 15:30:20  | 1   | 2017-08-04 16:30:20  |
| 192.168.33.4   | http://www.edu360.cn/job        | 2017-08-04 16:30:20  | 2   | 0                    |
| 192.168.33.44  | http://www.edu360.cn/stu        | 2017-08-05 15:30:20  | 1   | 0                    |
| 192.168.33.46  | http://www.edu360.cn/job        | 2017-08-05 16:30:20  | 1   | 2017-08-06 16:30:20  |
| 192.168.33.46  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 2   | 2017-08-06 16:30:20  |
| 192.168.33.46  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 3   | 0                    |
| 192.168.33.5   | http://www.edu360.cn/job        | 2017-08-04 15:40:20  | 1   | 0                    |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-05 15:40:20  | 1   | 2017-08-06 15:40:20  |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 2   | 2017-08-06 15:40:20  |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 3   | 0                    |
| 192.168.34.44  | http://www.edu360.cn/pay        | 2017-08-06 15:30:20  | 1   | 0                    |
| 192.168.44.3   | http://www.edu360.cn/teach      | 2017-08-05 15:35:20  | 1   | 0                    |
+----------------+---------------------------------+----------------------+-----+----------------------+--+


## FIRST_VALUE 函数
例：取每个用户访问的第一个页面
select ip,url,access_time,
row_number() over(partition by ip order by access_time) as rn,
first_value(url) over(partition by ip order by access_time rows between unbounded preceding and unbounded following)as last_access_time
from t_access;
+----------------+---------------------------------+----------------------+-----+---------------------------------+--+
|       ip       |               url               |     access_time      | rn  |        last_access_time         |
+----------------+---------------------------------+----------------------+-----+---------------------------------+--+
| 192.168.111.3  | http://www.edu360.cn/register   | 2017-08-06 15:35:20  | 1   | http://www.edu360.cn/register   |
| 192.168.133.3  | http://www.edu360.cn/register   | 2017-08-06 15:30:20  | 1   | http://www.edu360.cn/register   |
| 192.168.33.25  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 1   | http://www.edu360.cn/job        |
| 192.168.33.3   | http://www.edu360.cn/stu        | 2017-08-04 15:30:20  | 1   | http://www.edu360.cn/stu        |
| 192.168.33.3   | http://www.edu360.cn/teach      | 2017-08-04 15:35:20  | 2   | http://www.edu360.cn/stu        |
| 192.168.33.3   | http://www.edu360.cn/stu        | 2017-08-05 15:30:20  | 3   | http://www.edu360.cn/stu        |
| 192.168.33.36  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 1   | http://www.edu360.cn/excersize  |
| 192.168.33.4   | http://www.edu360.cn/stu        | 2017-08-04 15:30:20  | 1   | http://www.edu360.cn/stu        |
| 192.168.33.4   | http://www.edu360.cn/job        | 2017-08-04 16:30:20  | 2   | http://www.edu360.cn/stu        |
| 192.168.33.44  | http://www.edu360.cn/stu        | 2017-08-05 15:30:20  | 1   | http://www.edu360.cn/stu        |
| 192.168.33.46  | http://www.edu360.cn/job        | 2017-08-05 16:30:20  | 1   | http://www.edu360.cn/job        |
| 192.168.33.46  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 2   | http://www.edu360.cn/job        |
| 192.168.33.46  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 3   | http://www.edu360.cn/job        |
| 192.168.33.5   | http://www.edu360.cn/job        | 2017-08-04 15:40:20  | 1   | http://www.edu360.cn/job        |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-05 15:40:20  | 1   | http://www.edu360.cn/job        |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 2   | http://www.edu360.cn/job        |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 3   | http://www.edu360.cn/job        |
| 192.168.34.44  | http://www.edu360.cn/pay        | 2017-08-06 15:30:20  | 1   | http://www.edu360.cn/pay        |
| 192.168.44.3   | http://www.edu360.cn/teach      | 2017-08-05 15:35:20  | 1   | http://www.edu360.cn/teach      |
+----------------+---------------------------------+----------------------+-----+---------------------------------+--+

## LAST_VALUE 函数
例：取每个用户访问的最后一个页面
select ip,url,access_time,
row_number() over(partition by ip order by access_time) as rn,
last_value(url) over(partition by ip order by access_time rows between unbounded preceding and unbounded following)as last_access_time
from t_access;
+----------------+---------------------------------+----------------------+-----+---------------------------------+--+
|       ip       |               url               |     access_time      | rn  |        last_access_time         |
+----------------+---------------------------------+----------------------+-----+---------------------------------+--+
| 192.168.111.3  | http://www.edu360.cn/register   | 2017-08-06 15:35:20  | 1   | http://www.edu360.cn/register   |
| 192.168.133.3  | http://www.edu360.cn/register   | 2017-08-06 15:30:20  | 1   | http://www.edu360.cn/register   |
| 192.168.33.25  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 1   | http://www.edu360.cn/job        |
| 192.168.33.3   | http://www.edu360.cn/stu        | 2017-08-04 15:30:20  | 1   | http://www.edu360.cn/stu        |
| 192.168.33.3   | http://www.edu360.cn/teach      | 2017-08-04 15:35:20  | 2   | http://www.edu360.cn/stu        |
| 192.168.33.3   | http://www.edu360.cn/stu        | 2017-08-05 15:30:20  | 3   | http://www.edu360.cn/stu        |
| 192.168.33.36  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 1   | http://www.edu360.cn/excersize  |
| 192.168.33.4   | http://www.edu360.cn/stu        | 2017-08-04 15:30:20  | 1   | http://www.edu360.cn/stu        |
| 192.168.33.4   | http://www.edu360.cn/job        | 2017-08-04 16:30:20  | 2   | http://www.edu360.cn/stu        |
| 192.168.33.44  | http://www.edu360.cn/stu        | 2017-08-05 15:30:20  | 1   | http://www.edu360.cn/stu        |
| 192.168.33.46  | http://www.edu360.cn/job        | 2017-08-05 16:30:20  | 1   | http://www.edu360.cn/job        |
| 192.168.33.46  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 2   | http://www.edu360.cn/job        |
| 192.168.33.46  | http://www.edu360.cn/excersize  | 2017-08-06 16:30:20  | 3   | http://www.edu360.cn/job        |
| 192.168.33.5   | http://www.edu360.cn/job        | 2017-08-04 15:40:20  | 1   | http://www.edu360.cn/job        |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-05 15:40:20  | 1   | http://www.edu360.cn/job        |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 2   | http://www.edu360.cn/job        |
| 192.168.33.55  | http://www.edu360.cn/job        | 2017-08-06 15:40:20  | 3   | http://www.edu360.cn/job        |
| 192.168.34.44  | http://www.edu360.cn/pay        | 2017-08-06 15:30:20  | 1   | http://www.edu360.cn/pay        |
| 192.168.44.3   | http://www.edu360.cn/teach      | 2017-08-05 15:35:20  | 1   | http://www.edu360.cn/teach      |
+----------------+---------------------------------+----------------------+-----+---------------------------------+--+


/*
	累计报表--分析函数实现版
*/
-- sum() over() 函数
select id
,month
,sum(amount) over(partition by id order by month rows between unbounded preceding and current row)
from
(select id,month,
sum(fee) as amount
from t_test
group by id,month) tmp;


/*打序号

*/
row_number() over()
rank() over()
dense_rank() over()

求薪资排名中位于前1/3的人
ntile(2) over()

score   rownumber   rankover    dense_rank    ntile
89       1              1             1          1
90       2              2             2          1
90       3              2             2          1
91       4              4             3          2
92       5              5             4          2
92       6              5             4          2
93       7              7             5          3

求薪资排名中位于前1/3的人
ntile(2) over()













