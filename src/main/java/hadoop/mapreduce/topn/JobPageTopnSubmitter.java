package hadoop.mapreduce.topn;

import hadoop.mapreduce.flow.FlowBean;
import hadoop.mapreduce.flow.FlowCountMapper;
import hadoop.mapreduce.flow.FlowCountReducer;
import hadoop.mapreduce.flow.ProvincePartitioner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 实现写出使用最大流量的5个人
 */
public class JobPageTopnSubmitter {

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf);

		job.setJarByClass(JobPageTopnSubmitter.class);

		job.setMapperClass(PageTopnMapper.class);
		job.setReducerClass(PageTopnReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FlowBean.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FlowBean.class);

		job.setNumReduceTasks(1);

		FileInputFormat.setInputPaths(job, new Path("G:\\datas\\input\\flow"));
		FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\topn"));

		job.waitForCompletion(true);

	}

}
