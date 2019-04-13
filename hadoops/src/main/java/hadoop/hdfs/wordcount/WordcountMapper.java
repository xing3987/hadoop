package hadoop.hdfs.wordcount;

/**
 * 处理计数逻辑
 */

public class WordcountMapper implements Mapper {
    @Override
    public void map(String line, MapResult rs) {
        String[] words=line.split(" ");
        for(String word:words){
            if(rs.get(word)!=null){
                Integer count=(Integer) rs.get(word);
                rs.put(word,count+1);
            }else{
                rs.put(word,1);
            }
        }
    }
}
