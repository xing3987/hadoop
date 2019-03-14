package hadoop.hdfs.wordcount;


/**
 * 一个用来处理数据的接口
 */
public interface Mapper {

    void map(String line, MapResult rs);
}
