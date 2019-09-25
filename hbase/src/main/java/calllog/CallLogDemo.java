package calllog;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 通话日志，主键设计
 */
public class CallLogDemo {
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
        //创建表名对象
        TableName tableName = TableName.valueOf("calllogs"); //如果写成t1则ns1是数据库名字
        //创建表描述符对象
        HTableDescriptor tbl = new HTableDescriptor(tableName);

        //创建列族描述符
        HColumnDescriptor col = new HColumnDescriptor("f1");
        //保留删除的cell
        col.setKeepDeletedCells(true);
        //col.setTimeToLive(20); //设定数据保存的时间
        tbl.addFamily(col);
        admin.createTable(tbl);
    }

    @Test
    public void put() throws Exception {
        TableName tableName = TableName.valueOf("calllogs");
        Table table = conn.getTable(tableName);

        //设计主叫的rowkey
        //xx , callerid , time ,  direction（方向0,1用于区别主被叫）, calleid  ,duration（时长）
        //被叫:calleid,time,


        String callerId = "13877777777";//11位
        String calleeId = "13866666666";

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//14位
        String callTime = df.format(new Date());

        DecimalFormat def = new DecimalFormat("0000000");//通话时长7位数（秒）
        String duration = def.format(1000);

        //区域00-99
        String regNo = Util.getRegNo(callerId, callTime);

        //2+1+11+1+14+3+11+1+7=48长度
        String rowkey = regNo + "," + callerId + "," + callTime + "," + "0" + "," + calleeId + "," + duration;
        byte[] rowid = Bytes.toBytes(rowkey);
        Put put=new Put(rowid);
        put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("callerPos"),Bytes.toBytes("北京"));
        put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("calleePos"),Bytes.toBytes("上海"));
        table.put(put);
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
