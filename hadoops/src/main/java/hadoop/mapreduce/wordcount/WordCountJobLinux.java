package hadoop.mapreduce.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * wordcount 启动工程（linux 环境下的启动）
 */
public class WordCountJobLinux {
    public static void main(String[] args) throws Exception{
        if (args.length < 2) {
            System.exit(10);
        }
        //获取程序的输入和输出目录
        String inputPath = args[0];
        String outputPath = args[1];

        //初始化
        Configuration cof=new Configuration();
        Job job= Job.getInstance(cof);
        job.setJarByClass(WordCountJobLinux.class);
        //设置参数
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);


        //设置程序输入输出路径
        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        //想要启动的reduce task的数量
        job.setNumReduceTasks(2);
        // 提交job给yarn
        boolean res = job.waitForCompletion(true);
        //结束返回成功还是失败
        System.exit(res?0:-1);
    }
}
