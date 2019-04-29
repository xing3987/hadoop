


需要保证集群的各个节点时间同步 `ntpdate -u ntp.sjtu.edu.cn`
# 一.伪分布环境集群搭建(单机版) #
### 1.前提条件 ###
 + a.准备机器：hadoop100
 + b.Linux基础环境准备：java、ip、hostname、hosts、iptables、chkconfig、ssh免密码登录(注意hosts中的主机名和ip对应)
    注意：Jdk建议使用1.8

 + c.hadoop安装包下载地址：
    + 官网下载地址：https://archive.apache.org/dist/hadoop/common/ (国内下载速度慢);
    + 镜像站下载地址：https://mirrors.tuna.tsinghua.edu.cn/apache/hadoop (国内下载速度快)

```
配置环境变量 vi /etc/profile #具体path对应hadoop位置改变
    export HADOOP_HOME=/data/hadoop/hadoop-2.6.5
    export JAVA_HOME=/data/java/jdk1.8.0_191
    export JRE_HOME=${JAVA_HOME}/jre
    export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:${HADOOP_HOME}/lib:$CLASSPATH
    export JAVA_PATH=${JAVA_HOME}/bin:${JRE_HOME}/bin
    export PATH=$PATH:${JAVA_PATH}:${HADOOP_HOME}/bin
关闭防火墙：`service iptables stop  `
关闭防火墙自启： `chkconfig iptables off`   
```
### 2.解压安装包修改配置文件 ###
    ```
    # 建议把hadoop安装包放在/data/soft目录下
    cd /data/soft
    tar -zxvf hadoop-2.7.5.tar.gz
    cd hadoop-2.7.5/etc/hadoop
    
    # 开始修改配置文件
    vi hadoop-env.sh  
    export JAVA_HOME=/data/soft/jdk1.8
    export HADOOP_LOG_DIR=/data/hadoop_repo/logs/hadoop
    
    vi yarn-env.sh 
    export JAVA_HOME=/data/soft/jdk1.8
    export YARN_LOG_DIR=/data/hadoop_repo/logs/yarn
    
    vi core-site.xml
    <configuration>
        <property>
            <name>fs.defaultFS</name>
            <value>hdfs://hadoop100:9000</value>
        </property>
        <property>
            <name>hadoop.tmp.dir</name>
                   <value>/data/hadoop_repo</value>
       </property>
    </configuration>
    
    vi hdfs-site.xml
    <configuration>
            <property>
                    <name>dfs.replication</name>
                    <value>1</value>
            </property>
    </configuration>
    
    vi yarn-site.xml
    <configuration>
            <property>
                    <name>yarn.nodemanager.aux-services</name>
                    <value>mapreduce_shuffle</value>
            </property>
    </configuration>
    
    mv mapred-site.xml.template mapred-site.xml
    vi mapred-site.xml
    <configuration>
            <property>
                    <name>mapreduce.framework.name</name>
                    <value>yarn</value>
            </property>
    </configuration>
    
    vi slaves
    localhost
    
    # 至此，配置文件修改完毕
    ```
### 3.格式化hdfs文件系统,并启动集群 ###
    ```
    cd /data/soft/hadoop-2.7.5
    bin/hdfs namenode -format
    ```

# 二.分布式环境集群搭建 #
### 1.前提条件 ###
+ a.准备机器：hadoop100、hadoop101、hadoop102
+ b.Linux基础环境准备：java、ip、hostname、hosts、iptables、chkconfig、ssh、免密码登录
        注意：Jdk建议使用1.8
        hadoop100是主节点
        ssh-copy-id -i hadoop101
        ssh-copy-id -i hadoop102
        这样，就可以实现主节点到从节点的 SSH 免密码登录了
    
+ c.集群规划
        主节点：hadoop100 从节点：hadoop101、hadoop102
        hadoop100：namenode、resourcemanager、secondarynamenode
        hadoop101：datanode、nodemanager
        hadoop102：datanode、nodemanager
