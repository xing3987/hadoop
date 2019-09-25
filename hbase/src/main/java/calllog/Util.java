package calllog;

import java.text.DecimalFormat;

public class Util {

    public static String getRegNo(String callerId, String callTime) {
        //区域00-99
        int hash = (callerId + callTime.substring(0, 6)).hashCode();
        hash = (hash & Integer.MAX_VALUE) % 100; //转换为正数0~99
        DecimalFormat df = new DecimalFormat("00");//个位数前面补0
        return df.format(hash);
    }
}
