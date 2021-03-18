import com.rabbitmq.client.*;

import java.util.HashMap;
import java.util.Map;

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

        Map<String,Object> headers = new HashMap<>();
        headers.put("my1","11111");
        headers.put("my2","2222");

        //10秒不消费 消息过期移除消息队列
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .contentEncoding("utf-8")
                .expiration("30000")
                .headers(headers)
                .build();

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
            channel.basicPublish("", "test", properties, msg.getBytes());
        }
        //5.记得关闭相关的连接
        channel.close();
        connection.close();
    }
}
