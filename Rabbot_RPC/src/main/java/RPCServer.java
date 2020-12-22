import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author 余俊锋
 * @date 2020/12/22 19:46
 * @Description
 */
public class RPCServer {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    // 总金额
    private static Integer money = 0;

    /**
     * 加钱方法
     * @param n
     * @return
     */
    private static Integer addMoney(int n) {
        money += n;
        return money;
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.190.132");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/admin");

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {

            channel.queueDeclare(RPC_QUEUE_NAME, true, false, false, null);

            System.out.println("等待客户端请求.....");

            while (true) {

                // 接受到客户端的请求(消息)
                channel.basicConsume(RPC_QUEUE_NAME, true, new DefaultConsumer(channel) {

                    // 回调方法,当收到消息之后,会自动执行该方法
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                                .Builder()
                                .correlationId(properties.getCorrelationId())
                                .build();

                        System.out.println("客户端的消息: "+new String(body,"UTF-8"));
                        String response = "";

                        try {
                            String message = new String(body, "UTF-8");

                            // 调用加钱方法
                            response = addMoney(Integer.parseInt(message)) + "";

                        } finally {

                            // 发送一个消息给客户端
                            /*
                            properties.getReplyTo(): Client端设置的回调队列名
                            replyProps:封装的参数(主要是CorrelationId)
                             */
                            channel.basicPublish("", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
                        }
                    }
                });
            }

        }
    }
}
