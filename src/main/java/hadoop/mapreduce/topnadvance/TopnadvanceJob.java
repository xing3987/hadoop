package hadoop.mapreduce.topnadvance;

import hadoop.mapreduce.index.IndexJobStepTwo;
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

/**
 * 求出一组商品中统一订单中的最大交易额的三个订单
 * 思路：（利用mapreduce框架的分组排序规则进行对数据的处理，提高操作效率）
 *      1.用bean做key  >> 相同orderid的数据排到一起，id晓得排在前面，大的排后面
 *      2.重写数据分发的规则  >> 让orderid相同的数据分给同一个reduce task写出到同一个文件
 *      3.重写GroupingComparator >> 只要orderid相同，就会被看成同一组进行一次reduce聚合
 *
 */
public class TopnadvanceJob {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(JobPageTopnSubmitter.class);

        job.setMapperClass(IndexJobStepTwo.IndexTwoMapper.class);
        job.setReducerClass(IndexJobStepTwo.IndexTwoReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(1);

        FileInputFormat.setInputPaths(job, new Path("G:\\datas\\output\\index\\index1"));
        FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\index\\index2"));

        job.waitForCompletion(true);

    }

    public static class TopnadvanceMapper extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] words = value.toString().split("-");
            if (words.length > 1) {
                context.write(new Text(words[0]), new Text(words[1].replace("\t", "-->")));
            }
        }
    }

    public static class TopnadvanceReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            /*Iterator<Text> iterator = values.iterator();
            String out = "";
            while (iterator.hasNext()) {
                out += iterator.next().toString() + "\t";
            }*/
            StringBuffer sb=new StringBuffer();
            for(Text text:values){
                sb.append(text.toString()+"\t");
            }
            context.write(key, new Text(sb.toString()));
        }
    }
}
