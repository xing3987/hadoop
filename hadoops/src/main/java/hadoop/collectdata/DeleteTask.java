package hadoop.collectdata;

import hadoop.helper.Constants;
import hadoop.helper.PropertyHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class DeleteTask extends TimerTask {
    Logger logger=Logger.getLogger(DeleteTask.class);
    private final String COPY_FILE = PropertyHelper.getProperty(Constants.COPY_FILE);
    @Override
    public void run() {
        SimpleDateFormat sd=new SimpleDateFormat("yyyyMMdd");
        long now =new Date().getTime();
        File temporary=new File(COPY_FILE);
        File[] files=temporary.listFiles();
        try {
            //遍历删除一个月前的数据
            for (File file : files) {
                long time = sd.parse(file.getName()).getTime();
                if (now-time>30*24*60*60*1000l) {
                    FileUtils.deleteDirectory(file);
                    logger.debug("delete file:"+file.getName());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.debug(e.getStackTrace().toString());
        }
    }
}
