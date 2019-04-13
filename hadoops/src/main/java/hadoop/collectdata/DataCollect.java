package hadoop.collectdata;

import java.util.Timer;

/**
 *  日志采集
 *  1.启动一个定时任务
 *      定时探测日志源目录
 *      获取需要采集的文件
 *      移动这些文件到一个待上传的临时目录
 *      便利待上传目录汇总的各个文件，逐一传输到HDFS的目标路径，同时将传输完成的文件移动到备份的目录
 *
 *  2.规划各种路径
 *      日志源路径：g:/logs/logdatas
 *      待上传临时目录: g:/logs/temporary
 *      备份目录： g:/logs/copys
 *      hdfs储存路径：/logs/yyyyMMdd/filename
 */
public class DataCollect {
    public static void main(String[] args) {
        Timer timer=new Timer();
        timer.schedule(new CollectTask(),0,60*60*1000l);//自动采集数据并上传
        timer.schedule(new DeleteTask(),12,24*60*60*1000l);//自动删除过期数据(12小时后执行，每一天执行一次，删除一个月前的数据)
    }
}
