package hive;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * hive中如何定义自己的函数：
 * 1、先写一个java类（extends UDF,重载方法public C evaluate(A a,B b)），实现你所想要的函数的功能（传入一个json字符串和一个脚标，返回一个值）
 * 2、将java程序打成jar包，上传到hive所在的机器
 * 3、在hive命令行中将jar包添加到classpath ：
 * hive>add jar /root/hivetest/myjson.jar;
 * 4、在hive命令中用命令创建一个函数叫做myjson，关联你所写的这个java类
 * hive> create temporary function myjson as 'hive.MyJsonParser';
 */
public class MyJsonParser extends UDF {

    /**
     * @param json
     * @param index
     * @return
     */
    public String evaluate(String json, int index) {
        //{"movie":"1193","rate":"5","timeStamp":"978300760","uid":"1"}
        String[] fields = json.split("\"");
        if ((4 * index + 3) < fields.length){
            return fields[4 * index + 3];
        }else{
            return null;
        }
    }

}
