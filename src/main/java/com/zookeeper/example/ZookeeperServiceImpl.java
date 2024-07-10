package com.zookeeper.example;

import org.apache.zookeeper.*;

import java.util.List;

/**
 * This class is the implementation of ZookeeperService class
 */
public class ZookeeperServiceImpl implements ZookeeperService {
    private static final String parentZnode = "/parent";
    private ZookeeperClient zookeeperClient;
    private String currentChildZnode;

    public ZookeeperServiceImpl(ZookeeperClient zookeeperClient) {
        this.zookeeperClient=zookeeperClient;
    }

    /**
     * This method create parent node
     * @throws InterruptedException
     * @throws KeeperException
     */
    @Override
    public void createParentNode() throws InterruptedException, KeeperException {
        if (zookeeperClient.getZooKeeper().exists(parentZnode, null) == null) {
            zookeeperClient.getZooKeeper().create(parentZnode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        }
    }

    /**
     * This method creates non-persistent ephemeral node.
     *
     * @throws InterruptedException
     * @throws KeeperException
     */
    @Override
    public void createEphemeralNode() throws InterruptedException, KeeperException {
        this.currentChildZnode = zookeeperClient.getZooKeeper().create(parentZnode + "/n_", null,
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("currentChildZnode=" + this.currentChildZnode);
    }

    /**
     * This method returns all the available children's path
     *
     * @param parentZnode
     * @return list of child node paths
     * @throws InterruptedException
     * @throws KeeperException
     */
    @Override
    public List<String> getChildNodes(String parentZnode) throws InterruptedException, KeeperException {
        return zookeeperClient.getZooKeeper().getChildren(ZookeeperServiceImpl.parentZnode, false);
    }

    /**
     * this method finds the lowest sequence number among the children nodes and decide if the current node is leader
     * or not.
     *
     * @return true  | false
     * @throws InterruptedException
     * @throws KeeperException
     */
    @Override
    public boolean isLeader() throws InterruptedException, KeeperException {
        int currentZnodeNumber = Integer.parseInt(currentChildZnode.substring(currentChildZnode.lastIndexOf('_') + 1));

        // Get all children of the start znode
        List<String> children = this.getChildNodes(parentZnode);

        int minZnodeNumber = children.stream()
                .map(n -> Integer.parseInt(n.substring(2)))
                .min(Integer::compare)
                .orElse(-1);

        return currentZnodeNumber == minZnodeNumber;
    }
}
