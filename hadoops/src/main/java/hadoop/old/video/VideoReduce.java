package hadoop.old.video;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class VideoReduce extends Reducer<Text, VideoInfoWritable, Text, VideoInfoWritable> {
    @Override
    protected void reduce(Text k2, Iterable<VideoInfoWritable> v2s, Context context) throws IOException, InterruptedException {
        Iterator<VideoInfoWritable> it = v2s.iterator();
        Long golds = 0l;
        Long watchnumuvs = 0l;
        Long followers = 0l;
        Long lengths = 0l;
        while (it.hasNext()) {
            VideoInfoWritable next = it.next();
            golds+=next.getGold();
            watchnumuvs+=next.getWatchnumpv();
            followers+=next.getFollower();
            lengths+=next.getLength();
        }
        VideoInfoWritable v3=new VideoInfoWritable();
        v3.set(golds,watchnumuvs,followers,lengths);
        context.write(k2,v3);
    }
}
