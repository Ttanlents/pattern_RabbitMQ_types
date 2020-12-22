import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 余俊锋
 * @date 2020/12/22 18:43
 * @Description
 */
public class produce03 {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂,用于获取频道channel
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("192.168.190.132");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/admin");

        // 2.创建连接
        Connection connection = factory.newConnection();

        // 3.创建频道
        Channel channel = connection.createChannel();

        String exchangeName = "test_topic";

        //5. 创建交换机
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC,true,false,false,null);

        //6. 创建队列
        String queue1Name = "test_topic_queue1";
        String queue2Name = "test_topic_queue2";
        String queue3Name = "test_topic_queue3";
        String queue4Name = "test_topic_queue4";

        channel.queueDeclare(queue1Name,true,false,false,null);
        channel.queueDeclare(queue2Name,true,false,false,null);
        channel.queueDeclare(queue3Name,true,false,false,null);
        channel.queueDeclare(queue4Name,true,false,false,null);

        //7. 绑定队列和交换机
        channel.queueBind(queue1Name,exchangeName,"red.#.green");
        channel.queueBind(queue2Name,exchangeName,"red.green.*");
        channel.queueBind(queue3Name,exchangeName,"#.green");
        channel.queueBind(queue4Name,exchangeName,"*.green.#");

        String body = "topic....";
        //8. 发送消息
        /*
            red.green.green			1/2/3/4
            green.green				3/4
            green.red.blue
            green.green.green		3/4
            green.red.green			3

            red.#.green
            red.green.*
            #.green
            *.green.#
         */
//        channel.basicPublish(exchangeName,"red.green.green",null,body.getBytes());
//        channel.basicPublish(exchangeName,"green.green",null,body.getBytes());
//        channel.basicPublish(exchangeName,"green.red.blue",null,body.getBytes());
//        channel.basicPublish(exchangeName,"green.green.green",null,body.getBytes());
        channel.basicPublish(exchangeName,"green.red.green",null,body.getBytes());

        //9. 释放资源
        channel.close();
        connection.close();
    }
}
