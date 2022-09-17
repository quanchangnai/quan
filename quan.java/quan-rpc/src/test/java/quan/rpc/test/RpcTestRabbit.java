package quan.rpc.test;

import com.rabbitmq.client.ConnectionFactory;
import quan.rpc.LocalServer;
import quan.rpc.RabbitConnector;

/**
 * @author quanchangnai
 */
public class RpcTestRabbit {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        RabbitConnector rabbitConnector = new RabbitConnector(connectionFactory);

        LocalServer localServer = new LocalServer(3, 5, rabbitConnector);
        rabbitConnector.addRemote(1);
        localServer.addService(new TestService2(2));
        localServer.start();
    }

}
