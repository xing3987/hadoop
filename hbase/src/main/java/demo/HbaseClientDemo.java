package demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.junit.Before;
import org.junit.Test;

public class HbaseClientDemo {
    Connection conn=null;

    @Before
    public void getConn() throws Exception{
        Configuration conf= HBaseConfiguration.create();//创建hbase的配置文件
        conf.set("hbase.zookeeper.quorum","hadoop001:2181,hadoop002:2181,hadoop003:2181");
        conn= ConnectionFactory.createConnection(conf); //使用factory创建连接
    }

    @Test
    public void dropTable() throws Exception{
        Admin admin=conn.getAdmin();

        //停用表
        admin.disableTable(TableName.valueOf("user_info"));

        //删除表
        admin.deleteTable(TableName.valueOf("user_info"));

        admin.close();
        conn.close();
    }
}
