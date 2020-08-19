package com.gupaoedu.vip;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : lipu
 * @since : 2020-08-19 22:49
 */
@Component
public class GpRpcServer implements ApplicationContextAware, InitializingBean {
    ExecutorService executorService = Executors.newCachedThreadPool();

    private Map<String,Object> handlerMap = new HashMap<>();

    private int port;

    public GpRpcServer(int port) {
        this.port = port;
    }

    /**
     * 属性设置完会调用
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true){
                Socket socket = serverSocket.accept();
                //每个socket交给一个processorHandler处理
                executorService.execute(new ProcessorHandler(socket,handlerMap));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                //拿到注解
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();
                handlerMap.put(serviceName,serviceBean);
            }
        }
    }
}
