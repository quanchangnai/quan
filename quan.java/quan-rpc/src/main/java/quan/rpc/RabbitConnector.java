package quan.rpc;

import com.rabbitmq.client.*;
import quan.message.CodedBuffer;
import quan.message.DefaultCodedBuffer;
import quan.rpc.protocol.*;
import quan.rpc.serialize.ObjectWriter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于RabbitMQ的网络连接器
 *
 * @author quanchangnai
 */
public class RabbitConnector extends Connector {

    private String namePrefix = "";

    private ConnectionFactory connectionFactory;

    private Connection connection;

    private ThreadLocal<Channel> channel;

    private Map<Integer, Remote> remotes = new ConcurrentHashMap<>();

    private Set<Integer> remoteIds = Collections.unmodifiableSet(remotes.keySet());

    private ScheduledExecutorService executor;

    /**
     * 构造基于基于RabbitMQ的网络连接器
     *
     * @param connectionFactory RabbitMQ连接工厂
     * @param namePrefix        RabbitMQ交换机和队列的名称前缀，需要互连的服务器一定要保持一致
     */
    public RabbitConnector(ConnectionFactory connectionFactory, String namePrefix) {
        connectionFactory.useNio();
        this.connectionFactory = connectionFactory;
        if (namePrefix != null) {
            this.namePrefix = namePrefix;
        }
    }

    /**
     * @see #RabbitConnector(ConnectionFactory, String)
     */
    public RabbitConnector(ConnectionFactory connectionFactory) {
        this(connectionFactory, null);
    }

    @Override
    protected void start() {
        try {
            connection = connectionFactory.newConnection();
        } catch (Exception e) {
            throw new RuntimeException("创建连接失败", e);
        }

        executor = Executors.newScheduledThreadPool(localServer.getWorkerNum());

        channel = ThreadLocal.withInitial(this::initChannel);
        initChannel();

        for (Remote remote : remotes.values()) {
            remote.start();
        }
    }

    @Override
    protected void stop() {
        channel = null;
        remotes.values().forEach(Remote::stop);
        remotes.clear();
        connection.abort();
        connection = null;
        executor.shutdown();
        executor = null;
    }

    @Override
    protected void update() {
        remotes.values().forEach(Remote::update);
    }

    protected String exchangeName(int serverId) {
        return namePrefix + serverId;
    }

    protected String queueName(int serverId) {
        return namePrefix + serverId;
    }

