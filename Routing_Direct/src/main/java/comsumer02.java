import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 余俊锋
 * @date 2020/12/22 18:37
 * @Description
 */
public class comsumer02 {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂,用于获取频道channel
        ConnectionFactory factory=new ConnectionFactory();

        factory.setHost("192.168.190.132");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/admin");

        // 2.创建连接
        Connection connection = factory.newConnection();

        // 3.创建频道
        Channel channel = connection.createChannel();

        // 定义队列
        String queueName = "test_direct_queue2";
        channel.queueDeclare(queueName,true,false,false,null);

        // 接收消息
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                System.out.println("body："+new String(body));
            }
        };

        // 接受queue1的消息
        channel.basicConsume(queueName,true,consumer);

        // 不释放资源,让rabbitmq一直监听
    }
}
