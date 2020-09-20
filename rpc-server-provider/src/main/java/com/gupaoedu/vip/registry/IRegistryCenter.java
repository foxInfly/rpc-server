package com.gupaoedu.vip.registry;

/**
 * 注册中心
 *
 * @author lipu
 * @since  2020/9/20 21:14
 */
public interface IRegistryCenter {

    /**
     * 服务注册
     * @param serviceName 服务注册名称
     * @param serviceAddress 服务注册地址
     */
    void registry(String serviceName, String serviceAddress);
}
