package com.zookeeper.example;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

/**
 * this class will be used to create distributed lock
 * The other process or threads will be waiting for the lock if its already acquired by another
 */
public class DistributedLock {
    private final ZookeeperClient zookeeperClient;
    private final String lockPath;

    public DistributedLock(ZookeeperClient zookeeperClient, String lockPath) {
        this.zookeeperClient = zookeeperClient;
        this.lockPath = lockPath;
    }

    public void lock() throws KeeperException, InterruptedException {
        while(true) {
            try {
                zookeeperClient.getZooKeeper().create(lockPath, new byte[0],
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                System.out.printf("lock acquired! path: %s\n", lockPath);
                break;
            }catch (KeeperException.NodeExistsException e) {
                if (this.isNodeExist()) {
                    synchronized (this) {
                        System.out.printf("Wait, lock is already acquired, path: %s\n", lockPath);
                        wait(5000);
                    }
                }
            }
        }
    }

    public void unlock() throws KeeperException, InterruptedException {
        zookeeperClient.getZooKeeper().delete(lockPath, -1);
        System.out.printf("lock released! path: %s\n", lockPath);
    }

    public boolean isNodeExist() throws KeeperException, InterruptedException {
        Stat stat = zookeeperClient.getZooKeeper().exists(lockPath, true);
        return stat != null;
    }
}
