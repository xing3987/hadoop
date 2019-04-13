package hadoop.mapreduce.friends;

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

public class FriendsJobTwo {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(FriendsJobTwo.class);

        job.setMapperClass(FriendsTwoMapper.class);
        job.setReducerClass(FriendsTwoReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(1);

        FileInputFormat.setInputPaths(job, new Path("G:\\datas\\output\\friends"));
        FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\friendstwo"));

        job.waitForCompletion(true);

    }

    private static class FriendsTwoMapper extends Mapper<LongWritable, Text, Text, Text> {
        private Text outkey = new Text();
        private Text outvalue = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] words = value.toString().split("\t");
            if (words.length > 1) {
                outkey.set(words[0]);
                outvalue.set(words[1]);
                context.write(outkey, outvalue);
            }
        }
    }

    private static class FriendsTwoReducer extends Reducer<Text, Text, Text, Text> {
        private Text outvalue = new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuffer sb=new StringBuffer();
            for(Text value:values){
                sb.append(value.toString()+",");
            }
            outvalue.set(sb.toString());
            context.write(key,outvalue);
        }
    }
}
