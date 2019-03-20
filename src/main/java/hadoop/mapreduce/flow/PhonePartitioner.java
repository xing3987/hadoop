package hadoop.mapreduce.flow;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义mapper分发给reducer的规则
 * 通过手机号码的归属地进行分发，job的main方法要设置不使用默认分发规则，使用该自定类
 * 该类是给mapper服务的，所以数据是mapper的输出数据
 * MapTask通过这个类的getPartition方法，来计算它所产生的每一对kv数据该分发给哪一个reduce task
 */
public class PhonePartitioner extends Partitioner<Text, FlowBean> {

    private static Map<String, Integer> map = new HashMap<>();

    //定义静态代码块，在该类创建时先加载,实际情况应该从数据库查询得到号码的归属地，然后保存到map中
    static {
        map.put("135", 0);
        map.put("136", 1);
        map.put("137", 2);
        map.put("138", 3);
        map.put("139", 4);
    }

    @Override
    public int getPartition(Text text, FlowBean flowBean, int i) {
        Integer value = map.get(text.toString().substring(0, 3));
        return value == null ? 5 : value;
    }
}
