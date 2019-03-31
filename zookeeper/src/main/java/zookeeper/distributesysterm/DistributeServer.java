package zookeeper.distributesysterm;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 编写一个分布式系统，实现获取服务器上下线的动态感知
 */
public class DistributeServer {
    private ZooKeeper zk = null;

    /*
     * 创建连接
     */
    public void connect() throws Exception {
        zk = new ZooKeeper("hadoop001:2181,hadoop002:2181,hadoop003:2181", 2000, null);

    }

    /*
        注册服务器
     */
    public void registerServerInfo(String hostname, String port) throws Exception {
        //如果注册目录不存在，则创建一个持久的目录
        Stat stat = zk.exists("/servers", false);
        if (stat == null) {
            zk.create("/servers", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        //创建一个节点，开发权限，短暂有序的
        String path = zk.create("/servers/server", (hostname + ":" + port).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println("注册节点成功，注册节点为："+path);
    }

    public static void main(String[] args) throws Exception{
        DistributeServer ds=new DistributeServer();
        ds.connect();
        ds.registerServerInfo(args[0],args[1]);
        new DistributeService(Integer.parseInt(args[1])).start();
    }
}
