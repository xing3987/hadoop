package hadoop.video;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 统计每个用户当天直播的收到金币数量，总观看pv，粉丝关注数量，视频总开播时长
 数据格式示例：
 {"id":"15241919541468246679","uid":"123456","nickname":"SavageSara","gold":360,"watchnumpv":97,"watchnumuv":48,"hosts":5217,"smlook":17,"follower":2,"gifter":3,"length":2031,"area":"A_US","rating":"A","exp":54,"timestamp":1524194053}
 {'id':'视频id','uid:'主播id','nickname':'主播nickname','gold':'视频金币数据','watchnumpv':'视频观看人数pv','watchnumuv':'视频观看人数uv','hots':'视频点赞数','smlook':'进入直播间大于20秒人数','follower':'关注数','gifter':'送礼人数uv','length':'视频时长','area':'视频分区','rating':'评级','exp':'经验','timestamp':视频结束时间}
 */
public class VideoJob {
    public static void main(String[] args) throws Exception{
        if (args.length < 2) {
            System.exit(10);
        }
        //获取程序的输入和输出目录
        String inputPath = args[0];
        String outputPath = args[1];
        //创建配置
        Configuration conf = new Configuration();

        //获取名称
        String jobName = VideoJob.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);

        //组装jar包必备代码
        job.setJarByClass(VideoJob.class);

        //设置程序输入输出路径
        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        //设置map相关参数
        job.setMapperClass(VideoMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(VideoInfoWritable.class);  //注意输出的key,value要和mapper定义一致
        //设置reduce相关参数
        job.setReducerClass(VideoReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(VideoInfoWritable.class);  ////注意输出的key,value要和reduce定义一致

        //提交job,等待执行成功
        job.waitForCompletion(true);


    }
}
