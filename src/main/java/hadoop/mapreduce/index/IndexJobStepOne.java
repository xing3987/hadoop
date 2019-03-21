package hadoop.mapreduce.index;

import hadoop.mapreduce.topn.JobPageTopnSubmitter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Iterator;

/**
 * 实现对多个文件中的数据进行统计，并输出单词在每个文件出现的个数
 * 实现方式：1.先写一个mapreduce输出word-filename count
 *          2.再写一个mapreduce输出word filename count ; filename2 count ..
 */
public class IndexJobStepOne {
    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(JobPageTopnSubmitter.class);

        job.setMapperClass(IndexOneMapper.class);
        job.setReducerClass(IndexOneReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setNumReduceTasks(2);

        FileInputFormat.setInputPaths(job, new Path("G:\\datas\\input\\index"));
        FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\index\\index1"));

        job.waitForCompletion(true);

    }

    public static class IndexOneMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            FileSplit inputSplit = (FileSplit) context.getInputSplit();  //ctrl+h 得到所有的子类，强转
            String filename = inputSplit.getPath().getName();

            String line=value.toString();
            String[] words=line.split(" ");
            for(String word : words){
                context.write(new Text(word+"-"+filename),new IntWritable(1));
            }
        }
    }

    public static class IndexOneReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            Iterator<IntWritable> it = values.iterator();
            int sum = 0;
            while (it.hasNext()) {
                sum += it.next().get();
            }
            context.write(key, new IntWritable(sum));
        }
    }
}
