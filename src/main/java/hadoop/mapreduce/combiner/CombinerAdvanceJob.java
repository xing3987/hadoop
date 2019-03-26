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
 * 1.mapper阶段对key进行打散，使reducer阶段的数据量基本保持一致
 * 2.对得到的数据进行处理生成最终的数据
 */
public class CombinerAdvanceJob {
    public static void main(String[] args) throws Exception {

        //初始化
        Configuration cof = new Configuration();
        Job job = Job.getInstance(cof);
        job.setJarByClass(CombinerAdvanceJob.class);
        //设置参数
        job.setMapperClass(CombinerAdvanceMapper.class);
        job.setReducerClass(CombinerAdvanceReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setCombinerClass(CombinerAdvanceReducer.class);//设置mapper结束阶段对mapper的数据进行初步聚合，输入为mapper的输出，输出为reducer的输入，可以和reducer公用一个class

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //设置程序输入输出路径
        FileInputFormat.setInputPaths(job, new Path("G:\\datas\\input\\wordcount"));
        FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\combineradvance"));
        //想要启动的reduce task的数量
        job.setNumReduceTasks(2);
        // 提交job给yarn
        boolean res = job.waitForCompletion(true);
        //结束返回成功还是失败
        System.exit(res ? 0 : -1);
    }

    /**
     * mapper阶段对key进行打散，防止数据倾斜，使reducer阶段计算数据量几乎一致
     */
    public static class CombinerAdvanceMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private IntWritable valueout = new IntWritable(1);
        private Text keyout = new Text();

        private int num = 0;
        Random random = new Random();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            num = context.getNumReduceTasks();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString().trim();
            String[] words = line.split(" ");
            for (String word : words) {
                keyout.set(word + "-" + random.nextInt(num));
                context.write(keyout, valueout);
            }
        }
    }

    public static class CombinerAdvanceReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

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
