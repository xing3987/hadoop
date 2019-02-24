package hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Iterator;

/**
 * 数据排序，如成绩等
 */
public class DataSort {
    /*
    把输入的value做为key输出,使用hadoop对key的自动排列规则来对数据进行排列
     */
    public static class SortMap extends Mapper<LongWritable, Text, LongWritable, LongWritable>{
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            LongWritable k2 = new LongWritable();
            LongWritable v2 = new LongWritable();
            k2.set(Long.parseLong(line));
            v2.set(1);
            System.out.println("key:"+k2+"******** value:"+v2);
            context.write(k2,v2);
        }
    }

    public static class SortReduce extends Reducer<LongWritable, LongWritable, LongWritable, LongWritable> {
        private static LongWritable linenum=new LongWritable(1);
        @Override
        protected void reduce(LongWritable key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            for(LongWritable value:values){
                System.out.println("linenum:"+linenum+"******** key:"+key);
                context.write(key,null);
                linenum=new LongWritable(linenum.get()+1);
            }
        }
    }

    public static class SortPartition extends Partitioner<LongWritable, LongWritable>{
        @Override
        public int getPartition(LongWritable key, LongWritable value, int i) {
            int Maxnum=65223;
            int bound =Maxnum/i +1;
            long keynum=key.get();
            for(int j=1;j<i;j++){
                if(keynum<bound*j && keynum >= bound*(i-1)){
                    return j-1;
                }
            }
            return -1;
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
        //获取名称
        String jobName = DataSort.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);

        //组装jar包必备代码
        job.setJarByClass(DataSort.class);

        //设置程序输入输出路径
        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        //设置map相关参数
        job.setMapperClass(DataSort.SortMap.class);
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(LongWritable.class);
        //设置partition参数
        job.setPartitionerClass(DataSort.SortPartition.class);
        //设置reduce相关参数
        job.setReducerClass(DataSort.SortReduce.class);
        job.setOutputKeyClass(LongWritable.class);
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
