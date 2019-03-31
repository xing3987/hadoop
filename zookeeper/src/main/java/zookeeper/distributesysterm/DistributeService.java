package zookeeper.distributesysterm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * 创建一个业务服务,当请求时返回请求的时间
 */
public class DistributeService extends Thread{

    private Integer port;

    public DistributeService(int port) {
        this.port=port;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss=new ServerSocket(port);//创建socket服务器，绑定端口
            while (true){
                Socket sc = ss.accept();
                OutputStream outputStream = sc.getOutputStream();
                outputStream.write(new Date().toString().getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
