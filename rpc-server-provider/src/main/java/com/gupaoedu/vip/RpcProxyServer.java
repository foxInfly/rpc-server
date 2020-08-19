package com.gupaoedu.vip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 暴露服务
 * @author : lipu
 * @since : 2020-08-18 20:55
 */
public class RpcProxyServer {
    ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 暴露服务出去
     * @param service 要暴露的服务实现类
     * @param port 暴露的端口，能进来
     */
    /*public void publisher(Object service, int port){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true){
                Socket socket = serverSocket.accept();
                //每个socket交给一个processorHandler处理
                executorService.execute(new ProcessorHandler(socket,service));
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
    }*/
}
