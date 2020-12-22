import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * @author 余俊锋
 * @date 2020/12/22 19:32
 * @Description
 */
public class RPCClient implements AutoCloseable {
    public Connection connection;
    public Channel channel;
    public String requestQueueName = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.190.132");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/admin");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public static void main(String[] argv) throws Exception {
        // 初始化信息
        RPCClient rpcClient = new RPCClient();
        // 发起远程调用
        Integer response = rpcClient.call(20);

        System.out.println(response);

        rpcClient.channel.close();
        rpcClient.connection.close();
    }

    public Integer call(Integer money) throws IOException, InterruptedException {

        // 随机生成一个correlationId
        final String corrId = UUID.randomUUID().toString();

        // 服务端回调的队列名(随机生成)    定义好回调的队列
        String replyQueueName = this.channel.queueDeclare().getQueue();

        // 设置发送消息的一些参数
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        //推送消息和参数  到rpc_queue队列
        // 发送一个消息给客户端
                            /*
                            properties.getReplyTo(): Client端设置的回调队列名
                            props:封装的参数(主要是CorrelationId)
                             */
        channel.basicPublish("", requestQueueName, props, (money + "").getBytes("UTF-8"));

        final BlockingQueue<Integer> response = new ArrayBlockingQueue<>(1);

        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {

            // 回调方法,当收到消息之后,会自动执行该方法
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                if (properties.getCorrelationId().equals(corrId)) {
                    System.out.println("响应的消息：" + new String(body));

                    response.offer(Integer.parseInt(new String(body, "UTF-8")));
                }
            }
        });

        return response.take();
    }

    public void close() throws IOException {
        connection.close();
    }
}
