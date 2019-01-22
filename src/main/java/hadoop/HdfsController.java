package hadoop;


import org.apache.commons.compress.utils.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import sun.nio.ch.IOUtil;

import java.io.FileInputStream;
import java.io.IOException;

public class HdfsController {
    public static void main(String[] args) throws Exception{
        //创建配置
        Configuration conf=new Configuration();
        conf.set("fs.defaultFS","hdfs://hadoop001:9000");
        //获取操作的hdfs对象
        FileSystem fs= FileSystem.get(conf);
        //上传
        put(fs);
        return;

    }

    /**
     * 文件上次
     * 注意：windows中的用户没有向hdfs中上传数据的权限，所以默认代码执行会报错
     * 解决方案：1.去掉hdfs的用户权限检查机制，通过在hdfs-site.xml中配置dfs.permissions.enabled为false
     *         或2.把代码打包到linux中执行
     * @param fs
     * @throws IOException
     */
    private static void put(FileSystem fs) throws IOException {
        //指定需要上传的文件路径
        Path path=new Path("hdfs://hadoop001:9000/sample.txt");
        //获取文件系统的输出流
        FSDataOutputStream output=fs.create(path);
        //从本地获取文件
        FileInputStream input=new FileInputStream("E:\\github\\hadoop\\hadoop.iml");
        //通过工具类把输入流拷贝到输出流里面，实现本地文件上传到hdfs
        IOUtils.copy(input,output);
    }
}