    private Channel initChannel() {
        try {
            Channel channel = connection.createChannel();
            String exchangeName = exchangeName(localServer.getId());
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, false, true, null);

            String queueName = queueName(localServer.getId());
            Map<String, Object> queueArgs = new HashMap<>();
            queueArgs.put("x-message-ttl", localServer.getCallTtl() * 1000);//设置队列里消息的过期时间
            channel.queueDeclare(queueName, false, true, true, queueArgs);
            channel.queueBind(exchangeName, queueName, "");

            channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    try {
                        CodedBuffer buffer = new DefaultCodedBuffer(body);
                        Protocol protocol = localServer.getReaderFactory().apply(buffer).read();
                        handleProtocol(protocol);
                    } catch (Exception e) {
                        logger.error("处理协议出错", e);
                    }
                }
            });

            return channel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addRemote(int remoteId) {
        if (remotes.containsKey(remoteId) || localServer != null && localServer.hasRemote(remoteId)) {
            logger.error("远程服务器[{}]已存在", remoteId);
            return false;
        }

        Remote remote = new Remote(remoteId, this);
        remotes.put(remoteId, remote);
        if (localServer != null && localServer.isRunning()) {
            remote.start();
        }

        return true;
    }

    public boolean removeRemote(int remoteId) {
        Remote remote = remotes.remove(remoteId);
        if (remote != null) {
            remote.stop();
        }
        return remote != null;
    }

    @Override
    public boolean isRemoteActivated(int remoteId) {
        Remote remote = remotes.remove(remoteId);
        return remote != null && remote.activated;
    }

    @Override
    public Set<Integer> getRemoteIds() {
        return remoteIds;
    }

    @Override
    protected void sendProtocol(int remoteId, Protocol protocol) {
        checkRemote(remoteId, protocol);
        Worker worker = Worker.current();

        executor.execute(() -> {
            try {
                //异步发送，防止阻塞工作线程
                publishProtocol(remoteId, protocol);
            } catch (Exception e) {
                if (protocol instanceof Request) {
                    long callId = ((Request) protocol).getCallId();
                    worker.execute(() -> worker.handlePromise(callId, e, null));
                } else {
                    logger.error("发送协议出错，{}", protocol, e);
                }

            }
        });
    }

    private void checkRemote(int remoteId, Protocol protocol) {
        Remote remote = remotes.get(remoteId);
        if (remote == null) {
            throw new IllegalArgumentException(String.format("远程服务器[%s]不存在", remoteId));
        }

        if (!remote.activated && !(protocol instanceof Handshake)) {
            throw new IllegalStateException(String.format("远程服务器[%s]的连接还未建立", remoteId));
        }
    }

    private void publishProtocol(int remoteId, Protocol protocol) {
        try {
            CodedBuffer buffer = new DefaultCodedBuffer();
            ObjectWriter objectWriter = localServer.getWriterFactory().apply(buffer);
            objectWriter.write(protocol);
            channel.get().basicPublish(exchangeName(remoteId), "", null, buffer.remainingBytes());
        } catch (Exception e) {
            if (e instanceof AlreadyClosedException) {
                channel.remove();
            }
            throw new RuntimeException(String.format("发送协议到远程服务器[%s]出错", remoteId), e);
        }
    }

    protected void handleProtocol(Protocol protocol) {
        if (protocol instanceof Handshake) {
            handleHandshake((Handshake) protocol);
        } else if (protocol instanceof PingPong) {
            Remote remote = remotes.get(protocol.getServerId());
            if (remote != null) {
                remote.handlePingPong((PingPong) protocol);
            }
        } else if (protocol instanceof Request) {
            localServer.handleRequest((Request) protocol);
        } else if (protocol instanceof Response) {
            localServer.handleResponse((Response) protocol);
        } else {
            logger.error("收到非法RPC协议:{}", protocol);
        }
    }

    protected void sendHandshake(int remoteId, int param) {
        Handshake handshake = new Handshake(localServer.getId(), param);
        if (param != 3) {
            checkRemote(remoteId, handshake);
        }
        publishProtocol(remoteId, handshake);
    }

    protected void handleHandshake(Handshake handshake) {
        int remoteId = handshake.getServerId();
        Integer param = handshake.getParam(0);

        if (param == 1) {
            Remote remote = remotes.get(remoteId);
            if (remote == null) {
                if (localServer.hasRemote(remoteId)) {
                    sendHandshake(remoteId, 3);
                } else {
                    addRemote(remoteId);
                    remote = remotes.get(remoteId);
                    remote.passive = true;
                    remote.activated = true;
                }
            } else {
                remote.activated = true;
            }
        }

        if (param != 1 && remotes.containsKey(remoteId)) {
            removeRemote(remoteId);
            if (param == 2) {
                logger.info("远程服务器[{}]已删除，原因：服务器[{}]在远程被关闭", remoteId, localServer.getId());
            } else {
                logger.error("远程服务器[{}]已删除，原因：服务器[{}]在远程已存在", remoteId, localServer.getId());
            }

        }
    }

    private static class Remote {

        /**
         * 远程服务器ID
         */
        private int id;

        protected RabbitConnector connector;

        //被动添加的
        private volatile boolean passive;

        private volatile boolean activated;

        private volatile boolean stopped;

        private long lastSendPingPongTime;

        private long lastHandlePingPongTime = System.currentTimeMillis();

        public Remote(int id, RabbitConnector connector) {
            this.id = id;
            this.connector = connector;
        }

        public void start() {
            if (activated || stopped) {
                return;
            }

            Throwable throwable = null;

            try {
                connector.channel.get().exchangeDeclarePassive(connector.exchangeName(id));
            } catch (IOException e) {
                throwable = e.getCause();
            } catch (Throwable e) {
                throwable = e;
            }

            if (throwable == null && !passive) {
                try {
                    connector.sendHandshake(id, 1);
                } catch (Exception e) {
                    throwable = e;
                }
            }

            if (throwable != null) {
                logger.error("连接远程服务器[{}]失败，将在{}毫秒后尝试重连，失败原因：{}", id, connector.getReconnectInterval(), throwable.getMessage());
                connector.channel.remove();
            }

            //没有报错也需要再试一次，因为有可能握手收不到回应
            connector.executor.schedule(this::start, connector.getReconnectInterval(), TimeUnit.SECONDS);
        }

        public void stop() {
            stopped = true;
            if (activated) {
                activated = false;
                try {
                    connector.sendHandshake(id, 2);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }

        protected void update() {
            if (activated) {
                try {
                    checkActivated();
                    sendPingPong();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }

        public void restart() {
            activated = false;
            stopped = false;
            start();
        }

        protected void checkActivated() {
            long currentTime = System.currentTimeMillis();
            if (lastHandlePingPongTime == 0 || currentTime - lastHandlePingPongTime < connector.getPingPongInterval() * 2) {
                return;
            }

            activated = false;

            if (passive) {
                logger.info("远程服务器[{}]连接已断开", id);
                connector.removeRemote(id);
            } else {
                logger.error("远程服务器[{}]连接已断开，将在{}毫秒后尝试重连", id, connector.getReconnectInterval());
                connector.executor.schedule(this::restart, connector.getReconnectInterval(), TimeUnit.MILLISECONDS);
            }
        }

        protected void sendPingPong() {
            long currentTime = System.currentTimeMillis();
            if (lastSendPingPongTime + connector.getPingPongInterval() < currentTime) {
                PingPong pingPong = new PingPong(connector.localServer.getId(), currentTime);
                connector.checkRemote(id, pingPong);
                connector.publishProtocol(id, pingPong);
                lastSendPingPongTime = currentTime;
            }
        }

        protected void handlePingPong(PingPong pingPong) {
            lastHandlePingPongTime = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("远程服务器[{}]的延迟时间为{}毫秒", id, lastHandlePingPongTime - pingPong.getTime());
            }
        }

    }

}
