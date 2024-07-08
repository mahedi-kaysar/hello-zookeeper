package com.zookeeper.example;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import java.util.List;

public class ZookeeperServiceImpl implements ZookeeperService {
    private static final String parentZnode = "/parent";
    private ZookeeperClient zookeeperClient;
    private String currentChildZnode;
    ZookeeperClientConfig zookeeperClientConfig;
    public ZookeeperServiceImpl(ZookeeperClient zookeeperClient) {
        this.zookeeperClient=zookeeperClient;
    }

    @Override
    public void createParentNode() throws InterruptedException, KeeperException {
        if (zookeeperClient.getZooKeeper().exists(parentZnode, false) == null) {
            zookeeperClient.getZooKeeper().create(parentZnode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        }
    }

    @Override
    public void createEphemeralNode() throws InterruptedException, KeeperException {
        this.currentChildZnode = zookeeperClient.getZooKeeper().create(parentZnode + "/n_", null,
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("currentChildZnode=" + this.currentChildZnode);
    }

    @Override
    public List<String> getChildNodes(String parentZnode, boolean b) throws InterruptedException, KeeperException {
        return zookeeperClient.getZooKeeper().getChildren(ZookeeperServiceImpl.parentZnode, false);
    }
    @Override
    public boolean isLeader() throws InterruptedException, KeeperException {
        int currentZnodeNumber = Integer.parseInt(currentChildZnode.substring(currentChildZnode.lastIndexOf('/') + 3));

        // Get all children of the start znode
        List<String> children = this.getChildNodes(parentZnode, false);

        int minZnodeNumber = children.stream()
                .map(n -> Integer.parseInt(n.substring(2)))
                .min(Integer::compare)
                .orElse(-1);

        if (currentZnodeNumber == minZnodeNumber) {
            return true;
        }
        return false;
    }
}
