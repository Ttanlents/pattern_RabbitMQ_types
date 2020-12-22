import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 余俊锋
 * @date 2020/12/22 18:30
 * @Description
 */
public class producer02 {
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

        String exchangeName = "test_direct";

        //5. 创建交换机(routing模式)
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT,true,false,false,null);
        //6. 创建队列
        String queue1Name = "test_direct_queue1";
        String queue2Name = "test_direct_queue2";

        channel.queueDeclare(queue1Name,true,false,false,null);
        channel.queueDeclare(queue2Name,true,false,false,null);
        //7. 绑定队列和交换机
        /*
        queueBind(String queue, String exchange, String routingKey)
        参数：
            1. queue：队列名称
            2. exchange：交换机名称
            3. routingKey：路由键，绑定规则
                如果交换机的类型为fanout ，routingKey设置为""
         */
        //队列1绑定 red,green
        channel.queueBind(queue1Name,exchangeName,"red");
        channel.queueBind(queue1Name,exchangeName,"green");

        //队列2绑定 red,blue
        channel.queueBind(queue2Name,exchangeName,"red");
        channel.queueBind(queue2Name,exchangeName,"blue");

        String body = "routing....";
        //8. 发送消息(路由规则为red,很明显两个队列都能接受到消息)
        channel.basicPublish(exchangeName,"red",null,body.getBytes());

        //9. 释放资源
        channel.close();
        connection.close();
    }
}