```
配置环境变量 vi /etc/profile #具体path对应hadoop位置改变
    export HADOOP_HOME=/data/hadoop/hadoop-2.6.5
    export JAVA_HOME=/data/java/jdk1.8.0_191
    export JRE_HOME=${JAVA_HOME}/jre
    export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:${HADOOP_HOME}/lib:$CLASSPATH
    export JAVA_PATH=${JAVA_HOME}/bin:${JRE_HOME}/bin
    export PATH=$PATH:${JAVA_PATH}:${HADOOP_HOME}/bin
关闭防火墙：`service iptables stop  `
关闭防火墙自启： `chkconfig iptables off`   
```
### 2.解压安装包修改配置文件 ###
```
    # 注意，先操作hadoop100这个节点
    # 建议把hadoop安装包放在/data/soft目录下
    cd /data/soft
    tar -zxvf hadoop-2.7.5.tar.gz
    cd hadoop-2.7.5/etc/hadoop
    
    # 开始修改配置文件
    vi hadoop-env.sh
    export JAVA_HOME=/data/soft/jdk1.8
    export HADOOP_LOG_DIR=/data/hadoop_repo/logs/hadoop
    
    vi yarn-env.sh
    export JAVA_HOME=/data/soft/jdk1.8
    export YARN_LOG_DIR=/data/hadoop_repo/logs/yarn
    
    vi core-site.xml
    <configuration>
        <property>
            <name>fs.defaultFS</name>
            <value>hdfs://hadoop100:9000</value>
        </property>
        <property>
            <name>hadoop.tmp.dir</name>
            <value>/data/hadoop_repo</value>
       </property>
    </configuration>
    
    vi hdfs-site.xml
    <configuration>
    	<property>
    		<name>dfs.replication</name>
    		<value>2</value>
    	</property>
    	<property>
    		<name>dfs.namenode.secondary.http-address</name> #指定second namenode地址
    		<value>hadoop100:50090</value>
    	</property>
    	<property>
            <name>dfs.blocksize</name> #切块大小的参数
            <value>64m</value>
        </property>
    </configuration>
    
    vi yarn-site.xml
    <configuration>
    	<property>
    		<name>yarn.nodemanager.aux-services</name>
    		<value>mapreduce_shuffle</value>
    	</property>
    	<property>
    		<name>yarn.resourcemanager.hostname</name>
    		<value>hadoop100</value>
    	</property> 
    </configuration>
    
    mv mapred-site.xml.template mapred-site.xml
    vi mapred-site.xml
    <configuration>
            <property>
                    <name>mapreduce.framework.name</name>
                    <value>yarn</value>
            </property>
    </configuration>
    
    vi slaves
    hadoop101
    hadoop102

    # 至此，配置文件修改完毕
```
### 3.拷贝文件到其他机器,格式化hdfs文件系统,并启动集群 ###
```
    # 把hadoop100这个节点上配置好的hadoop的安装包拷贝到其他两个节点中
    # 注意：需要保证这两个节点中的/data目录是存在的
    # 注意：保证每个节点的/etc/hosts文件中都有三个节点的ip和主机名映射
    scp -rq hadoop-2.7.5 hadoop101:/data/soft
    scp -rq hadoop-2.7.5 hadoop102:/data/soft
    
    # 下面开始在hadoop100节点上格式化hdfs文件系统
    cd /data/soft/hadoop-2.7.5
    bin/hdfs namenode -format
    看到里面的successfully就说明文件系统格式化成功了。
    
    启动hadoop，有两种方案
    第一种：在hadoop100节点上执行下面命令
    cd /data/soft/hadoop-2.7.5
    sbin/start-all.sh
    
    第二种：在hadoop100节点上执行下面命令
    cd /data/soft/hadoop-2.7.5
    sbin/start-dfs.sh
    sbin/start-yarn.sh
    
    验证，执行jps，看到下面信息说明启动成功
        在hadoop100上看到如下进程信息：
            41058 secondaryNameNode
            44852 Jps
            41293 ResourceManager
            40846 NameNode
        
        在hadoop101上看到如下进程信息：
            3808 NodeManager
            3701 DataNode
        
        在hadoop102上看到如下进程信息：
            3176 NodeManager
            3069 DataNode
    打开网页
        http://hadoop001:50070/explorer.html#/    #hdfs
        http://hadoop001:8088/cluster           #yarn
```
### 4.查看MapReduce任务输出日志群 ###
+ a.historyserver进程作用
    把之前本来散落在nodemanager节点上的日志统计收集到hdfs上的指定目录中
    修改集群配置文件
    ```
    vi yarn-site.xml
    <property> 
    	<name>yarn.log-aggregation-enable</name>  
    	<value>true</value>
    </property>
    <property>
    	<name>yarn.log.server.url</name>
    	<value>http://hadoop100:19888/jobhistory/logs/</value>
    </property>

    ```
    重启集群,启动historyserver【在所有nodemanager节点启动】
    执行`sbin/mr-jobhistory-daemon.sh start historyserver`

