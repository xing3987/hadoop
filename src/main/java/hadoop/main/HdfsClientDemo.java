package hadoop.main;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

public class HdfsClientDemo {
    public static void main(String[] args) throws Exception {
        Configuration cof = new Configuration();//加载配置对象
        cof.set("dfs.replication", "2");//设置保存的副本数
        cof.set("dfs.blocksize", "64m");//设置文件块大小
        FileSystem fs = FileSystem.get(new URI("hdfs://hadoop001:9000"), cof, "root");//连接文件系统
        fs.copyFromLocalFile(new Path("E:\\github\\hadoop\\hadoop.iml"), new Path("/"));//设置上传的文件和保存路径
        fs.close();//关闭连接
    }

    FileSystem fs = null;

    @Before
    public void init() throws Exception {
        Configuration cof = new Configuration();//加载配置对象
        cof.set("dfs.replication", "2");//设置保存的副本数
        cof.set("dfs.blocksize", "64m");//设置文件块大小
        fs = FileSystem.get(new URI("hdfs://hadoop001:9000"), cof, "root");//连接文件系统
    }

    /**
     * 从集群中下载文件到window本地,需要先在window配置hadoop环境（hadoop-windows,放在英文目录下）
     *
     * @throws Exception
     */
    @Test
    public void downLoadFromHadoop() throws Exception {
        fs.copyToLocalFile(new Path("/mydata"), new Path("F:\\"));//第一个参数是hadoop集群的文件路径，第二个参数是本地路径
    }

    @Test
    public void rename() throws Exception {
        fs.rename(new Path("/mydata"), new Path("/mydatas"));
    }

    @Test
    public void mkdir() throws Exception {
        fs.mkdirs(new Path("/mydirs"));
    }

    @Test
    public void delete() throws Exception {
        fs.delete(new Path("/mydirs"), true); //第一个参数是删除的路径或文件，第一个参数是是否递归删除
    }

    /**
     * 查询hadoop集群中的文件信息，返回的是迭代器
     * @throws Exception
     */
    @Test
    public void select() throws Exception {
        RemoteIterator<LocatedFileStatus> it = fs.listFiles(new Path("/sortdata"), true);
        while (it.hasNext()){
            LocatedFileStatus status=it.next();
            System.out.println("path:"+status.getPath());
            System.out.println("blocksize:"+status.getBlockSize());
            System.out.println("replication:"+status.getReplication());
            System.out.println("file length:"+ status.getLen());
        }
    }

    /**
     * 查询hadoop集群中的文件信息，返回的是该文件夹下的数组
     * @throws Exception
     */
    @Test
    public void selectCurrent() throws Exception {
        FileStatus[] fileStatuList=fs.listStatus(new Path("/sortdata"));
        for(FileStatus fileStatus:fileStatuList){
            System.out.println("path"+fileStatus.getPath());
            System.out.println("isDirectory"+fileStatus.isDirectory());
        }
    }

    @After
    public void close() throws Exception {
        fs.close();
    }
}
