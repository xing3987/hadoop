package hadoop.mapreduce.combiner;

import hadoop.mapreduce.wordcount.WordCountJobWinLocal;
import hadoop.mapreduce.wordcount.WordCountMapper;
import hadoop.mapreduce.wordcount.WordCountReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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
 * 当处理一行有大量重复的数据时可以使用combiner组件在mapper阶段对数据进行初步reducer
 * 然后在reducer阶段进行全局聚合
 */
public class CombinerJob {
    public static void main(String[] args) throws Exception {

        //初始化
        Configuration cof = new Configuration();
        Job job = Job.getInstance(cof);
        job.setJarByClass(CombinerJob.class);
        //设置参数
        job.setMapperClass(CombinerMapper.class);
        job.setReducerClass(CombinerReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setCombinerClass(CombinerReducer.class);//设置mapper结束阶段对mapper的数据进行初步聚合，输入为mapper的输出，输出为reducer的输入，可以和reducer公用一个class

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //设置程序输入输出路径
        FileInputFormat.setInputPaths(job, new Path("G:\\datas\\input\\wordcount"));
        FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\combiner"));
        //想要启动的reduce task的数量
        job.setNumReduceTasks(2);
        // 提交job给yarn
        boolean res = job.waitForCompletion(true);
        //结束返回成功还是失败
        System.exit(res ? 0 : -1);
    }

    public static class CombinerMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private IntWritable valueout = new IntWritable(1);
        private Text keyout = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString().trim();
            String[] words = line.split(" ");
            for (String word : words) {
                keyout.set(word);
                context.write(keyout, valueout);
            }
        }
    }

    public static class CombinerReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable value = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            Iterator<IntWritable> it = values.iterator();
            int sum = 0;
            while (it.hasNext()) {
                sum += it.next().get();
            }
            value.set(sum);
            context.write(key, value);
        }
    }
}