# 三.zookeeper集群搭建 #
### 1.前提条件 ###
+ a.准备机器：hadoop100、hadoop101、hadoop102
+ b.Linux基础环境准备：java、ip、hostname、hosts、iptables、chkconfig、ssh、免密码登录
### 2.解压安装包修改配置文件 ###
```
    # 注意，先操作hadoop100这个节点
    # 建议把zookeeeper放到/data目录下
    cd /data
    tar -zxvf zookeeper-3.4.6.tar.gz
    cd zookeeper-3.4.6/conf
    cp zoo_sample.cfg zoo.cfg
    vi zoo.cfg
        dataDir=/data/zkdata  #日志写出位置
        server.1=hadoop100:2888:3888  #开发自己的投票端口和对外服务端口
        server.2=hadoop101:2888:3888
        server.3=hadoop102:2888:3888
    对3台节点，都创建目录 mkdir /data/zkdata
        对3台节点，在工作目录中生成myid文件，但内容要分别为各自的id： 1,2,3与配置一致
        hadoop100上：  echo 1 > /root/zkdata/myid
        hadoop101上：  echo 2 > /root/zkdata/myid
        hadoop102上：  echo 3 > /root/zkdata/myid
```
## 3.启动zookeeper集群 ###
    在每一台节点上，运行命令：(建议编写自定义脚本启动)
          `bin/zkServer.sh start`
    启动后，用jps应该能看到一个进程：QuorumPeerMain
    但是，光有进程不代表zk已经正常服务，需要用命令检查状态：
           `bin/zkServer.sh status`
    能看到角色模式：为leader或follower，即正常了。
    
# 四.HA集群搭建(防止单点故障导致集群崩溃) #
### 1.前提条件 ###
+ a.准备机器：hadoop00、hadoop01、hadoop02、hadoop03、hadoop04、hadoop05、hadoop06、hadoop07
+ b.Linux基础环境准备：java、ip、hostname、hosts、iptables、chkconfig、ssh、免密码登录
+ c.集群规划
 
 	主机名		IP				安装的软件					运行的进程

	hadoop00	192.168.1.200	jdk、hadoop					NameNode、DFSZKFailoverController(zkfc)
	
	hadoop01	192.168.1.201	jdk、hadoop					NameNode、DFSZKFailoverController(zkfc)
	
	hadoop02	192.168.1.202	jdk、hadoop					ResourceManager 
	
	hadoop03	192.168.1.203	jdk、hadoop					ResourceManager
	
	hadoop04	192.168.1.204	jdk、hadoop					DataNode、NodeManager 
	
	hadoop05	192.168.1.205	jdk、hadoop、zookeeper、DataNode、NodeManager、JournalNode、QuorumPeerMain
	
	hadoop06	192.168.1.206	jdk、hadoop、zookeeper、DataNode、NodeManager、JournalNode、QuorumPeerMain
	
	hadoop07	192.168.1.207	jdk、hadoop、zookeeper、DataNode、NodeManager、JournalNode、QuorumPeerMain

    说明：
    	1.在hadoop2.0中通常由两个NameNode组成，一个处于active状态，另一个处于standby状态。Active NameNode对外提供服务，而Standby NameNode则不对外提供服务，仅同步active namenode的状态，以便能够在它失败时快速进行切换。
    	hadoop2.0官方提供了两种HDFS HA的解决方案，一种是NFS，另一种是QJM。这里我们使用简单的QJM。在该方案中，主备NameNode之间通过一组JournalNode同步元数据信息，一条数据只要成功写入多数JournalNode即认为写入成功。通常配置奇数个JournalNode
    	这里还配置了一个zookeeper集群，用于ZKFC（DFSZKFailoverController）故障转移，当Active NameNode挂掉了，会自动切换Standby NameNode为standby状态
    	2.hadoop-2.2.0中依然存在一个问题，就是ResourceManager只有一个，存在单点故障，hadoop-2.6.4解决了这个问题，有两个ResourceManager，一个是Active，一个是Standby，状态由zookeeper进行协调
    	
