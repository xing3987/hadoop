package zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * zookeeper客户端java操作
 */
public class ZookeeperClientDemo {
    private ZooKeeper zooKeeper = null;

    @Before
    public void init() throws Exception {
        //构建一个连接zookeeper客户端的对象  1.连接ip:port可以多个 2.超时时间 3.自定义的监听处理(没有为null)，默认只会执行一次
        zooKeeper = new ZooKeeper("hadoop001:2181,hadoop002:2181,hadoop003:2181", 2000, new WatchDemo());
    }

    @Test
    public void create() throws Exception {
        //创建数据 1.目录（path） 2.数据  3.权限  4.创建模式持久或者短暂
        String create = zooKeeper.create("/clientdemo", "clientdemo".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("create:" + create);
    }

    @Test
    public void update() throws Exception {
        //1.路径  2.更新的数据 3.版本（-1代表任何版本）
        zooKeeper.setData("/clientdemo", "setnewdata".getBytes(), -1);
    }

    @Test
    public void select() throws Exception {
        //1.路径  2.是否监听 3.版本
        byte[] bytes = zooKeeper.getData("/clientdemo", false, null);
        System.out.println("******getdatas:" + new String(bytes, "UTF-8"));
    }

    @Test
    public void selectchild() throws Exception {
        //查询得到子目录：1.路径  2.是否监听
        List<String> children = zooKeeper.getChildren("/aa", false);
        if (children != null) {
            for (String child : children) {
                System.out.println("child :" + child);
            }
        }
    }

    @Test
    public void delete() throws Exception{
        //删除：1.路径  2.版本
        zooKeeper.delete("/clientdemo",-1);
    }

    @Test
    public void watch() throws Exception{
        //添加监听事件，默认只会执行一次，如果要反复执行，可以在监听事件中再次添加监听事件
        byte[] data = zooKeeper.getData("/clientdemo", new WatchDemo(zooKeeper), null);//设置查询的监听
        System.out.println(new String(data,"UTF-8"));
        Thread.sleep(Integer.MAX_VALUE);//使线程处于等待，以得到监听结果
    }

    @After
    public void close() throws Exception {
        zooKeeper.close();
    }
}
