package hadoop.helper;

import java.util.Properties;

/*
 * 单利模式获取配置文件
 * 双检锁/双重校验锁（DCL，即 double-checked locking）
    JDK 版本：JDK1.5 起
    是否 Lazy 初始化：是
    是否多线程安全：是
 */
public class PropertyHelper {

    private static volatile Properties prop=null;

    private PropertyHelper() {
    }

    public static Properties getProps() throws Exception{
        if(prop == null){
            synchronized (PropertyHelper.class){
                if(prop == null){
                    prop=new Properties();
                    prop.load(PropertyHelper.class.getClassLoader().getResourceAsStream("collect.properties"));
                }
            }
        }
        return prop;
    }

    public static String getProperty(String key,String defaulValue) {
        try{
            return PropertyHelper.getProps().getProperty(key,defaulValue);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getProperty(String key) {
        try{
            return PropertyHelper.getProps().getProperty(key);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