### 2.安装步骤 ###
 + 1.安装配置zooekeeper集群（在hadoop05上）
	+ 1.1解压
		`	tar -zxvf zookeeper-3.4.5.tar.gz -C /home/hadoop/app/`
	+ 	1.2修改配置
	 	```
			cd /home/hadoop/app/zookeeper-3.4.5/conf/
			cp zoo_sample.cfg zoo.cfg
			vim zoo.cfg
			修改：dataDir=/home/hadoop/app/zookeeper-3.4.5/tmp
			在最后添加：
			server.1=hadoop05:2888:3888
			server.2=hadoop06:2888:3888
			server.3=hadoop07:2888:3888
			保存退出
			然后创建一个tmp文件夹
			mkdir /home/hadoop/app/zookeeper-3.4.5/tmp
			echo 1 > /home/hadoop/app/zookeeper-3.4.5/tmp/myid
        ```
	+ 	1.3将配置好的zookeeper拷贝到其他节点
	 	```
	 	    首先分别在hadoop06、hadoop07根目录下创建一个hadoop目录：mkdir /hadoop
			scp -r /home/hadoop/app/zookeeper-3.4.5/ hadoop06:/home/hadoop/app/
			scp -r /home/hadoop/app/zookeeper-3.4.5/ hadoop07:/home/hadoop/app/
			
			注意：修改hadoop06、hadoop07对应/hadoop/zookeeper-3.4.5/tmp/myid内容
			hadoop06：
				echo 2 > /home/hadoop/app/zookeeper-3.4.5/tmp/myid
			hadoop07：
				echo 3 > /home/hadoop/app/zookeeper-3.4.5/tmp/myid
	    ```
 +	2.安装配置hadoop集群（在hadoop00上操作）
	+	2.1解压
			tar -zxvf hadoop-2.6.4.tar.gz -C /home/hadoop/app/
	+	2.2配置HDFS（hadoop2.0所有的配置文件都在$HADOOP_HOME/etc/hadoop目录下）
		```
			#将hadoop添加到环境变量中
			vim /etc/profile
			export JAVA_HOME=/usr/java/jdk1.7.0_55
			export HADOOP_HOME=/hadoop/hadoop-2.6.4
			export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin
			
			#hadoop2.0的配置文件全部在$HADOOP_HOME/etc/hadoop下
			cd /home/hadoop/app/hadoop-2.6.4/etc/hadoop
			
			2.2.1修改hadoop-env.sh
				export JAVA_HOME=/home/hadoop/app/jdk1.7.0_55
			
			2.2.2修改core-site.xml
                <configuration>
                <!-- 指定hdfs的nameservice为ns1 -->
                <property>
                <name>fs.defaultFS</name>
                <value>hdfs://hdp24/</value>
                </property>
                <!-- 指定hadoop临时目录 -->
                <property>
                <name>hadoop.tmp.dir</name>
                <value>/root/hdptmp/</value>
                </property>
                
                <!-- 指定zookeeper地址 -->
                <property>
                <name>ha.zookeeper.quorum</name>
                <value>hdp-05:2181,hdp-06:2181,hdp-07:2181</value>
                </property>
                </configuration>	
            2.2.3修改hdfs-site.xml
                <configuration>
                <!--指定hdfs的nameservice为bi，需要和core-site.xml中的保持一致 -->
                <property>
                <name>dfs.nameservices</name>
                <value>hdp24</value>
                </property>
                <!-- hdp24下面有两个NameNode，分别是nn1，nn2 -->
                <property>
                <name>dfs.ha.namenodes.hdp24</name>
                <value>nn1,nn2</value>
                </property>
                <!-- nn1的RPC通信地址 -->
                <property>
                <name>dfs.namenode.rpc-address.hdp24.nn1</name>
                <value>hdp-01:9000</value>
                </property>
                <!-- nn1的http通信地址 -->
                <property>
                <name>dfs.namenode.http-address.hdp24.nn1</name>
                <value>hdp-01:50070</value>
                </property>
                <!-- nn2的RPC通信地址 -->
                <property>
                <name>dfs.namenode.rpc-address.hdp24.nn2</name>
                <value>hdp-02:9000</value>
                </property>
                <!-- nn2的http通信地址 -->
                <property>
                <name>dfs.namenode.http-address.hdp24.nn2</name>
                <value>hdp-02:50070</value>
                </property>
                
                
                <!-- 指定NameNode的edits元数据在机器本地磁盘的存放位置 -->
                <property>
                <name>dfs.namenode.name.dir</name>
                <value>/root/hdpdata/name</value>
                </property>
                
                <property>
                <name>dfs.datanode.data.dir</name>
                <value>/root/hdpdata/data</value>
                </property>
                
                
                <!-- 指定NameNode的共享edits元数据在JournalNode上的存放位置 -->
                <property>
                <name>dfs.namenode.shared.edits.dir</name>
                <value>qjournal://hdp-05:8485;hdp-06:8485;hdp-07:8485/hdp24</value>
                </property>
                
                <!-- 指定JournalNode在本地磁盘存放数据的位置 -->
                <property>
                <name>dfs.journalnode.edits.dir</name>
                <value>/root/hdpdata/journaldata</value>
                </property>
                
                <!-- 开启NameNode失败自动切换 -->
                <property>
                <name>dfs.ha.automatic-failover.enabled</name>
                <value>true</value>
                </property>
                <!-- 配置失败自动切换实现方式 -->
                <property>
                <name>dfs.client.failover.proxy.provider.hdp24</name>
                <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
                </property>
                <!-- 配置隔离机制方法，多个机制用换行分割，即每个机制暂用一行-->
                <property>
                <name>dfs.ha.fencing.methods</name>
                <value>
                sshfence
                shell(/bin/true)
                </value>
                </property>
                <!-- 使用sshfence隔离机制时需要ssh免登陆 -->
                <property>
                <name>dfs.ha.fencing.ssh.private-key-files</name>
                <value>/root/.ssh/id_rsa</value>
                </property>
                <!-- 配置sshfence隔离机制超时时间 -->
                <property>
                <name>dfs.ha.fencing.ssh.connect-timeout</name>
                <value>30000</value>
                </property>
                </configuration>
            2.2.4修改mapred-site.xml
                <configuration>
                <!-- 指定mr框架为yarn方式 -->
                <property>
                <name>mapreduce.framework.name</name>
                <value>yarn</value>
                </property>
                </configuration>
            2.2.5修改yarn-site.xml
                <configuration>
                <!-- 开启RM高可用 -->
                <property>
                <name>yarn.resourcemanager.ha.enabled</name>
                <value>true</value>
                </property>
                <!-- 指定RM的cluster id -->
                <property>
                <name>yarn.resourcemanager.cluster-id</name>
                <value>yrc</value>
                </property>
                <!-- 指定RM的逻辑名字 -->
                <property>
                <name>yarn.resourcemanager.ha.rm-ids</name>
                <value>rm1,rm2</value>
                </property>
                <!-- 分别指定RM的地址 -->
                <property>
                <name>yarn.resourcemanager.hostname.rm1</name>
                <value>hdp-03</value>
                </property>
                <property>
                <name>yarn.resourcemanager.hostname.rm2</name>
                <value>hdp-04</value>
                </property>
                <!-- 指定zk集群地址 -->
                <property>
                <name>yarn.resourcemanager.zk-address</name>
                <value>hdp-01:2181,hdp-02:2181,hdp-03:2181</value>
                </property>
                <property>
                <name>yarn.nodemanager.aux-services</name>
                <value>mapreduce_shuffle</value>
                </property>
                </configuration>
                
            2.2.6修改slaves(slaves是指定子节点的位置，因为要在hadoop01上启动HDFS、在hadoop03启动yarn，所以hadoop01上的slaves文件指定的是datanode的位置，hadoop03上的slaves文件指定的是nodemanager的位置)
                hadoop05
                hadoop06
                hadoop07
            2.2.7配置免密码登陆
				#首先要配置hadoop00到hadoop01、hadoop02、hadoop03、hadoop04、hadoop05、hadoop06、hadoop07的免密码登陆
				#在hadoop01上生产一对钥匙
				ssh-keygen -t rsa
				#将公钥拷贝到其他节点，包括自己
				ssh-coyp-id hadoop00
				ssh-coyp-id hadoop01
				ssh-coyp-id hadoop02
				ssh-coyp-id hadoop03
				ssh-coyp-id hadoop04
				ssh-coyp-id hadoop05
				ssh-coyp-id hadoop06
				ssh-coyp-id hadoop07
				#配置hadoop02到hadoop04、hadoop05、hadoop06、hadoop07的免密码登陆
				#在hadoop02上生产一对钥匙
				ssh-keygen -t rsa
				#将公钥拷贝到其他节点
				ssh-coyp-id hadoop03				
				ssh-coyp-id hadoop04
				ssh-coyp-id hadoop05
				ssh-coyp-id hadoop06
				ssh-coyp-id hadoop07
				#注意：两个namenode之间要配置ssh免密码登陆，别忘了配置hadoop01到hadoop00的免登陆
				在hadoop01上生产一对钥匙
				ssh-keygen -t rsa
				ssh-coyp-id -i hadoop00	
        ```
    +   2.3将配置好的hadoop拷贝到其他节点
    ```
			scp -r /hadoop/ hadoop02:/
			scp -r /hadoop/ hadoop03:/
			scp -r /hadoop/hadoop-2.6.4/ hadoop@hadoop04:/hadoop/
			scp -r /hadoop/hadoop-2.6.4/ hadoop@hadoop05:/hadoop/
			scp -r /hadoop/hadoop-2.6.4/ hadoop@hadoop06:/hadoop/
			scp -r /hadoop/hadoop-2.6.4/ hadoop@hadoop07:/hadoop/
	```
	
