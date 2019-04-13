package hadoop.mapreduce.flow;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TreeMapSample {
    public static void main(String[] args) {
        TreeMap<Integer,String> treeMap=new TreeMap<>();
        treeMap.put(5,"hello");
        treeMap.put(2,"good");
        treeMap.put(8,"yes");
        treeMap.put(4,"no");
        treeMap.put(1,"help");
        System.out.println(treeMap);//treeMap会自动以key的大小排序

        TreeMap<FlowBean,String> treeMapbean=new TreeMap<>(new Comparator<FlowBean>() {//实现一个比较器
            @Override
            public int compare(FlowBean o1, FlowBean o2) {
                if(o1.getAmountFlow()-o2.getAmountFlow()==0){//当总流量相同时
                    return o1.getPhone().compareTo(o2.getPhone());//比较手机号码大小，实现字符串的对比
                }
                return o1.getAmountFlow()-o2.getAmountFlow();
            }
        });
        FlowBean b1=new FlowBean("13677",500,300);
        FlowBean b2=new FlowBean("12564",600,100);
        FlowBean b3=new FlowBean("13695",700,300);
        FlowBean b4=new FlowBean("16845",450,200);
        FlowBean b5=new FlowBean("16945",450,350);
        treeMapbean.put(b1,null);
        treeMapbean.put(b2,null);
        treeMapbean.put(b3,null);
        treeMapbean.put(b4,null);
        treeMapbean.put(b5,null);
        Set<Map.Entry<FlowBean, String>> entries = treeMapbean.entrySet();
        for (Map.Entry entry:entries){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }
    }
}
