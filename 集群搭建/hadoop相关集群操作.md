
# 一.hdfs集群操作 #
 + 1.查看hdfs中的目录信息
    hadoop fs -ls /hdfs路径
    
 + 2.上传文件到hdfs中
    hadoop fs -put /本地文件  /aaa
    hadoop fs -copyFromLocal /本地文件  /hdfs路径   ##  copyFromLocal等价于 put
    
    hadoop fs -moveFromLocal /本地文件  /hdfs路径  ## 跟copyFromLocal的区别是：从本地移动到hdfs中
    
 + 3.下载文件到客户端本地磁盘
    hadoop fs -get /hdfs中的路径   /本地磁盘目录
    hadoop fs -copyToLocal /hdfs中的路径 /本地磁盘路径   ## 跟get等价
    hadoop fs -moveToLocal /hdfs路径  /本地路径  ## 从hdfs中移动到本地
    
 + 4.在hdfs中创建文件夹
    hadoop fs -mkdir  -p /aaa/xxx
    
 + 5.移动hdfs中的文件（更名）
    hadoop fs -mv /hdfs的路径  /hdfs的另一个路径
    
 + 6.删除hdfs中的文件或文件夹
    hadoop fs -rm -r /aaa
    
 + 7.修改文件的权限
    hadoop fs -chown user:group /aaa
    hadoop fs -chmod 700 /aaa
    
 + 8.追加内容到已存在的文件
    hadoop fs -appendToFile /本地文件   /hdfs中的文件
    
 + 9.显示文本文件的内容
    hadoop fs -cat /hdfs中的文件
    hadoop fs -tail /hdfs中的文件
    
 + 10.补充：hdfs命令行客户端的所有命令列表
    Usage: hadoop fs [generic options]

 + 11.NameNode文件内容查看
 ```
 NameNode主要包括以下文件[/data/hadoop_repo/dfs/name/current]：
    fsimage:元数据镜像文件。存储某一时段NameNode内存中的元数据信息。
    edits:操作日志文件【事务文件】。
    seen_txid:是存放transactionId的文件，format之后是0，它代表的是namenode里面的edits_*文件的尾数,namenode重启的时候，会按照seen_txid的数字，循序从头跑edits_0000001~到seen_txid的数字。
    VERSION:保存了HDFS的版本信息
    查看文件内容：
        hdfs oiv -p XML -i  fsimage_0000000000000000005  -o fsimage.xml
        hdfs oev -i  edits_inprogress_0000000000000000007  -o edits.xml
 ```  
 
# 二.yarn集群操作 #
### 1.运行mapper reducer工程 ###
 + a.将整个工程（yarn客户端类mapreduce所有jar和自定义类）打成jar包
 + b.然后，将jar包上传到hadoop集群中的任意一台机器上
 + c,最后，运行jar包中的（YARN客户端类）
        [root@hdp-04 ~]# hadoop jar wc.jar cn.edu360.hadoop.mr.wc.JobSubmitter

### 2.MapReduce引用第三方jar包 ###
+ a.将第三方jar包和你的MapReduce程序打成一个jar包
    优点：使用的时候方便，只需要指定mapreduce的jar包即可
    缺点：如果依赖的jar包很多，会造成打的依赖包很大，上传到服务器会比较慢
+ b.使用 libjars 这个参数
    `hadoop jar hello.jar packagename.ClassName -libjars /data/fastjson-1.2.47.jar /inputpath /outputpath`
    优点：mapreduce的jar包中只包含业务代码，打包以及上传都很快。多个依赖jar之间用逗号隔开
    依赖jar包的路径可以使用hdfs路径吗？可以！！
    缺点：每次启动jar包的时候都需要在后面指定一堆依赖的jar名称
    解决方案：【可以用shell脚本保存命令】
    注意：想要使用-libjars需要调整一下代码
    ```
    public static void main(String[] args) throw Exception{
        Configuration conf = new Configuration();
        //两个输入参数不能直接从args中获取
        String[] remainArgs = new GenericOptionsParser(con,args).getRemainingArgs();
        String inputPath = remainArgs[0];
        String outputPath = remainArgs[1];
    }
    ```

### 3.停止yarn上的任务 ###
    `yarn application -kill <application_id>`
    注意：
    在命令行ctrl+c无法停止程序，因为程序已经提交到yarn集群运行了
    `yarn application -kill` 不仅可以停止mr任务，只要是在yarn上运行的任务，都可以使用这个命令杀掉进程。

# 三.zookeeper集群操作 #
```
    bin/zkCli.sh   #进入本机客户端
    bin/zkCli.sh -server hadoop101:2181  #连接其他机器2181为服务端口
    
    创建节点： create /aaa 'ppppp'
    查看节点下的子节点：   ls /aaa
    获取节点的value： get /aaa
    修改节点的value： set /aaa 'mmmmm'
    删除节点：rmr /aaa
    数据监听功能
        ls /aaa watch   
        ## 查看/aaa的子节点的同时，注册了一个监听“节点的子节点变化事件”的监听器
        
        get /aaa watch
        ## 获取/aaa的value的同时，注册了一个监听“节点value变化事件”的监听器
```
   
