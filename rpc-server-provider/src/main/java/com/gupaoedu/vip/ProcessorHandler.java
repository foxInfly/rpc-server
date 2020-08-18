package com.gupaoedu.vip;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 发布服务
 * @author : lipu
 * @since : 2020-08-18 21:21
 */
public class ProcessorHandler implements Runnable {

    private Socket socket;
    private Object service;

    public ProcessorHandler(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            //输入流包含的内容：请求的目标类，方法名称，参数
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = invoke(rpcRequest);

            //把实现类对象返回给客户端
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 生成指定的对象
     * @param rpcRequest 反序列化后的对象
     * @return 通过反序列化对象，利用反射生成的对象
     */
    private Object invoke(RpcRequest rpcRequest) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //反射调用
        Object[] args = rpcRequest.getParameters();
        Class<?>[] types = new Class[args.length];//获取参数对应的类型
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        Class<?> clazz = Class.forName(rpcRequest.getClassName());//根据请求的类进行加载HelloServiceImpl
        Method method = clazz.getMethod(rpcRequest.getMethodName(),types);//sayHello,saveUser
        Object result = method.invoke(service, args);//HelloServiceImpl

        return result;
    }
}
