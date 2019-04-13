package hadoop.mapreduce.topnadvance;

import hadoop.mapreduce.topn.JobPageTopnSubmitter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 求出一组商品中统一订单中的最大交易额的三个订单
 * 思路：（利用mapreduce框架的分组排序规则进行对数据的处理，提高操作效率）
 * 1.用bean做key  >> 相同orderid的数据排到一起，id晓得排在前面，大的排后面
 * 2.重写数据分发的规则  >> 让orderid相同的数据分给同一个reduce task写出到同一个文件
 * 3.重写GroupingComparator >> 只要orderid相同，就会被看成同一组进行一次reduce聚合
 */
public class TopnadvanceJob {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(JobPageTopnSubmitter.class);

        job.setMapperClass(TopnadvanceMapper.class);
        job.setGroupingComparatorClass(TopnadvanceGroup.class); //设置自定义的分发规则
        job.setPartitionerClass(TopnadvancePartitioner.class); //设置分发给reduce的规则
        job.setReducerClass(TopnadvanceReducer.class);
        job.setMapOutputKeyClass(Product.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setOutputKeyClass(Product.class);
        job.setOutputValueClass(NullWritable.class);

        job.setNumReduceTasks(3);

        FileInputFormat.setInputPaths(job, new Path("G:\\datas\\input\\topnadvance"));
        FileOutputFormat.setOutputPath(job, new Path("G:\\datas\\output\\topnadvance"));

        job.waitForCompletion(true);

    }

    public static class TopnadvanceMapper extends Mapper<LongWritable, Text, Product, NullWritable> {

        private Product product = new Product();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] words = value.toString().split(",");
            if (words.length >= 5) {
                product.setOrder(words[0]);
                product.setUid(words[1]);
                product.setName(words[2]);
                product.setPrice(Double.parseDouble(words[3]));
                product.setCount(Integer.parseInt(words[4]));
                context.write(product, NullWritable.get());
            }
        }
    }

    /**
     * 重写分组规则，根据product中的order进行分组
     */
    public static class TopnadvanceGroup extends WritableComparator {

        /**
         * 创建构造函数用来指定父类writableComparator中的实现类
         */
        public TopnadvanceGroup() {
            super(Product.class,true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            Product o1 = (Product) a;
            Product o2 = (Product) b;
            return o1.getOrder().compareTo(o2.getOrder());
        }
    }

    /**
     * 改写分发给reducer的规则
     */
    public static class TopnadvancePartitioner extends Partitioner<Product, NullWritable> {

        @Override
        public int getPartition(Product product, NullWritable product2, int i) {
            //return Integer.parseInt(product.getOrder().substring(2, -1)) % 3;
            return (product.getOrder().hashCode() & Integer.MAX_VALUE) % 3;
        }
    }


    public static class TopnadvanceReducer extends Reducer<Product, NullWritable, Product, NullWritable> {
        @Override
        protected void reduce(Product key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            int i = 0;
            for (NullWritable value : values) {
                context.write(key, value);
                if (++i == 3) return;
            }

        }
    }
}
