package hadoop.mapreduce.combiner;

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
import java.util.Random;

/**
 * 对combinerAdvance第一阶段的数据进行再次处理
 */
public class CombinerAdvanceTwo {
    public static void main(String[] args) throws Exception {

        //初始化
        Configuration cof = new Configuration();
        Job job = Job.getInstance(cof);
        job.setJarByClass(CombinerAdvanceTwo.class);
        //设置参数
        job.setMapperClass(CombinerAdvanceTwoMapper.class);
        job.setReducerClass(CombinerAdvanceTwoReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //设置程序输入输出路径
        FileInputFormat.setInputPaths(job, new Path("G:\\datas\\output\\combineradvance"));
        FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\combinerAdvanceTwo"));
        //想要启动的reduce task的数量
        job.setNumReduceTasks(2);
        // 提交job给yarn
        boolean res = job.waitForCompletion(true);
        //结束返回成功还是失败
        System.exit(res ? 0 : -1);
    }


    public static class CombinerAdvanceTwoMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private IntWritable valueout = new IntWritable();
        private Text keyout = new Text();


        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString().trim();
            String[] words = line.split("\t");
            valueout.set(Integer.parseInt(words[1]));
            String[] keys=words[0].split("-");
            keyout.set(keys[0]);
            context.write(keyout, valueout);
        }
    }

    public static class CombinerAdvanceTwoReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

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
