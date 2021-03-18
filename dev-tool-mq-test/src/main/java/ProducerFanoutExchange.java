import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @Author xzw
 * @Date 2021/3/18
 */
public class ProducerFanoutExchange {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.3.30");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHandshakeTimeout(20000);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_fanout_exchange";
        String routeKey = "";

        String msg = "This is a Producer Fanout Exchange Test!RabbitMQ666~~~";

        for(int i = 0 ;i < 10 ; i++){
            channel.basicPublish(exchangeName,routeKey,null,msg.getBytes());
        }

        channel.close();
        connection.close();
    }
}
