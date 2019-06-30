package flume;

import com.alibaba.fastjson.JSONObject;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义拦截器
 * 运行：
 * 1.把自定义jar包放入lib中
 * 2.bin/flume-ng agent -n a1(agent-name) -f conf/cus.conf(conf-path)
 *      -c conf -Dflume.root.logger=INFO,console
 * 注意要把fastjson-jar包打入jar中，要不然会卡在parseJson这步
 */
public class JsonInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(JsonInterceptor.class);
    private String[] schema;
    private String separator;//读取内容的分隔符，不是schema的分隔符

    public JsonInterceptor(String fields, String separator) {
        this.schema = fields.split("[,]");//fields必须以","为间隔
        this.separator = separator;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        logger.info("begin intercept...");
        Map<String, String> tuple = new LinkedHashMap<>();
        //将传入的Event中的body内容，加上schema，然后在放入到Event中
        String line = new String(event.getBody());
        String[] split = line.split(separator);
        if (split.length == schema.length) {//如果内容和schema匹配
            for (int i = 0; i < schema.length; i++) {
                tuple.put(schema[i], split[i]);
                logger.info("schema:"+schema[i]+",fields:"+split[i]);
            }
        }
        logger.info("begin parse json...");
        String json = JSONObject.toJSONString(tuple);
        logger.info(json);
        event.setBody(json.getBytes());
        logger.info("after intercept...");
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> list) {
        for (Event e : list) {
            intercept(e);
        }
        return list;
    }

    @Override
    public void close() {

    }

    /**
     * 定义静态内部类，构建拦截器对象static
     * Interceptor的生命周期方法
     * 构造器->configure->build
     */
    public static class JsonBuilder implements Interceptor.Builder {

        private String fields;
        private String separator;

        @Override
        public Interceptor build() {
            return new JsonInterceptor(fields, separator);
        }

        /**
         * 读取配置文件中的属性
         * 1.数据的分隔符
         * 2.字段名
         * 3.schema字段的分隔符
         *
         * @param context
         */
        @Override
        public void configure(Context context) {
            fields = context.getString("fields");
            separator = context.getString("separator");
        }
    }
}
