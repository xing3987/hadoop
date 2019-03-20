package hadoop.mapreduce.topn;

import hadoop.mapreduce.flow.FlowBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PageTopnReducer extends Reducer<Text, FlowBean, Text, FlowBean> {

    List<FlowBean> list=new ArrayList<>();

    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Context context)
            throws IOException, InterruptedException {

        int upSum = 0;
        int dSum = 0;

        for(FlowBean value:values){
            upSum += value.getUpFlow();
            dSum += value.getdFlow();
        }
        FlowBean bean=new FlowBean(key.toString(), upSum, dSum);
        list.add(bean);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        Collections.sort(list);
        Configuration cof=context.getConfiguration();//得到配置文件
        int max=cof.getInt("top.n",5);//从配置文件取值，如果没有默认为5
        for(int i=0;i<max;i++){
            context.write(new Text(list.get(i).getPhone()), list.get(i));
        }
    }
}