### 3.拷贝文件到其他机器,格式化hdfs文件系统,并启动集群 ###
```
###注意：严格按照下面的步骤和顺序!!
		3.1启动zookeeper集群（分别在hdp-05、hdp-06、hdp-07上启动zk）
			cd /hadoop/zookeeper-3.4.5/bin/
			./zkServer.sh start
			#查看状态：一个leader，两个follower
			./zkServer.sh status
			
		3.2手动启动journalnode（分别在在hdp-05、hdp-06、hdp-07上执行）
			cd /hadoop/hadoop-2.6.4
			sbin/hadoop-daemon.sh start journalnode
			#运行jps命令检验，hadoop05、hadoop06、hadoop07上多了JournalNode进程
		
		3.3格式化namenode
			#在hdp-01上执行命令:
			hdfs namenode -format
			#格式化后会在根据core-site.xml中的hadoop.tmp.dir配置生成个文件，这里我配置的是/hadoop/hadoop-2.6.4/tmp，然后将/hadoop/hadoop-2.6.4/tmp拷贝到hadoop02的/hadoop/hadoop-2.6.4/下。
			scp -r tmp/ hadoop02:/home/hadoop/app/hadoop-2.6.4/
			##也可以这样，建议hdfs namenode -bootstrapStandby
		
		3.4格式化ZKFC(在hdp-01上执行即可)
			hdfs zkfc -formatZK
		
		3.5启动HDFS(在hadoop00上执行)
			sbin/start-dfs.sh

		3.6启动YARN(#####注意#####：是在hadoop02上执行start-yarn.sh，把namenode和resourcemanager分开是因为性能问题，因为他们都要占用大量资源，所以把他们分开了，他们分开了就要分别在不同的机器上启动)
			sbin/start-yarn.sh
			
		3.7验证
    		到此，hadoop-2.6.4配置完毕，可以统计浏览器访问:
        		http://hadoop00:50070
        		NameNode 'hadoop01:9000' (active)
        		http://hadoop01:50070
        		NameNode 'hadoop02:9000' (standby)
    	
        	验证HDFS HA
        		首先向hdfs上传一个文件
        		hadoop fs -put /etc/profile /profile
        		hadoop fs -ls /
        		然后再kill掉active的NameNode
        		kill -9 <pid of NN>
        		通过浏览器访问：http://192.168.1.202:50070
        		NameNode 'hadoop02:9000' (active)
        		这个时候hadoop02上的NameNode变成了active
        		在执行命令：
        		hadoop fs -ls /
        		-rw-r--r--   3 root supergroup       1926 2014-02-06 15:36 /profile
        		刚才上传的文件依然存在！！！
        		手动启动那个挂掉的NameNode
        		sbin/hadoop-daemon.sh start namenode
        		通过浏览器访问：http://192.168.1.201:50070
        		NameNode 'hadoop01:9000' (standby)
        	
        	验证YARN：
        		运行一下hadoop提供的demo中的WordCount程序：
        		hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.4.1.jar wordcount /profile /out
```
### 4.测试集群工作状态的一些指令 ###
```
    bin/hdfs dfsadmin -report	 查看hdfs的各节点状态信息
    
    bin/hdfs haadmin -getServiceState nn1		 获取一个namenode节点的HA状态
    
    sbin/hadoop-daemon.sh start namenode  单独启动一个namenode进程
    
    ./hadoop-daemon.sh start zkfc   单独启动一个zkfc进程
```

