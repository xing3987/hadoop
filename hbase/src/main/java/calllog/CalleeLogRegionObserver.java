package calllog;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 自定义区域观察者
 */
public class CalleeLogRegionObserver extends BaseRegionObserver {

    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        super.postPut(e, put, edit, durability);

        FileWriter fw = new FileWriter("/home/centos/kkk.txt", true);
        //指定要监视的tablename
        String tableName = TableName.valueOf("calllogs").getNameAsString();
        //获取当地操作的tablename
        String currentTable = e.getEnvironment().getRegion().getRegionInfo().getTable().getNameAsString();
        fw.write(currentTable + "\r\n");
        if (!tableName.equals(currentTable)) {
            return;
        }

        //得到主叫的rowkey
        //xx , callerid , time ,  direction, calleeid  ,duration
        //被叫:calleid,time,

        String rowkey = Bytes.toString(put.getRow());
        String[] arr = rowkey.split(",");
        //如果是被叫号码则忽略
        if (arr[3].equals("1")) {
            return;
        }
        //把被叫的号码和时间计算hash,作为主键的hash区号
        String hash = Util.getRegNo(arr[4], arr[2]);
        String newRowKey = hash + "," + arr[4] + "," + arr[2] + ",1," + arr[1] + "," + arr[5];
        Put newPut = new Put(Bytes.toBytes(newRowKey));

        //得到主叫和被叫地址
        List<Cell> calleePos = put.get(Bytes.toBytes("f1"), Bytes.toBytes("callerPos"));
        List<Cell> callerPos = put.get(Bytes.toBytes("f1"), Bytes.toBytes("calleePos"));

        put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("callerPos"), callerPos.get(0).getValueArray());
        put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("calleePos"), calleePos.get(0).getValueArray());
        Table t = e.getEnvironment().getTable(TableName.valueOf("calllogs"));
        t.put(newPut);

        fw.write(t.getName().getNameAsString() + "\r\n");
        fw.close();
    }

    @Override
    public void preGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
        super.preGetOp(e, get, results);

    }
}
