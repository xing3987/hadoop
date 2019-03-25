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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 求共同好友问题
 * 分两步：
 * 1.求两个人的共同一个好友(注意reduce阶段顺序问题)
 * 2.合并共同好友，得到两个人的所有共同好友(注意第二部mapper阶段数据key,value是\t划分的)
 */
public class FriendsJob {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(FriendsJob.class);

        job.setMapperClass(FriendsMapper.class);
        job.setReducerClass(FriendsReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(1);

        FileInputFormat.setInputPaths(job, new Path("G:\\datas\\input\\friends"));
        FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\friends"));

        job.waitForCompletion(true);

    }

    public static class FriendsMapper extends Mapper<LongWritable, Text, Text, Text> {
        private Text outkey=new Text();
        private Text outvalue=new Text();
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (line != "") {
                String user = line.split(":")[0];
                String friendstr = line.split(":")[1];
                String[] friends = friendstr.split(",");
                outvalue.set(user);
                for (String friend : friends) {
                    outkey.set(friend);
                    context.write(outkey, outvalue);
                }
            }
        }
    }

    public static class FriendsReducer extends Reducer<Text, Text, Text, Text> {
        private List<String> list=new ArrayList<>();
        private Text outkey=new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            list.clear();//注意要重置list,要不然会出现数据冗余
            for (Text friend : values) {
                list.add(friend.toString());
            }
            Collections.sort(list);
            for(int i=0;i<list.size();i++){
                for(int j=i+1;j<list.size();j++){
                    outkey.set(list.get(i)+"-"+list.get(j));
                    context.write(outkey,key);
                }
            }
        }
    }
}