# 五.HIVE安装和部署(注意查询hive和hadoop集群的兼容性) #
### 1.前提：mysql安装部署 ###
    ```
    1.1安装
        方法一.直接安装
            mysql apt-get update && apt-get install -y mysql-server
        方法二.使用rmp安装文件安装
            rpm -ivh MySQL-server-5.6.26-1.linux_glibc2.5.x86_64.rpm
            rpm -ivh MySQL-client-5.6.26-1.linux_glibc2.5.x86_64.rpm
            如果有缺工具
                yum list | grep 工具名
                yun apt-get install -y 查询到的工具全名
            如果有冲突
                rpm -e 冲突的lib文件名 --nodeps
    1.2启动和配置权限
    a.启动：service mysql start
    b.查看是否成功:netstat -tap|grep mysql
    c.登录: mysql -u root -p
        密码初始为空
            (如果登录不成功：cat /etc/mysql/debian.cnf得到初始账号和密码再修改)
    d.停止service mysql stop
    e.重启service mysql restart
    f.开放远程登陆权限：
        1.进入mysql客户端mysql -uroot -proot
        2.开放权限(所有用户给'root'@'%'用户权限,identified by 'root'是用户的密码)
            grant all privileges on *.* to 'root'@'%' identified by 'root' with grant option;
        3.使权限生效
            flush privileges;
    ```
