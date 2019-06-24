package flume;

import org.apache.commons.io.FileUtils;
import org.apache.flume.Context;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * flume source 的生命周期：构造器 -> configure -> start -> processor.process
 * 1.读取配置文件：（配置文件的内容：读取哪个文件、编码集、偏移量写到哪个文件、多长时间检查一下文件是否有新内容）
 */
public class TailFileSource extends AbstractSource implements EventDrivenSource, Configurable {

    private String filePath;
    private String charset;
    private String posiFile;
    private Long interval;
    private ExecutorService executor;
    private static final Logger logger = LoggerFactory.getLogger(TailFileSource.class);
    private FileRunnable fileRunnable;

    @Override
    public void configure(Context context) {
        filePath = context.getString("filePath");
        charset = context.getString("charset", "UTF-8");
        posiFile = context.getString("posiFile");
        interval = context.getLong("interval", 1000L);
    }

    @Override
    public synchronized void start() {
        executor = Executors.newSingleThreadExecutor();//创建一个单线程的线程池
        //定义一个实现runnable接口的类
        fileRunnable = new FileRunnable(filePath, interval, charset, getChannelProcessor(), posiFile);
        executor.submit(fileRunnable);//提交任务
        //调用父类的start方法
        super.start();
    }

    @Override
    public synchronized void stop() {
        fileRunnable.setFlag(false);
        executor.shutdown();
        //如果executor停不了
        while (!executor.isTerminated()) {
            logger.debug("Waiting for filer executor service to stop");
            try {
                executor.awaitTermination(500, TimeUnit.MILLISECONDS);//等等0.5s
            } catch (InterruptedException e) {
                logger.debug("Interrupted while waiting for exec executor service "
                        + "to stop. Just exiting.");
                Thread.currentThread().interrupt();
            }
        }
        super.stop();
    }

    private static class FileRunnable implements Runnable {

        private long interval;
        private String charset;
        private ChannelProcessor channelProcessor;//用来把source发送给channel
        private long offset = 0L;
        private RandomAccessFile raf;
        private boolean flag = true;
        private File positionFile;

        private FileRunnable(String filePath, long interval, String charset, ChannelProcessor channelProcessor, String posiFile) {
            this.interval = interval;
            this.charset = charset;
            this.channelProcessor = channelProcessor;
            this.positionFile = new File(posiFile);
            if (!positionFile.exists()) {
                try {
                    positionFile.createNewFile();
                } catch (IOException e) {
                    //e.printStackTrace();
                    logger.error("create position file error", e);
                }
            }
            //读取偏移量
            try {
                String offsetString = FileUtils.readFileToString(positionFile);
                //如果以前记录过偏移量
                if (offsetString != null && !"".equals(offsetString)) {
                    //将当前的偏移量转换成long
                    offset = Long.parseLong(offsetString);
                }
                //读取log文件是从指定的位置读取数据
                raf = new RandomAccessFile(filePath, "r");
                //按照指定的偏移量读取
                raf.seek(offset);
            } catch (IOException e) {
                //e.printStackTrace();
                logger.error("read position file error", e);
            }
        }

        @Override
        public void run() {
            while (flag) {
                try {
                    String line = raf.readLine();
                    if (line != null) {
                        line = new String(line.getBytes("ISO-8859-1"), charset);
                        //将数据发送给Channel
                        channelProcessor.processEvent(EventBuilder.withBody(line.getBytes()));
                        //获取最新的偏移量，然后更新偏移量
                        offset = raf.getFilePointer();
                        //将偏移量写入到位置文件中
                        FileUtils.writeStringToFile(positionFile, offset + "");
                    }else{
                        Thread.sleep(interval);
                    }
                } catch (IOException e) {
                    logger.error("read log file error", e);
                } catch (InterruptedException e) {
                    logger.error("read file thread interrupted", e);
                }

            }
        }

        private void setFlag(boolean flag) {
            this.flag = flag;
        }
    }
}
