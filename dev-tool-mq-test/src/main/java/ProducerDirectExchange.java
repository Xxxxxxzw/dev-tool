import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @Author xzw
 * @Date 2021/3/18
 */
public class ProducerDirectExchange {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.3.30");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
        connectionFactory.setHandshakeTimeout(20000);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_direct_exchange";
        String routeKey = "test.direct";
        String msg = "This is a Producer Direct Exchange Test!RabbitMQ4~";
        channel.basicPublish(exchangeName,routeKey,null,msg.getBytes());

    }
}
