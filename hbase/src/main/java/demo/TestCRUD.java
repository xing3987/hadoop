package demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class TestCRUD {
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
        TableName tableName = TableName.valueOf("t1"); //如果写成t1则ns1是数据库名字
        //创建表描述符对象
        HTableDescriptor tbl = new HTableDescriptor(tableName);

        //创建列族描述符
        HColumnDescriptor col = new HColumnDescriptor("f1");
        HColumnDescriptor col2 = new HColumnDescriptor("f2");
        //保留删除的cell
        col.setKeepDeletedCells(true);
        //col.setTimeToLive(20); //设定数据保存的时间
        tbl.addFamily(col);
        tbl.addFamily(col2);
        admin.createTable(tbl);
    }

    @Test
    public void disableTable() throws Exception {
        //禁用表 enable(...) disableTable(...)
        admin.disableTable(TableName.valueOf("t1"));
        admin.deleteTable(TableName.valueOf("t1"));
    }

    @Test
    public void put() throws Exception {

        //通过连接查询tableName对象
        TableName tname = TableName.valueOf("t1");
        //获得table
        Table table = conn.getTable(tname);

        //通过bytes工具类创建字节数组(将字符串)
        byte[] rowid = Bytes.toBytes("row3");

        //创建put对象
        Put put = new Put(rowid);

        byte[] f1 = Bytes.toBytes("f1");
        byte[] id = Bytes.toBytes("id");
        byte[] value = Bytes.toBytes(102);
        put.addColumn(f1, id, value);

        //执行插入
        table.put(put);
    }

    @Test
    public void bigInsert() throws Exception {

        DecimalFormat format = new DecimalFormat();
        format.applyPattern("0000");

        long start = System.currentTimeMillis();
        TableName tname = TableName.valueOf("t1");
        HTable table = (HTable) conn.getTable(tname);
        //不要自动清理缓冲区
        table.setAutoFlush(false);

        Random random = new Random();
        for (int i = 1; i < 10000; i++) {
            Put put = new Put(Bytes.toBytes("row" + format.format(i)));
            //关闭写前日志
            put.setWriteToWAL(false);
            put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("id"), Bytes.toBytes(i));
            put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("name"), Bytes.toBytes("tom" + i));
            put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("age"), Bytes.toBytes(random.nextInt(i) % 100));

            put.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("id"), Bytes.toBytes(i));
            put.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("name"), Bytes.toBytes("tom" + i));
            put.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("age"), Bytes.toBytes(random.nextInt(i) % 100));
            table.put(put);

            if (i % 2000 == 0) {
                table.flushCommits();
            }
        }
        //
        table.flushCommits();
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void get() throws Exception {
        //通过连接查询tableName对象
        TableName tname = TableName.valueOf("t1");
        //获得table
        Table table = conn.getTable(tname);

        //通过bytes工具类创建字节数组(将字符串)
        Get get = new Get(Bytes.toBytes("row0003"));
        Result r = table.get(get);
        byte[] idvalue = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
        byte[] namevalue = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
        System.out.println(Bytes.toInt(idvalue) + ":" + new String(namevalue));
    }

    @Test
    public void createNameSpace() throws Exception {
        Admin admin = conn.getAdmin();
        //创建名字空间描述符(创建数据库)
        NamespaceDescriptor nsd = NamespaceDescriptor.create("ns2").build();
        admin.createNamespace(nsd);

        NamespaceDescriptor[] ns = admin.listNamespaceDescriptors();
        for (NamespaceDescriptor n : ns) {
            System.out.println(n.getName());
        }
    }

    @Test
    public void listNameSpaces() throws Exception {
        Admin admin = conn.getAdmin();

        NamespaceDescriptor[] ns = admin.listNamespaceDescriptors();
        for (NamespaceDescriptor n : ns) {
            System.out.println(n.getName());
        }
    }


    @Test
    public void deleteData() throws IOException {
        TableName tname = TableName.valueOf("t1");

        Table table = conn.getTable(tname);
        Delete del = new Delete(Bytes.toBytes("row0001"));
        del.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("id"));
        del.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("name"));
        table.delete(del);
        System.out.println("over");
    }

    /**
     * 删除数据
     */
    @Test
    public void scan() throws IOException {

        TableName tname = TableName.valueOf("t1");
        Table table = conn.getTable(tname);
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes("row5000"));
        scan.setStopRow(Bytes.toBytes("row8000"));
        ResultScanner rs = table.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            System.out.println(Bytes.toString(name));
        }
    }


    /**
     * 动态遍历1
     */
    @Test
    public void scan3() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Table table = conn.getTable(tname);
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes("row7000"));
        scan.setStopRow(Bytes.toBytes("row7100"));
        ResultScanner rs = table.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            //得到一行的所有map,key=f1,value=Map<Col,Map<Timestamp,value>>
            NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = r.getMap();
            //
            for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : map.entrySet()) {
                //得到列族
                String f = Bytes.toString(entry.getKey());
                Map<byte[], NavigableMap<Long, byte[]>> colDataMap = entry.getValue();
                for (Map.Entry<byte[], NavigableMap<Long, byte[]>> ets : colDataMap.entrySet()) {
                    String c = Bytes.toString(ets.getKey());
                    Map<Long, byte[]> tsValueMap = ets.getValue();
                    for (Map.Entry<Long, byte[]> e : tsValueMap.entrySet()) {
                        Long ts = e.getKey();
                        String value = Bytes.toString(e.getValue());
                        System.out.print(f + ":" + c + ":" + ts + "=" + value + ",");
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * 动态遍历2
     *
     * @throws Exception
     */
    @Test
    public void testScan() throws Exception {

        Table table = conn.getTable(TableName.valueOf("t1"));

        // 包含起始行键，不包含结束行键,但是如果真的想查询出末尾的那个行键，那么，可以在末尾行键上拼接一个不可见的字节（\000）
        Scan scan = new Scan("row7000".getBytes(), "row7100".getBytes());
        ResultScanner scanner = table.getScanner(scan);
        Iterator<Result> iterator = scanner.iterator();
        while (iterator.hasNext()) {

            Result result = iterator.next();
            // 遍历整行结果中的所有kv单元格
            CellScanner cellScanner = result.cellScanner();
            while (cellScanner.advance()) {
                Cell cell = cellScanner.current();

                byte[] rowArray = cell.getRowArray();  //本kv所属的行键的字节数组
                byte[] familyArray = cell.getFamilyArray();  //列族名的字节数组
                byte[] qualifierArray = cell.getQualifierArray();  //列名的字节数据
                byte[] valueArray = cell.getValueArray(); // value的字节数组

                System.out.println("行键: " + new String(rowArray, cell.getRowOffset(), cell.getRowLength()));
                System.out.println("列族名: " + new String(familyArray, cell.getFamilyOffset(), cell.getFamilyLength()));
                System.out.println("列名: " + new String(qualifierArray, cell.getQualifierOffset(), cell.getQualifierLength()));
                System.out.println("value: " + new String(valueArray, cell.getValueOffset(), cell.getValueLength()));
            }
            System.out.println("----------------------");
        }
    }

    /**
     * 按照指定版本数查询
     *
     * @throws IOException
     */
    @Test
    public void getWithVersion() throws IOException {
        TableName tableName = TableName.valueOf("t1");
        Table table = conn.getTable(tableName);
        Get get = new Get(Bytes.toBytes("row0001"));
        //检索所有版本
        get.setMaxVersions();

        Result result = table.get(get);
        List<Cell> columnCells = result.getColumnCells(Bytes.toBytes("f1"), Bytes.toBytes("name"));
        for (Cell c : columnCells) {
            String f = Bytes.toString(c.getFamily());
            String col = Bytes.toString(c.getQualifier());
            long ts = c.getTimestamp();
            String val = Bytes.toString(c.getValue());
            System.out.println(f + "/" + col + "/" + ts + "=" + val);
        }
    }

    /**
     * 使用缓存查询
     *
     * @throws Exception
     */
    @Test
    public void getScanCache() throws IOException {

        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();
        scan.setCaching(5000);//5000个一组处理
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        long start = System.currentTimeMillis();
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            System.out.println(r.getColumnLatestCell(Bytes.toBytes("f1"), Bytes.toBytes("name")));
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    /**
     * 测试缓存和批处理
     */
    @Test
    public void testBatchAndCaching() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes("row9900"));
        scan.setCaching(2);//设置一次处理行数
        scan.setBatch(2);//设置一次处理列数，一对k-v为一列
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();//一次列处理返回一个result
        while (it.hasNext()) {
            Result r = it.next();
            System.out.println("========================================");
            //得到一行的所有map,key=f1,value=Map<Col,Map<Timestamp,value>>
            NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = r.getMap();
            //
            for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : map.entrySet()) {
                //得到列族
                String f = Bytes.toString(entry.getKey());
                Map<byte[], NavigableMap<Long, byte[]>> colDataMap = entry.getValue();
                for (Map.Entry<byte[], NavigableMap<Long, byte[]>> ets : colDataMap.entrySet()) {
                    String c = Bytes.toString(ets.getKey());
                    Map<Long, byte[]> tsValueMap = ets.getValue();
                    for (Map.Entry<Long, byte[]> e : tsValueMap.entrySet()) {
                        Long ts = e.getKey();
                        String value = Bytes.toString(e.getValue());
                        System.out.print(f + "/" + c + "/" + ts + "=" + value + ",");
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * 测试RowFilter过滤器
     */
    @Test
    public void testRowFilter() throws IOException {

        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();
        //rowkey小于等于
        RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("row0100")));
        scan.setFilter(rowFilter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            System.out.println(Bytes.toString(r.getRow()));
        }
    }

    /**
     * 测试FamilyFilter过滤器,过滤族键
     */
    @Test
    public void testFamilyFilter() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes("row9900"));
        FamilyFilter filter = new FamilyFilter(CompareFilter.CompareOp.LESS, new BinaryComparator(Bytes.toBytes("f2")));
        scan.setFilter(filter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            System.out.println(new String(f1id));
        }
    }

    /**
     * 测试QualifierFilter(列过滤器)
     */
    @Test
    public void testColFilter() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();
        //只能查询出符合要求的列
        QualifierFilter colfilter = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("name")));
        scan.setFilter(colfilter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
            byte[] f1age = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("age"));
            byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            System.out.println(f1id + " : " + f1age + " : " + f1name);
            //System.out.println(Bytes.toInt(f1id) + " : " + Bytes.toInt(f1age) + " : " + Bytes.toString(f1name));
        }
    }

    /**
     * 依赖列过滤器
     */
    @Test
    public void testDepFilter() throws IOException {

        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();
        DependentColumnFilter filter = new DependentColumnFilter(Bytes.toBytes("f1"),
                Bytes.toBytes("name"),
                false,
                CompareFilter.CompareOp.EQUAL,
                new BinaryComparator(Bytes.toBytes("tom0003"))
        );

        scan.setFilter(filter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
            byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            System.out.println(f1id + " : " + Bytes.toString(f1name));
        }
    }

    /**
     * 测试ValueFilter(值过滤器)
     * 过滤value的值，含有指定的字符子串
     */
    @Test
    public void testValueFilter() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();
        //指定值等于 以“tom99”开头
        //ValueFilter filter = new ValueFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("tom99"));

        //指定值大于或等于 “tom99”
        ValueFilter filter = new ValueFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes("tom99")));
        scan.setFilter(filter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
            byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            System.out.println(f1id + " : " + Bytes.toString(f1name));
        }
    }

    /**
     * 单列值排除过滤器：单列的值不符合条件，整行将不显示,同时结果集中不含指定的列
     */
    @Test
    public void testSingleColumValueExcludeFilter() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();
        //scan.setStartRow(Bytes.toBytes("row9900"));
        SingleColumnValueExcludeFilter filter = new SingleColumnValueExcludeFilter(Bytes.toBytes("f1"),
                Bytes.toBytes("name"),//name列的值不显示
                CompareFilter.CompareOp.EQUAL,
                new BinaryComparator(Bytes.toBytes("tom9998")));

        scan.setFilter(filter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
            byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            System.out.println(Bytes.toInt(f1id) + " : " + Bytes.toString(f1name));
        }
    }

    /**
     * 前缀过滤,是rowkey过滤. where rowkey like 'row22%'
     */
    @Test
    public void testPrefixFilter() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();
        PrefixFilter filter = new PrefixFilter(Bytes.toBytes("row222"));

        scan.setFilter(filter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
            byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
            byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
            System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
        }
    }


    /**
     * 分页过滤,是rowkey过滤,在region上扫描时，对每次page设置的大小。
     * 返回到到client，设计到每个Region结果的合并。
     */
    @Test
    public void testPageFilter() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();

        PageFilter filter = new PageFilter(10);

        scan.setFilter(filter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
            byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
            byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
            System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
        }
    }


    /**
     * keyOnly过滤器，只提取key,丢弃value.
     *
     * @throws IOException
     */
    @Test
    public void testKeyOnlyFilter() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();

        KeyOnlyFilter filter = new KeyOnlyFilter();

        scan.setFilter(filter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
            byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
            byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
            System.out.println(Bytes.toInt(f1id) + " : " + Bytes.toInt(f2id) + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
        }
    }

    /**
     * ColumnPageFilter,列分页过滤器，过滤指定范围列，
     * select ,a,b from t1
     */
    @Test
    public void testColumnPageFilter() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes("row9900"));
        ColumnPaginationFilter filter = new ColumnPaginationFilter(1, 2);

        scan.setFilter(filter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
            byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
            byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
            System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
        }
    }


    /**
     * 正则表达式对比器 like查询RegexStringComparator
     */
    @Test
    public void testLike() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();

        ValueFilter filter = new ValueFilter(CompareFilter.CompareOp.EQUAL,
                new RegexStringComparator("^tom2")
        );

        scan.setFilter(filter);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
            byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
            byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
            System.out.println(f1id + " : " + f2id + " : " + Bytes.toString(f1name) + " : " + Bytes.toString(f2name));
        }
    }


    /**
     * 测试一些查询
     * select * from t1 where f1:age <= 13 and f1:name like %tom1
     * or (f2:age > 30 and f2:name like %t)
     *
     * @throws IOException
     */
    @Test
    public void testComboFilter() throws IOException {

        TableName tname = TableName.valueOf("t1");
        Scan scan = new Scan();

        //where ... f1:age <= 13
        SingleColumnValueFilter ftl = new SingleColumnValueFilter(
                Bytes.toBytes("f1"),
                Bytes.toBytes("age"),
                CompareFilter.CompareOp.LESS_OR_EQUAL,
                new BinaryComparator(Bytes.toBytes(10))
        );

        //where ... f1:name like %t
        SingleColumnValueFilter ftr = new SingleColumnValueFilter(
                Bytes.toBytes("f1"),
                Bytes.toBytes("name"),
                CompareFilter.CompareOp.EQUAL,
                new RegexStringComparator("^tom1")
        );
        //ft
        FilterList ft = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        ft.addFilter(ftl);
        ft.addFilter(ftr);

        //where ... f2:age > 30
        SingleColumnValueFilter fbl = new SingleColumnValueFilter(
                Bytes.toBytes("f2"),
                Bytes.toBytes("age"),
                CompareFilter.CompareOp.GREATER,
                new BinaryComparator(Bytes.toBytes(30))
        );

        //where ... f2:name like %t
        SingleColumnValueFilter fbr = new SingleColumnValueFilter(
                Bytes.toBytes("f2"),
                Bytes.toBytes("name"),
                CompareFilter.CompareOp.EQUAL,
                new RegexStringComparator("t$")
        );
        //ft
        FilterList fb = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        fb.addFilter(fbl);
        fb.addFilter(fbr);


        FilterList fall = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        fall.addFilter(ft);
        fall.addFilter(fb);

        scan.setFilter(fall);
        Table t = conn.getTable(tname);
        ResultScanner rs = t.getScanner(scan);
        Iterator<Result> it = rs.iterator();
        while (it.hasNext()) {
            Result r = it.next();
            byte[] f1id = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("id"));
            byte[] f2id = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
            byte[] f1name = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
            byte[] f2name = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("name"));
            byte[] age1 = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("age"));
            byte[] age2 = r.getValue(Bytes.toBytes("f2"), Bytes.toBytes("age"));
            System.out.println(f1id + " : " + Bytes.toString(f1name) + " : " + Bytes.toInt(age1) + ";"
                    + f2id + " : " + Bytes.toString(f2name) + " : " + Bytes.toInt(age2));
        }
    }

    /**
     * 测试计数器,系统默认带有加锁机制，可以用于并发的计数，保证数据的准确(默认值为Long类型)
     */
    @Test
    public void testIncr() throws IOException {
        TableName tname = TableName.valueOf("t1");
        Table t = conn.getTable(tname);
        //给计数器赋值，增加数值
        Increment incr = new Increment(Bytes.toBytes("row88888"));
        incr.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("daily"), 1);
        incr.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("weekly"), 10);
        incr.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("monthly"), 100);
        t.increment(incr);

        //通过bytes工具类创建字节数组(将字符串)
        Get get = new Get(Bytes.toBytes("row88888"));
        Result r = t.get(get);
        byte[] daily = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("daily"));
        byte[] weekly = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("weekly"));
        byte[] monthly = r.getValue(Bytes.toBytes("f1"), Bytes.toBytes("monthly"));
        System.out.println(Bytes.toLong(daily) + ":" + Bytes.toLong(weekly) + ":" + Bytes.toLong(monthly));
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