### 2.hive安装部署 ###
    ```
    2.1解压
		`tar -zxvf apache-hive-1.2.1-bin.tar.gz -C /data/hive/`
	2.2修改配置文件(创建文件hive-site.xml,配置与mysql连接的密码和位置,mysql在本地机器)
	    vi hive-1.2.1/conf hive-site.xml
	    <property>
			<name>hive.metastore.local</name>
			<value>true</value>
    	</property>
    	<property>
    			<name>javax.jdo.option.ConnectionURL</name>
    			<value>jdbc:mysql://localhost:3306/hive</value>
    	</property>
    	<property>
    			<name>javax.jdo.option.ConnectionDriverName</name>
    			<value>com.mysql.jdbc.Driver</value>
    	</property>
    	<property>
    			<name>javax.jdo.option.ConnectionUserName</name>
    			<value>root</value>
    	</property>
    	<property>
    			<name>javax.jdo.option.ConnectionPassword</name>
    			<value>root</value>
    	</property>
    2.3添加mysql连接jar包到hive-1.2.1/lib
        mysql-connector-java-5.1.37.jar
    2.4添加hdfs集群的hive配置
        hadoop-->core-site.xml补充一下配置 (root用户组，其他用户组就替换root)
            <property>
                 <name>hadoop.proxyuser.root.groups</name>
                 <value>*</value>
            </property>
            <property>
                 <name>hadoop.proxyuser.root.hosts</name>
                 <value>*</value>
            </property>
    2.5添加环境变量
        vi /conf/hive-env.sh
            HADOOP_HOME=/data/hadoop/hadoop-2.6.5
        vi /etc/profile
            export HIVE_HOME=/data/hadoop/hive-1.2.1
            export PATH=$PATH:${JAVA_PATH}:${HADOOP_HOME}/bin:${HIVE_HOME}/bin:${HIVE_HOME}/conf
        
    2.6.hive启动 
        一.直接启动:/bin/hive
        二.服务端和客户端分开食用
            a.启动服务端：/bin/hiveserver2
        	b.启动客户端：/bin/beeline
        	  连接服务端: !connect jdbc:hive2://hadoop001:10000
        	  然后输入配置的用户组用户和密码
        	c.直接连接：bin/beeline -u jdbc:hive2://hadoop001:10000 -n root
    2.7.hive脚本执行sql(直接在外部主机上运行)
        hive -e "create table xx(sex string,count int)"   直接运行sql语句
        hive -f test.hql       直接运行sql文件
    2.8.hive默认在hdfs中的库目录
        hdfs://hadoop001:9000/user/hive/warehouse/
    ```      
    
