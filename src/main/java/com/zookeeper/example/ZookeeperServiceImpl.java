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
    DistributedLock distributedLock;
    public ZookeeperServiceImpl(ZookeeperClient zookeeperClient, DistributedLock distributedLock) {
        this.zookeeperClient=zookeeperClient;
        this.distributedLock = distributedLock;
    }

    /**
     * This method create parent node
     * @throws InterruptedException
     * @throws KeeperException
     */
    @Override
    public void createParentNode() throws InterruptedException, KeeperException {
        try {
            if (zookeeperClient.getZooKeeper().exists(parentZnode, null) == null) {
                zookeeperClient.getZooKeeper().create(parentZnode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);

            }
        } catch (Exception exception) {
            System.out.printf("exception on create parent node:%s, %s\n",parentZnode, exception);
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
        System.out.println("getChildren method is called!");
        return zookeeperClient.getZooKeeper()
                .getChildren(parentZnode, false);
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
        int currentZnodeNumber = Integer.parseInt(currentChildZnode
                .substring(currentChildZnode.lastIndexOf('_') + 1));

        // Get all children of the start znode
        List<String> children = null;
        distributedLock.lock();
        try {
            children = this.getChildNodes(parentZnode);
        } catch (KeeperException keeperException){
            keeperException.printStackTrace();
        } finally {
            distributedLock.unlock();
        }
        System.out.printf("childrens: %s\n", children);
        if (children!=null) {
            int minZnodeNumber = children.stream()
                    .map(n -> Integer.parseInt(n.substring(2)))
                    .min(Integer::compare)
                    .orElse(-1);

            return currentZnodeNumber == minZnodeNumber;
        } else {
            return false;
        }
    }
}
