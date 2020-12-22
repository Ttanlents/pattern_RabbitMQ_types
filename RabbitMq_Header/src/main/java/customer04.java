import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author 余俊锋
 * @date 2020/12/22 20:10
 * @Description
 */
public class customer04 {
    public static void main(String[] args) throws IOException, TimeoutException {
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
        channel.exchangeDeclare("test_header", BuiltinExchangeType.HEADERS,true,true,false,null);
        /*
            定义一个队列
            true:代表持久化
            false:不是独占(允许多个消费者监听此队列)
            true:自动删除(没有consumer时自动删除)
            null:没有其他要传递的参数
         */
        channel.queueDeclare("test_header_queue",true, false, true,null);

        Map<String, Object> headers = new HashMap<>();
        /*
            all:Producer必须匹配所有的键值对
            any:只要Producer匹配任意一个键值对即可
         */
        headers.put("x-match", "all");
        headers.put("key1", "147");
        headers.put("key2", "258");
        headers.put("key3", "369");

        // 为交换机指定队列，设置binding 绑定header键值对
        channel.queueBind("test_header_queue", "test_header","", headers);

        channel.basicConsume("test_header_queue", true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                System.out.println("body："+new String(body));
            }
        });
    }
    }

