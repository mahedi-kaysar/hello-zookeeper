package com.zookeeper.example;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ZookeeperClient {
    private ZooKeeper zooKeeper;
    ZookeeperClientConfig zookeeperClientConfig;
    public ZookeeperClient(ZookeeperClientConfig zookeeperClientConfig) throws IOException, InterruptedException {
        this.zookeeperClientConfig = zookeeperClientConfig;
        initialise();
    }

    private void initialise() throws IOException, InterruptedException {
        int countRetry = 0;
        while (true) {
            try {
                this.zooKeeper = new ZooKeeper(zookeeperClientConfig.getConnectionString(),
                        zookeeperClientConfig.getTimeout(), null);
                break;
            } catch (IOException e) {
                if (countRetry++ == zookeeperClientConfig.getMaxRetry()) {
                    System.out.println("Maximum retries reached");
                    throw e;
                }
                System.out.println("Failed to connect to ZooKeeper, retrying in " + zookeeperClientConfig.getRetryDelay() + "ms...");
                Thread.sleep(zookeeperClientConfig.getRetryDelay());
            }
        }
    }

    public void closeConnection() throws InterruptedException {
        zooKeeper.close();
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }
}
