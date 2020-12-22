import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 余俊锋
 * @date 2020/12/22 18:16
 * @Description
 */
public class comsumer01 {
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接
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
        // 4.创建队列
        String queue1="test_fanout_queue1";
        String queue2="test_fanout_queue2";
        channel.queueDeclare(queue1,true,false,false,null);

        //5.创建消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("consumer-1 body：" + new String(body));
            }
        };
        channel.basicConsume(queue1,true,consumer);
        channel.basicConsume(queue2,true,consumer);
        //不释放资源，让rabbitmq一直监听

    }
}
