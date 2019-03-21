package hadoop.mapreduce.index;

import hadoop.mapreduce.topn.JobPageTopnSubmitter;
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

public class IndexJobStepTwo {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(JobPageTopnSubmitter.class);

        job.setMapperClass(IndexTwoMapper.class);
        job.setReducerClass(IndexTwoReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(1);

        FileInputFormat.setInputPaths(job, new Path("G:\\datas\\output\\index\\index1"));
        FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\index\\index2"));

        job.waitForCompletion(true);

    }

    public static class IndexTwoMapper extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] words = value.toString().split("-");
            if (words.length > 1) {
                context.write(new Text(words[0]), new Text(words[1].replace("\t", "-->")));
            }
        }
    }

    public static class IndexTwoReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Iterator<Text> iterator = values.iterator();
            String out = "";
            while (iterator.hasNext()) {
                out += iterator.next().toString() + "\t";
            }
            context.write(key, new Text(out));
        }
    }
}
