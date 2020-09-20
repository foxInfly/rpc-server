package com.gupaoedu.vip;

/**
 * @author : lipu
 * @since : 2020-08-18 20:42
 */
public interface IHelloService {

    String sayHello(double money);

    /**
     * 保存用户
     * @param user 用户
     * @return string
     */
    String saveUser(User user);
}
