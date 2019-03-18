package hadoop.mapreduce.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 1.开发环境，job提交到本地，不会提交到yarn上，只用于开发调试
 * 2.集群可以不打开
 * 3.先决条件：本地有hadoop-window版，并配成HADOOP_HOME的路径；把hadoop-window的bin配到Path中
 * 4.该模式可以方便debug
 * 5.开发完成后改变文件输入输出路径就可以直接在集群中运行
 */
public class WordCountJobWinLocal {
    public static void main(String[] args) throws Exception{

        //初始化
        Configuration cof=new Configuration();
        Job job= Job.getInstance(cof);
        job.setJarByClass(WordCountJobWinLocal.class);
        //设置参数
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);


        //设置程序输入输出路径
        FileInputFormat.setInputPaths(job, new Path("G:\\datas\\input\\wordcount"));
        FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\wordcount"));
        //想要启动的reduce task的数量
        job.setNumReduceTasks(2);
        // 提交job给yarn
        boolean res = job.waitForCompletion(true);
        //结束返回成功还是失败
        System.exit(res?0:-1);
    }
}
