package hadoop.mapreduce.topn;

import hadoop.mapreduce.flow.FlowBean;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

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
        int max=5;
        for(int i=0;i<max;i++){
            context.write(new Text(list.get(i).getPhone()), list.get(i));
        }
    }
}
