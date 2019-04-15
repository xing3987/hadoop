package demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    @Test
    public void put() throws Exception{
        Table table=conn.getTable(TableName.valueOf("user_info"));// 获取一个操作指定表的table对象,进行DML操作
        Put put=new Put(Bytes.toBytes("001"));// 构造要插入的数据为一个Put类型(一个put对象只能对应一个rowkey)的对象
        put.addColumn(Bytes.toBytes("base_info"),Bytes.toBytes("username"),Bytes.toBytes("张三"));
        put.addColumn(Bytes.toBytes("base_info"),Bytes.toBytes("age"),Bytes.toBytes("18"));
        put.addColumn(Bytes.toBytes("extra_info"),Bytes.toBytes("addr"),Bytes.toBytes("上海"));

        Put put2=new Put(Bytes.toBytes("002"));// 构造要插入的数据为一个Put类型(一个put对象只能对应一个rowkey)的对象
        put.addColumn(Bytes.toBytes("base_info"),Bytes.toBytes("username"),Bytes.toBytes("李四"));
        put.addColumn(Bytes.toBytes("base_info"),Bytes.toBytes("age"),Bytes.toBytes("22"));
        put.addColumn(Bytes.toBytes("extra_info"),Bytes.toBytes("addr"),Bytes.toBytes("天津"));

        List<Put> puts=new ArrayList<>();
        puts.add(put);
        puts.add(put2);

        table.put(puts);//插入数据
        table.close();
    }


    /**
     * 循环插入大量数据
     * @throws Exception
     */
    @Test
    public void testManyPuts() throws Exception{

        Table table = conn.getTable(TableName.valueOf("user_info"));
        ArrayList<Put> puts = new ArrayList<>();

        for(int i=0;i<100000;i++){
            Put put = new Put(Bytes.toBytes(""+i));
            put.addColumn(Bytes.toBytes("base_info"), Bytes.toBytes("username"), Bytes.toBytes("张三"+i));
            put.addColumn(Bytes.toBytes("base_info"), Bytes.toBytes("age"), Bytes.toBytes((18+i)+""));
            put.addColumn(Bytes.toBytes("extra_info"), Bytes.toBytes("addr"), Bytes.toBytes("北京"));

            puts.add(put);
        }

        table.put(puts);

    }


    @Test
    public void delete() throws Exception{
        Table table = conn.getTable(TableName.valueOf("user_info"));

        Delete delete=new Delete(Bytes.toBytes("001"));// 构造一个对象封装要删除的数据信息
        Delete delete2 = new Delete(Bytes.toBytes("002"));
        delete2.addColumn(Bytes.toBytes("extra_info"), Bytes.toBytes("addr"));

        ArrayList<Delete> dels = new ArrayList<>();
        dels.add(delete);
        dels.add(delete2);

        table.delete(dels);
        table.close();
    }

    /**
     * 查
     * @throws Exception
     */
    @Test
    public void get() throws Exception{
        Table table = conn.getTable(TableName.valueOf("user_info"));
        Get get=new Get(Bytes.toBytes("002"));
        Result result=table.get(get);
        byte[] value=result.getValue(Bytes.toBytes("base_info"),Bytes.toBytes("age"));// 从结果中取用户指定的某个key的value
        System.out.println(new String(value));

        // 遍历整行结果中的所有kv单元格
        CellScanner scanner=result.cellScanner();
        while (scanner.advance()){
            Cell cell=scanner.current();
            byte[] rowArray=cell.getRowArray();
            byte[] familyArray=cell.getFamilyArray();
            byte[] qualifierArray=cell.getQualifierArray();
            byte[] valueArray=cell.getValueArray();
            System.out.println("行键: "+new String(rowArray,cell.getRowOffset(),cell.getRowLength()));
            System.out.println("列族名: "+new String(familyArray,cell.getFamilyOffset(),cell.getFamilyLength()));
            System.out.println("列名: "+new String(qualifierArray,cell.getQualifierOffset(),cell.getQualifierLength()));
            System.out.println("value: "+new String(valueArray,cell.getValueOffset(),cell.getValueLength()));

        }
    }

    /**
     * 按行键范围查询数据
     * @throws Exception
     */
    @Test
    public void testScan() throws Exception{

        Table table = conn.getTable(TableName.valueOf("user_info"));

        // 包含起始行键，不包含结束行键,但是如果真的想查询出末尾的那个行键，那么，可以在末尾行键上拼接一个不可见的字节（\000）
        //Scan scan = new Scan("10".getBytes(), "10000".getBytes());
        Scan scan = new Scan("10".getBytes(), "10000\001".getBytes());

        ResultScanner scanner = table.getScanner(scan);

        Iterator<Result> iterator = scanner.iterator();

        while(iterator.hasNext()){

            Result result = iterator.next();
            // 遍历整行结果中的所有kv单元格
            CellScanner cellScanner = result.cellScanner();
            while(cellScanner.advance()){
                Cell cell = cellScanner.current();

                byte[] rowArray = cell.getRowArray();  //本kv所属的行键的字节数组
                byte[] familyArray = cell.getFamilyArray();  //列族名的字节数组
                byte[] qualifierArray = cell.getQualifierArray();  //列名的字节数据
                byte[] valueArray = cell.getValueArray(); // value的字节数组

                System.out.println("行键: "+new String(rowArray,cell.getRowOffset(),cell.getRowLength()));
                System.out.println("列族名: "+new String(familyArray,cell.getFamilyOffset(),cell.getFamilyLength()));
                System.out.println("列名: "+new String(qualifierArray,cell.getQualifierOffset(),cell.getQualifierLength()));
                System.out.println("value: "+new String(valueArray,cell.getValueOffset(),cell.getValueLength()));
            }
            System.out.println("----------------------");
        }
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
