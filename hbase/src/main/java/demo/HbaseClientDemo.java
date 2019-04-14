package demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HbaseClientDemo {
    Connection conn = null;
    Admin admin = null;

    @Before
    public void getConn() throws Exception {
        Configuration conf = HBaseConfiguration.create();//创建hbase的配置文件
        conf.set("hbase.zookeeper.quorum", "hadoop001:2181,hadoop002:2181,hadoop003:2181");
        conn = ConnectionFactory.createConnection(conf); //使用factory创建连接
        admin = conn.getAdmin();
    }

    @Test
    public void createTable() throws Exception {
        Admin admin = conn.getAdmin();//从连接中构造一个ddl操作器
        //创建一个表定义描述对象
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf("user_info"));
        //创建列族定义描述对象
        HColumnDescriptor base_info = new HColumnDescriptor("base_info");
        base_info.setMaxVersions(3);//设置数据保留的版本数（可以不设定）
        HColumnDescriptor extra_info = new HColumnDescriptor("extra_info");
        //将列族定义信息对象放入表定义对象中
        hTableDescriptor.addFamily(base_info);
        hTableDescriptor.addFamily(extra_info);
        //建表
        admin.createTable(hTableDescriptor);
    }

    @Test
    public void alterTable() throws Exception{
        HTableDescriptor tableDescriptor=admin.getTableDescriptor(TableName.valueOf("user_info"));//得到表对象
        HColumnDescriptor hColumnDescriptor=new HColumnDescriptor("other_info");//创建一个新的列族
        hColumnDescriptor.setBloomFilterType(BloomType.ROWCOL);//设置该列族的布隆过滤器，用来查找数据
        tableDescriptor.addFamily(hColumnDescriptor);//添加新的列族到表中
        admin.modifyTable(TableName.valueOf("user_info"),tableDescriptor);//将修改过的表对象提交给admin处理
    }

    @Test
    public void dropTable() throws Exception {
        //停用表
        admin.disableTable(TableName.valueOf("user_info"));

        //删除表
        admin.deleteTable(TableName.valueOf("user_info"));
    }

    @After
    public void close() throws Exception {
        //关闭连接
        if (conn != null) {
            conn.close();
        }
        if (admin != null) {
            admin.close();
        }
    }
}
