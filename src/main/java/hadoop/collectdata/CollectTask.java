package hadoop.collectdata;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.UUID;

/**
 * 定时任务
 * 定时探测日志源目录
 * 获取需要采集的文件
 * 移动这些文件到一个待上传的临时目录
 * 遍历待上传目录汇总的各个文件，逐一传输到HDFS的目标路径，同时将传输完成的文件移动到备份的目录
 */
public class CollectTask extends TimerTask {
    Logger logger = Logger.getLogger(CollectTask.class);
    private final String TEMPORARY_FILE = "g:/logs/temporary";
    private final String COPY_FILE = "g:/logs/copys/";
    private final String HDFS_FILE = "/logs/";
    private final String HDFS_URI = "hdfs://hadoop001:9000";

    @Override
    public void run() {
        logger.debug("begin to run task.");
        //获取日期
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String datastr = sd.format(new Date());

        File dir = new File("g:/logs/logdatas/");
        //过滤需要的文件，以.log名字结尾的log文件
        logger.debug("begin to get files by filefilter.");
        File[] listFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".log")) {
                    return true;
                }
                return false;
            }
        });
        //移动文件到临时目录
        logger.debug("begin to move files to temporary.");
        try {
            File temporary = new File(TEMPORARY_FILE);
            for (File file : listFiles) {
                FileUtils.moveFileToDirectory(file, temporary, true);
            }

            //遍历待上传目录汇总的各个文件，逐一传输到HDFS的目标路径，同时将传输完成的文件移动到备份的目录
            Configuration cof = new Configuration();//加载配置对象
            cof.set("dfs.replication", "2");//设置保存的副本数
            cof.set("dfs.blocksize", "64m");//设置文件块大小
            FileSystem fs = FileSystem.get(new URI(HDFS_URI), cof, "root");//连接文件系统
            File[] files = temporary.listFiles();
            //判断存放文件的路径文件夹在hdfs中存在
            Path hdfs = new Path(HDFS_FILE + datastr);
            if (!fs.exists(hdfs)) {
                fs.mkdirs(hdfs);
            }
            logger.debug("begin to move files to hdfs.");
            for (File file : files) {
                fs.copyFromLocalFile(new Path(file.getAbsolutePath()), new Path(HDFS_FILE + datastr + "/" + UUID.randomUUID() + ".log"));
                FileUtils.moveFileToDirectory(file, new File(COPY_FILE + datastr), true);
            }

            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getStackTrace());
        }
    }
}
