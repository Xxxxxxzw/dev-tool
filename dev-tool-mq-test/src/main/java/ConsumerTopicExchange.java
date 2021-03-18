import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * @Author xzw
 * @Date 2021/3/18
 */
public class ConsumerTopicExchange {
    public static void main(String[] args) throws Exception {
        //1.创建一个连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.3.30");
        connectionFactory.setPort(5672);
        connectionFactory.setHandshakeTimeout(20000);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);
        //2.通过连接工厂创建一个连接
        Connection connection = connectionFactory.newConnection();
        //3.通过连接创建一个渠道
        Channel channel = connection.createChannel();

        String exchangeName = "test_topic_exchange";
        String exchangeType = "topic";
        String routeKey = "test.#";
        String queueName = "test_topic_queue";

        //声明一个路由
        channel.exchangeDeclare(exchangeName,exchangeType,true,false,false,null);
        //声明一个队列
        channel.queueDeclare(queueName,false,false,false,null);
        //建立绑定
        channel.queueBind(queueName,exchangeName,routeKey);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName,true,consumer);

        while (true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("消息："+msg + ", routeKey:" + delivery.getEnvelope().getRoutingKey());
        }
    }
}
