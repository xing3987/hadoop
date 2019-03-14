package hadoop.hdfs.wordcount;


import hadoop.helper.Constants;
import hadoop.helper.PropertyHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * 写一个框架用来处理map过程
 */
public class HdfsWordCount {

    public static void main(String[] args) throws Exception {

        //初始化数据
        Configuration cof = new Configuration();
        cof.set("dfs.replication", "2");
        cof.set("dfs.blocksize", "64m");
        FileSystem fs = FileSystem.get(new URI(PropertyHelper.getProperty(Constants.HDFS_URI)), cof, "root");
        Path inputpath=new Path(PropertyHelper.getProperty(Constants.HDFS_WORDCOUNT_INPUT_PATH));
        if(!fs.exists(inputpath)){
            throw new RuntimeException("input file is not exists.");
        }
        Path outputpath=new Path(PropertyHelper.getProperty(Constants.HDFS_WORDCOUNT_OUTPUT_PATH));
        if(fs.exists(outputpath)){
            throw new RuntimeException("output file is exists.please change a file.");
        }else {
            fs.mkdirs(outputpath);
        }

        //加载工具类
        String classname=PropertyHelper.getProperty(Constants.HDFS_WORDCOUNT_MAPPER);
        Class clz=Class.forName(classname);
        Mapper mapper=(Mapper) clz.newInstance();
        MapResult rs=new MapResult();

        //获取文件中的数据并处理
        RemoteIterator<LocatedFileStatus> iter = fs.listFiles(inputpath, true);//如果是文件夹递归获取文件夹中所有文件
        while (iter.hasNext()){
            LocatedFileStatus lfs=iter.next();
            FSDataInputStream inputfile=fs.open(lfs.getPath());
            BufferedReader bf=new BufferedReader(new InputStreamReader(inputfile));
            String line=null;
            while ((line=bf.readLine())!=null){
                //处理数据
                mapper.map(line,rs);
            }
            inputfile.close();
            bf.close();
        }




        //写出数据到hdfs中
        FSDataOutputStream out=fs.create(new Path(PropertyHelper.getProperty(Constants.HDFS_WORDCOUNT_OUTPUT_PATH)+"/datas.dat"));
        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(out));

        Map<Object, Object> resultMap = rs.getResultMap();
        Set<Map.Entry<Object, Object>> entries = resultMap.entrySet();
        for(Map.Entry<Object,Object> entry:entries){
            bw.write(entry.getKey().toString()+"\t"+entry.getValue().toString()+"\n");
        }
        bw.close();
        out.close();

        fs.close();
        System.out.println("data successed write out.");
    }
}
