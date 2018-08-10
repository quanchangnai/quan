package quan.network.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quan.network.handler.Handler;

import java.net.SocketOption;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于Socket的网络节点
 * Created by quanchangnai on 2017/7/1.
 */
public abstract class Bootstrap {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected String ip;

    protected int port;

    protected volatile boolean running;

    protected Handler handler;

    protected Map<SocketOption<?>, Object> socketOptions = new LinkedHashMap<>();

    protected int readBufferSize = 1024;

    protected int writeBufferSize = 1024;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isRunning() {
        return this.running;
    }

    protected void setRunning(boolean running) {
        this.running = running;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public <T> void setSocketOption(SocketOption<T> option, T value) {
        if (value == null) {
            socketOptions.remove(option);
        } else {
            socketOptions.put(option, value);
        }
    }

    public Map<SocketOption<?>, Object> getSocketOptions() {
        return socketOptions;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setWriteBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
    }

    public abstract void start();

    public abstract void stop();

}
