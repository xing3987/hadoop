package zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 创建一个监听事件，默认只会执行一次
 */
public class WatchDemo implements Watcher {
    private ZooKeeper zooKeeper = null;

    public WatchDemo() {
    }

    public WatchDemo(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent event) {

        //设定事件类型，不是初始化&&是节点发生变化是才会触发
        if (event.getState() == Event.KeeperState.SyncConnected
                && event.getType() == Event.EventType.NodeDataChanged) {
            System.out.println("********执行监听事件************");
            System.out.println(event.getPath()); //收到时间所发生的节点路径
            System.out.println(event.getType()); //事件的类型
            System.out.println("收到事件，设定处理逻辑....");
            System.out.println("********执行监听事件结束************");
            //添加一个反复执行的监听事件
            if (zooKeeper != null) {
                try {
                    zooKeeper.getData("/clientdemo", this::process, null);
                } catch (Exception e) {
                }
            }
        }
    }
}
