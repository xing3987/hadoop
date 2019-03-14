package hadoop.hdfs.wordcount;


import java.util.HashMap;
import java.util.Map;

/**
 * save result data for mapper
 */
public class MapResult {

    private Map<Object,Object> resultMap=new HashMap<>();

    public void put(Object key,Object value){
        resultMap.put(key,value);
    }

    public Object get(Object key){
        return resultMap.get(key);
    }

    public Map<Object, Object> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<Object, Object> resultMap) {
        this.resultMap = resultMap;
    }
}
