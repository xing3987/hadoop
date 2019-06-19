package elasticsearch;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ESCRUD {
    private TransportClient client = null;

    @Before
    public void init() throws Exception {
        //设置集群名称
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                //自动感知的功能（可以通过当前指定的节点获取所有es节点的信息）
                .put("client.transport.sniff", true)
                .build();
        //创建client
        client = new PreBuiltTransportClient(settings).addTransportAddresses(
                new InetSocketTransportAddress(InetAddress.getByName("ubuntu"), 9300));

    }

    @Test
    public void testCreate() throws Exception {
        client.prepareIndex("gamelog", "user", "1")
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("username", "u1")
                                .field("gender", "male")
                                .field("birthday", new Date())
                                .field("fv", 9999)
                                .field("message", "trying out es.")
                                .endObject()
                ).get();

    }

    //查找一条
    @Test
    public void testGet() throws IOException {
        GetResponse response = client.prepareGet("gamelog", "user", "1").get();
        System.out.println(response.getSourceAsString());
    }

    //查找多条
    @Test
    public void testMultiGet() throws IOException {
        MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
                .add("gamelog", "user", "1")
                .add("gamelog", "user", "2", "3")
                .add("store", "books", "4")
                .get();

        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()) {
                String json = response.getSourceAsString();
                System.out.println(json);
            }
        }
    }
}
