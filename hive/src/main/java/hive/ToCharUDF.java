package hive;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * GenericUDF是进阶版的UDF,它支持传null参数，而且支持传入可变的参数
 */
public class ToCharUDF extends GenericUDF {
    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        return null;
    }

    @Override
    public Object evaluate(DeferredObject[] args) throws HiveException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        //方法有参数
        if (args != null) {
            if (args.length > 1) {
                //传入的参数需要转化成String类型；如需转成其他类型也要先转成Object(args[0].get()),再强转
                sf.applyPattern((String) args[1].get());
            }
            return sf.format((Date) args[0].get());
        } else {//方法无参数
            Date date = new Date();
            return sf.format(date);
        }
    }
/*
    //可以分开来重载方法使用，也可以像上面那样一起处理
    public Object evaluate() throws HiveException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = new Date();
        return sf.format(date);
    }

    public Object evaluate(Date date) throws HiveException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        return sf.format(date);
    }
*/

    @Override
    public String getDisplayString(String[] strings) {
        return null;
    }
}
