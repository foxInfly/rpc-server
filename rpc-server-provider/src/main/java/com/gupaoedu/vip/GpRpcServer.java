package com.gupaoedu.vip;

import com.gupaoedu.vip.registry.IRegistryCenter;
import com.gupaoedu.vip.registry.RegistryCenterWithZk;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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

    private IRegistryCenter registryCenter=new RegistryCenterWithZk();

    public GpRpcServer(int port) {
        this.port = port;
    }

    /**
     * 属性设置完会调用
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //===============socket=======================
    /*    ServerSocket serverSocket = null;
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
*/
        //===============netty=======================
        //接收客户端的链接
        NioEventLoopGroup boossGroup = new NioEventLoopGroup();
        //处理已经接收的链接
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                    .addLast(new ObjectEncoder())
                                    .addLast(new ProcessorHandler(handlerMap));
                        }
                    });

            //正式启动服务，相当于用一个死循环开始轮询
            ChannelFuture future = serverBootstrap.bind(this.port).sync();
//            System.out.println("GP RPC Registry start listen at " + this.port);
//            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /* workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();*/
        }

    }

    /**
     * spring启动的时候去扫描，创建对象（要注册的暴露出去的对象）
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                //拿到注解
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();//接口类,类的全路径
                String version = rpcService.version();//版本号
                if (!StringUtils.isEmpty(version)) {
                    serviceName += "-" + version;
                }
                handlerMap.put(serviceName,serviceBean);
                registryCenter.registry(serviceName,getAddress()+":"+port);
            }
        }
        for (String key : handlerMap.keySet()) {
           System.out.println("已初始化 ==> 服务名： "+ key + " ， 对象： " + handlerMap.get(key));
          }
    }

    /**获取当前服务器的ip地址
     * @author lipu
     * @since 2020/9/20 21:35
     */
    private static String getAddress(){
        InetAddress inetAddress=null;
        try {
            inetAddress= InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return inetAddress.getHostAddress();// 获得本机的ip地址
    }
}
