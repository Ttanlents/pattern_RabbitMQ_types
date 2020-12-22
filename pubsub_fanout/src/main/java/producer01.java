import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 余俊锋
 * @date 2020/12/22 17:56
 * @Description
 */
public class producer01 {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setHost("192.168.190.132");
        factory.setPort(5672);
        factory.setVirtualHost("/admin");

        String exchangeName = "test_fanout";

        // 2.创建连接
        Connection connection = factory.newConnection();

        // 3.创建频道
        Channel channel = connection.createChannel();
        //4. 创建交换机
        /*
           exchangeDeclare(String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments)
           1. exchange:交换机名称
           2. type:交换机类型
               DIRECT("direct"),：定向
               FANOUT("fanout"),：扇形（广播），发送消息到每一个与之绑定队列。
               TOPIC("topic"),通配符的方式
               HEADERS("headers");参数匹配
           3. durable:是否持久化
           4. autoDelete:自动删除
           5. internal：内部使用。 一般false
           6. arguments：参数
        */
        String queue1="test_fanout_queue1";
        String queue2="test_fanout_queue2";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT,true,false,false,null);

        //5创建队列
        channel.queueDeclare(queue1,true,false,false,null);
        channel.queueDeclare(queue2,true,false,false,null);
        //6. 绑定队列和交换机  重点
        /*
        queueBind(String queue, String exchange, String routingKey)
        参数：
            1. queue：队列名称
            2. exchange：交换机名称
            3. routingKey：路由键，绑定规则
                如果交换机的类型为fanout ，routingKey设置为""
         */
        //重点  绑定
        String body = "呼叫各位";
        channel.queueBind(queue1,exchangeName,"");
        channel.queueBind(queue2,exchangeName,"");
        //7.将消息推到交换机
        channel.basicPublish(exchangeName,"",null,body.getBytes());
        //9. 释放资源
        channel.close();
        connection.close();

    }
}
