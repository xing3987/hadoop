package zookeeper.distributesysterm;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DistributeConsumer {
    //定义存放数据的列表
    private List<String> servers = new ArrayList<>();

    private ZooKeeper zk = null;

    /*
     * 创建连接
     */
    public void connect() throws Exception {
        //创建连接，并添加监听事件,只要节点有变化，就输出当前在线的服务器
        zk = new ZooKeeper("hadoop001:2181,hadoop002:2181,hadoop003:2181", 2000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeDataChanged
                        && event.getState() == Event.KeeperState.SyncConnected) {
                    try {
                        System.out.println("有新的服务器上线...");
                        getServersOnline();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * 得到在线的服务器列表并保存到list中
     *
     * @throws Exception
     */
    public void getServersOnline() throws Exception {
        System.out.println("开始查询当前在线的服务器...");
        //得到服务器的路径，从而得到内容,并添加监听事件
        List<String> children = zk.getChildren("/servers", true);
        servers.clear();//清空列表，用于存入最新数据
        for (String child : children) {
            //得到路径下的数据，不需要监听，版本为最新的
            byte[] data = zk.getData("/servers/" + child, false, null);
            String datas = new String(data, "UTF-8");
            servers.add(datas);
        }

        System.out.println("当前在线的服务器有：" + servers);
    }

    /**
     * 中list中挑选一个服务器并发送请求
     *
     * @throws Exception
     */
    public void sentRequest() {
        Random random = new Random();
        while (true) {
            OutputStream outputStream = null;
            InputStream inputStream = null;
            Socket sc = null;
            try {
                int nextInt = random.nextInt(servers.size());
                System.out.println("本次的服务器为：" + servers.get(nextInt));
                String host = servers.get(nextInt).split(":")[0];
                String port = servers.get(nextInt).split(":")[1];
                sc = new Socket(host, Integer.parseInt(port));//创建socket连接
                outputStream = sc.getOutputStream();
                outputStream.write("hello".getBytes());
                outputStream.flush();//写出数据

                inputStream = sc.getInputStream();
                byte[] buf = new byte[256];
                int read = inputStream.read(buf);
                System.out.println("服务器响应的时间为：" + new String(buf, 0, read));

                inputStream.close();
                outputStream.close();
                sc.close();

                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        DistributeConsumer consumer = new DistributeConsumer();
        consumer.connect();
        consumer.getServersOnline();
        consumer.sentRequest();
    }
}