# 六.HBase集群搭建 #
### 1.前提 ### 
 +	首先，要有一个HDFS集群，并正常运行;
    regionserver应该跟hdfs中的datanode在一起
 +  其次，还需要一个zookeeper集群，并正常运行
    然后，安装HBASE
    角色分配如下：
    
    Hdp01:  namenode  datanode  regionserver  hmaster  zookeeper
    
    Hdp02:  datanode   regionserver  zookeeper
    
    Hdp03:  datanode   regionserver  zookeeper

### 2.安装步骤 ###    
    ```
    2.1解压hbase安装包
    2.2修改hbase-env.sh
        vi hbase-env.sh
        export JAVA_HOME=/root/apps/jdk1.7.0_67
        export HBASE_MANAGES_ZK=false  #禁止默认的zookeeper
    2.3修改hbase-site.xml
        vi hbase-site.xml
        <configuration>
    		<!-- 指定hbase在HDFS上存储的路径 -->
            <property>
                    <name>hbase.rootdir</name>
                    <value>hdfs://hdp01:9000/hbase</value>
            </property>
    		<!-- 指定hbase是分布式的 -->
            <property>
                    <name>hbase.cluster.distributed</name>
                    <value>true</value>
            </property>
    		<!-- 指定zk的地址，多个用“,”分割 -->
            <property>
                    <name>hbase.zookeeper.quorum</name>
                    <value>hdp01:2181,hdp02:2181,hdp03:2181</value>
            </property>
    	</configuration>
    2.4修改 regionservers
        vi regionservers
            hdp01
            hdp02
            hdp03
    ```
### 3.启动hbase集群 ### 
    `bin/start-hbase.sh`
    启动完后，还可以在集群中找任意一台机器启动一个备用的master
    `bin/hbase-daemon.sh start master`
    新启的这个master会处于backup状态
### 4.启动hbase的命令行客户端 ### 
```
    bin/hbase shell
    Hbase> list     // 查看表
    Hbase> status   // 查看集群状态
    Hbase> version  // 查看集群版本
```

# 七.Flume的安装部署 #
+ 1.Flume的安装非常简单，只需要解压即可，当然，前提是已有hadoop环境
上传安装包到数据源所在节点上
```
    a.解压  tar -zxvf apache-flume-1.6.0-bin.tar.gz
    b.然后进入flume的目录，修改conf下的flume-env.sh，在里面配置JAVA_HOME
    c.添加path
    vi /etc/profile
        export SQOOP_HOME=/data/sqoop-1.4.6
        export PATH=$PATH:${JAVA_PATH}:${HADOOP_HOME}/bin:${HIVE_HOME}/bin:${HIVE_HOME}/conf:${SQOOP_HOME}/bin
```
+ 2.Flume的运行
```
    1、先在flume的conf目录下新建一个配置文件（采集方案）
        vi netcat-logger.properties
            # 定义这个agent中各组件的名字
            a1.sources = r1
            a1.sinks = k1
            a1.channels = c1
            
            # 描述和配置source组件：r1
            a1.sources.r1.type = netcat
            a1.sources.r1.bind = localhost
            a1.sources.r1.port = 44444
            
            # 描述和配置sink组件：k1
            a1.sinks.k1.type = logger
            
            # 描述和配置channel组件，此处使用是内存缓存的方式
            a1.channels.c1.type = memory
            a1.channels.c1.capacity = 1000
            a1.channels.c1.transactionCapacity = 100
            
            # 描述和配置source  channel   sink之间的连接关系
            a1.sources.r1.channels = c1
            a1.sinks.k1.channel = c1
    2、启动agent去采集数据
        bin/flume-ng agent -c conf -f conf/netcat-logger.conf -n a1  -Dflume.root.logger=INFO,console
        -c conf   指定flume自身的配置文件所在目录
        -f conf/netcat-logger.con  指定我们所描述的采集方案
        -n a1  指定我们这个agent的名字
```