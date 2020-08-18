package com.gupaoedu.vip;

/**
 * @author : lipu
 * @since : 2020-08-18 20:50
 */
public class HelloServiceImpl implements IHelloService {
    @Override
    public String sayHello(String content) {
        System.out.println("request in:"+content);
        return "Say Hello:" + content;
    }

    @Override
    public String saveUser(User user) {
        System.out.println("request in saveUser:"+user);
        return "SUCCESS";
    }
}
