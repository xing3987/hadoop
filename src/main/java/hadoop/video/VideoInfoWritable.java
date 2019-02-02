package hadoop.video;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class VideoInfoWritable implements Writable{

    private Long gold;
    private Long watchnumpv;
    private Long follower;
    private Long length;

    public Long getGold() {
        return gold;
    }

    public Long getWatchnumpv() {
        return watchnumpv;
    }

    public Long getFollower() {
        return follower;
    }

    public Long getLength() {
        return length;
    }

    public void set(Long gold,Long watchnumpv,Long follower,Long length) {
        this.gold = gold;
        this.watchnumpv = watchnumpv;
        this.follower = follower;
        this.length = length;
    }

    /*
    继承父类的方法，定义写出的数据，注意顺序要和readfields方法一致，不然会发生数据错位
    注意对应类型要使用对应的方法写出
     */
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(gold);
        dataOutput.writeLong(watchnumpv);
        dataOutput.writeLong(follower);
        dataOutput.writeLong(length);
    }

    public void readFields(DataInput dataInput) throws IOException {
        this.gold=dataInput.readLong();
        this.watchnumpv=dataInput.readLong();
        this.follower=dataInput.readLong();
        this.length=dataInput.readLong();
    }

    @Override
    public String toString() {
        return  gold +"/t" + watchnumpv +"/t" + follower +"/t" + length;
    }
}
