package com.zookeeper.example;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZookeeperClient {
    private ZooKeeper zooKeeper;
    ZookeeperClientConfig zookeeperClientConfig;
    CountDownLatch connectionLatch = new CountDownLatch(1);
    public ZookeeperClient(ZookeeperClientConfig zookeeperClientConfig) throws IOException, InterruptedException {
        this.zookeeperClientConfig = zookeeperClientConfig;
        initialise();
    }


    /**
     * this is to initialise connection.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void initialise() throws IOException, InterruptedException {

        this.zooKeeper = new ZooKeeper(zookeeperClientConfig.getConnectionString(),
            zookeeperClientConfig.getTimeout(), watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    System.out.println("Connected");
                    connectionLatch.countDown();
                } else if (watchedEvent.getState() == Watcher.Event.KeeperState.Disconnected) {
                    System.out.println("Disconnected");
                }
        });
        connectionLatch.await(5000, TimeUnit.MILLISECONDS);
    }

    public void closeConnection() throws InterruptedException {
        zooKeeper.close();
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

}
