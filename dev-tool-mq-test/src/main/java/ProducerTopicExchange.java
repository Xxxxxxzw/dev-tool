import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @Author xzw
 * @Date 2021/3/18
 */
public class ProducerTopicExchange {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.3.30");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHandshakeTimeout(20000);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_topic_exchange";
        String routeKey1 = "test.abc";
        String routeKey2 = "test.bbc";
        String routeKey3 = "test.delete.add";

        String msg = "This is a Producer Top Exchange Test!RabbitMQ5~~~~你好";
        channel.basicPublish(exchangeName,routeKey1,null,msg.getBytes());
        channel.basicPublish(exchangeName,routeKey2,null,msg.getBytes());
        channel.basicPublish(exchangeName,routeKey3,null,msg.getBytes());
    }
}
