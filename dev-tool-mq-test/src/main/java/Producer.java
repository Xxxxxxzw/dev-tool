import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @Author xzw
 * @Date 2021/3/17
 */
public class Producer {
    public static void main(String[] args) throws Exception {
        //1.创建连接工厂并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.3.30");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHandshakeTimeout(20000);

        //2.通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        //3.通过Connection创建一个Channel
        Channel channel = connection.createChannel();

        /**
         * basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
         * exchange:指定交换机 不指定 则默认 （AMQP default交换机） 通过routingkey进行匹配
         * props 消息属性
         * body 消息体
         */
        //4.通过Channel发送数据
        for(int i = 0; i < 5; i++){
            System.out.println("生产消息："+ i);
            String msg = "Hello RabbitMQ" + i;
            channel.basicPublish("", "test", null, msg.getBytes());
        }
        //5.记得关闭相关的连接
        channel.close();
        connection.close();
    }
}
