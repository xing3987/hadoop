
# java环境搭建 #
## 1.linux配置java开发环境 ##
    a.去官网下载对于linux版本的jdk-*-linux.tar.gz压缩包
    
    b.在usr中创建java文件夹,并把安装包考入该文件夹(注意考入时开放导入权限 `sudo chmod 644 ./java `)
    
    c.解压：`tar -xzvf jdk-*-linux.tar.gz`
    
    d.配置环境变量
        `sudo vim /etc/profile`
         在文件中添加
        ``` 
             export JAVA_HOME=/usr/java/jdk1.8.0_191
             export JRE_HOME=${JAVA_HOME}/jre
             export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:$CLASSPATH
             export JAVA_PATH=${JAVA_HOME}/bin:${JRE_HOME}/bin
             export PATH=$PATH:${JAVA_PATH}
        ```
         保存并使文件生效
            `source /etc/profile让profile`
            
    e.测试安装是否成功
        1.使用javac命令，不会出现command not found错误
        2.使用java -version
        3.echo $PATH  打印输出,看看自己刚刚设置的的环境变量配置是否都正确

## 2.设置ssh免密码登陆
        a.安装ssh  `apt-get install -y ssh`
        b.创建文件夹:  `ls -a /home/[currentuser]/.ssh`
        c.在文件夹中生成密钥: `ssh-keygen -t dsa -P '' -f .ssh/id_dsa`
        d.把公钥加到当前用户: `cat .ssh/id_dsa.pub >> .ssh/authorized_keys` (注意这里添加的是当前用户，如果需要root用户要先切换到root用户)
            （也可以在主用户上使用`ssh-copy-id root@hadoop002` 这样的命令实现把公钥加到其他用户中，实现免密登陆其他用户）
        e.修改配置文件：`sudo vim  /etc/ssh/sshd_config`
        ```
            PubkeyAuthentication yes
            RSAAuthentication yes
            AuthorizedKeysFile %h/.ssh/authorized_keys
        ```
        f.重启服务器ssh localhost如果还是不行
            设置权限：
            ```
            sudo chmod 700 ~/.ssh
            sudo  chmod 600 ~/.ssh/authorized_keys
            ```
        g.`sudo service ssh restart`重启服务器
        
        h.免密登陆其他服务器
            有机器A(192.168.1.155)，B(192.168.1.181)。现想A通过ssh免密码登录到B。
            1. 把A机下的id_rsa.pub复制到B机下，在B机的.ssh/authorized_keys文件里，我用scp复制。
            ```
                [hadoop001@A ~]$ scp .ssh/id_rsa.pub hadoop002@192.168.1.181:/home/hadoop002/id_rsa.pub
                hadoop002@192.168.1.181's password:
                id_rsa.pub                                    100%  223     0.2KB/s   00:00
            ```
                由于还没有免密码登录的，所以要输入密码。
            
            2. B机把从A机复制的id_rsa.pub添加到.ssh/authorzied_keys文件里。
            ```
                [hadoop002@B ~]$ cat id_rsa.pub >> .ssh/authorized_keys
                [hadoop002@B ~]$ chmod 600 .ssh/authorized_keys
            ```
                authorized_keys的权限最低要是600。
                
            3.测试A机器登陆B机器:  ssh hadoop002@hadoop002
            
            4.测试复制文件夹到远程机器
               `scp -r /home/administrator/test/ root@192.168.1.100:/root/`
        
            5.如果需要root用户免密登陆，可以把密钥生成到/root/.ssh下
    
    