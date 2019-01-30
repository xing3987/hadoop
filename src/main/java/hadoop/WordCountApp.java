package hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Iterator;

/**
 * 单词计数,原始数据
 * hello you
 * hello me
 * <p>
 * 想要的结果
 * hello 2
 * you 1
 * me 1
 * <p>
 * <k1,v1>=<0,hello you>  <10,hello me>
 * <k2,v2>=<hello,1> <you,1> <hello,1> ..
 * <k3,v3>=<hello,2> ..
 */
public class WordCountApp {

    public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
        Text k2 = new Text();
        LongWritable v2 = new LongWritable();

        /*
            对文件中的每一行进行处理
            输入<k1,v1>=<0,hello you>  <10,hello me>
            输出<k2,v2>=<hello,1> <you,1> <hello,1>
         */
        @Override
        protected void map(LongWritable k1, Text v1, Mapper<LongWritable, Text, Text, LongWritable>.Context context) throws IOException, InterruptedException {
            String line = v1.toString();
            String[] splits = line.split(" "); //通过空格分隔
            for (String word : splits) {
                k2.set(word);
                v2.set(1);
                context.write(k2, v2);
            }
        }
    }

    public static class MyReduce extends Reducer<Text, LongWritable, Text, LongWritable> {
        /**
         * 针对每一个<k2,v2s>都会调用一次reduce方法
         * 最终会产生<k3,v3>
         */
        @Override
        protected void reduce(Text k2, Iterable<LongWritable> v2s, Reducer<Text, LongWritable, Text, LongWritable>.Context context) throws IOException, InterruptedException {
            Iterator<LongWritable> it = v2s.iterator();
            long sum = 0;
            while (it.hasNext()) {
                LongWritable next = it.next();
                sum += next.get();
            }
            context.write(k2, new LongWritable(sum));
        }
    }

    //把map和reduce组装成一个job
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.exit(10);
        }
        //获取程序的输入和输出目录
        String inputPath = args[0];
        String outputPath = args[1];
        //创建配置
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://hadoop001:9000");
        //获取名称
        String jobName = WordCountApp.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);

        //组装jar包必备代码
        job.setJarByClass(WordCountApp.class);

        //设置程序输入输出路径
        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        //设置map相关参数
        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        //设置reduce相关参数
        job.setReducerClass(MyReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        //提交job,等待执行成功
        job.waitForCompletion(true);

        //后续操作
        //1.maven clean package 打成jar包放到主节点服务器上
        //2.运行hadoop jar [jarname eg:hadoopJob.jar] [jobpath eg:hadoop.WordCountApp] /inputfilepath /outputfile
        //3.注意输出的路径在hadoop集群中不能有重名，否则会报错
        //4.等待job执行完成后查看outputfile中的数据
    }
}






