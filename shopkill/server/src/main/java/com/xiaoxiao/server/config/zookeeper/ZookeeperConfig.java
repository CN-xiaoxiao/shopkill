package com.xiaoxiao.server.config.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * zookeeper配置
 */
@Configuration
public class ZookeeperConfig {
    @Autowired
    private Environment env;
    @Bean
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(env.getProperty("zk.host"))
                .namespace(env.getProperty("zk.namespace"))
                .retryPolicy(new RetryNTimes(5, 1000))
                .build();

        curatorFramework.start();
        return curatorFramework;
    }
}
