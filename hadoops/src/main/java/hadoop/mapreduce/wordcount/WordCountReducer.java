package hadoop.mapreduce.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

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
