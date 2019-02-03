package hadoop.video;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class VideoMapper extends Mapper<LongWritable, Text, Text, VideoInfoWritable> {
    @Override
    protected void map(LongWritable k1, Text v1, Context context) throws IOException, InterruptedException {
        VideoInfoWritable v2 = new VideoInfoWritable();
        Text k2 = new Text();
        System.out.println("*************k1:"+k1+",v1:"+v1+"**************");
        JSONObject object = JSON.parseObject(v1.toString());
        String uid = object.getString("uid");
        Long gold = object.getLong("gold"); //注意要直接使用getLong等方法，要不然可能发生类型转换错误
        Long watchnumuv = object.getLong("watchnumuv");
        Long follower = object.getLong("follower");
        Long length = object.getLong("length");

        k2.set(uid);
        v2.set(gold, watchnumuv, follower, length);
        context.write(k2, v2);
    }
}
