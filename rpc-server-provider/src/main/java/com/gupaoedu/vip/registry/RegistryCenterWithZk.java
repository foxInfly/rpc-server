package com.gupaoedu.vip.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * zookeeper实现注册中心
 *
 * @author lipu
 * @since  2020/9/20 21:19
 */
public class RegistryCenterWithZk implements IRegistryCenter{

    CuratorFramework curatorFramework =null;

    {
        //初始化zookeeper的连接， 会话超时时间是5s，衰减重试
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZkConfig.CONNECTION_STR).sessionTimeoutMs(5000).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).
                namespace("registry")//命名空间，就是根目录，后面创建的都在这里面
                .build();
        curatorFramework.start();
    }


    @Override
    public void registry(String serviceName, String serviceAddress) {
        //根据服务名和服务地址创建节点
        String servicePath="/"+serviceName;
        try {
            //判断节点是否存在，不存在创建持久化节点（服务名称）
            if(curatorFramework.checkExists().forPath(servicePath)==null){
                curatorFramework.create().creatingParentsIfNeeded().
                        withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            //serviceAddress: ip:port，服务地址：临时节点
            String addressPath=servicePath+"/"+serviceAddress;
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath);
            System.out.println("注册服务到zookeeper成功"+addressPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
