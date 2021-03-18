import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * @Author xzw
 * @Date 2021/3/18
 */
public class ConsumerDirectExchange {
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
        //3.通过连接创建一个通道
        Channel channel = connection.createChannel();

        String exchangeName = "test_direct_exchange";
        String exchangeType = "direct";
        String queueName = "test_direct_queue";
        String routeKey = "test.direct";

        //声明一个路由
        channel.exchangeDeclare(exchangeName,exchangeType,true,false,false,null);
        //声明一个队列
        channel.queueDeclare(queueName,false,false,false,null);
        //建立一个绑定，路由和队列之间的规则
        channel.queueBind(queueName,exchangeName,routeKey);

        //durable 是否持久化消息
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        //参数：队列名称,是否自动ACK,Consumer
        channel.basicConsume(queueName,true,queueingConsumer);

        //循环获取消息
        while (true){
            //获取消息,如果没有消息,这一步将会一直阻塞
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println(msg);
        }
    }
}
