import com.rabbitmq.client.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 余俊锋
 * @date 2020/12/22 20:03
 * @Description
 */
public class produce04 {
    public static void main(String[] args) throws Exception {
        // 创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.190.132");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/admin");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /*
            定义一个交换机为header类型
            true:代表持久化
            true:代自动删除(没有consumer时自动销毁)
            false:代表不是内部使用的交换机
            null:传递的参数为null
         */
        String exchangeName = "test_header";

        // 声明交换机和类型headers
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.HEADERS,true,true,false,null);
        String body = "header....";

        Map<String,Object> headers =  new HashMap<>();
        headers.put("key1", "147");
       headers.put("key2", "258");
        headers.put("key3", "369");


        AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();
        properties.headers(headers);

        // 将消息直接发送给交换机(携带指定的参数header),让交换机自己根据header条件去转发到指定queue
        channel.basicPublish(exchangeName, "",properties.build(),body.getBytes());

        channel.close();
        connection.close();
    }
}
