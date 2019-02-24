package hadoop;

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
import java.util.Arrays;
import java.util.Iterator;

/**
 * 单表关联,给出child-parent表,要求输出grandchild-grandparent表
 */
public class STjoin {

    public static int time = 0;

    public static class MyMapper extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        protected void map(LongWritable k1, Text v1, Mapper<LongWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            String line = v1.toString();
            if (k1.get() != 0) { //去除标题行不处理
                String[] splits = line.split(" "); //通过空格分隔
                String relationtype = "1"; //用于区分左右表grandchild与grandparent表
                context.write(new Text(splits[1]), new Text(relationtype + " " + splits[0] + " " + splits[1]));
                relationtype = "2"; //用于区分左右表grandchild(1)与grandparent(2)表
                context.write(new Text(splits[0]), new Text(relationtype + " " + splits[0] + " " + splits[1]));
            }
        }
    }

    public static class MyReduce extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text k2, Iterable<Text> v2s, Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            if (time == 0) { //输出表头
                context.write(new Text("grandchild"), new Text("grandparent"));
                time++;
            }
            int grandchildnum = 0;
            int grandparentnum = 0;
            int datalong = 10;//初始数组长度
            String[] grandchild = new String[datalong];
            String[] grandparent = new String[datalong];
            Iterator<Text> it = v2s.iterator();
            int newdatalong = 0;
            while (it.hasNext()) {
                newdatalong++;
                if (newdatalong > datalong) {//如果数据过多超出数组长度限度，扩容
                    datalong = datalong * 2;
                    grandchild = Arrays.copyOf(grandchild, datalong);
                    grandparent = Arrays.copyOf(grandparent, datalong);
                }
                String data = it.next().toString();
                String[] datas = data.split(" ");
                String childname = datas[1];
                String parentname = datas[2];
                //取出child放入grandchild
                if (datas[0].equals("1")) {
                    grandchild[grandchildnum] = childname;
                    grandchildnum++;
                } else {//取出parent放入grandparent
                    grandparent[grandparentnum] = parentname;
                    grandparentnum++;
                }

            }
            /*
             * 数据说明：
             *          （优先输出这个）当key为jack时..(map 自动按字母顺序排列)tom alice;tom jesse;jone alice;jone jesse;
             *          （然后）当key为lucy时map：
             *                  lucy: tom 1 tom lucy/jone 1 jone lucy/ lucy 2 lucy mary/lucy 2 lucy ben
             *          reduce:
             *                  grandchild[0] = tom;grandchild[1] = jone;
             *                  grandparent[0] = mary;grandparent[1] = ben;
             *          输出:
             *                  tom mary;tom ben;jone mary;jone ben;
             *
             */
            //grandchild和grandparent数组求笛卡尔积
            if (grandchildnum != 0 && grandparentnum != 0) {
                for (int m = 0; m < grandchildnum; m++) {
                    for (int n = 0; n < grandparentnum; n++) {
                        context.write(new Text(grandchild[m]), new Text(grandparent[n]));
                    }
                }
            }
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
        String jobName = STjoin.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);

        //组装jar包必备代码
        job.setJarByClass(STjoin.class);

        //设置程序输入输出路径
        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        //设置map相关参数
        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        //设置reduce相关参数
        job.setReducerClass(MyReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //提交job,等待执行成功
        job.waitForCompletion(true);

        //后续操作
        //1.maven clean package 打成jar包放到主节点服务器上
        //2.运行hadoop jar [jarname eg:hadoopJob.jar] [jobpath eg:hadoop.WordCountApp] /inputfilepath /outputfile
        //3.注意输出的路径在hadoop集群中不能有重名，否则会报错
        //4.等待job执行完成后查看outputfile中的数据
    }
}
