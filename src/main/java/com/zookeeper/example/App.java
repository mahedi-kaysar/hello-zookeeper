package com.zookeeper.example;

import org.apache.zookeeper.KeeperException;

/**
 * This class prints a message on startup once across the cluster.
 * It uses Leader Selection approach provided by apache zookeeper
 * to make sure only leader prints the message across cluster.
 */
public class App {
    ZookeeperService zookeeperService;

    public App(ZookeeperService zookeeperService){
        this.zookeeperService = zookeeperService;
    }

    /**
     * This method creates a prent node (if not present) which is persistent and ephemeral (non-persistent) sequential
     * nodes are created under the parent node.
     *
     * The parent node ensures that the starting point which is always available even all the ephemeral nodes got deleted.
     *
     * Everytime a new node is started up - it create a new ephemeral node with a unique sequence number (ensured by apache
     * zookeeper)
     *
     * The child node of lowest sequence number will be selected as leader and prints the message.
     *
     * If the leader is deleted after printing the message - only when a new nodes is coming up - it will check again if
     * the new node has the lowest sequence number or not. The new node will be lowest only if there are no active child
     * node present under the parent node.
     *
     * For our scenarios - the watcher is not necessary to re-select the leader if a child node goes down.
     * Because the message will be printed only once across the cluster when at least 1 node is present.
     *
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void startNodeAndPrintMessage() throws InterruptedException, KeeperException {
        zookeeperService.createParentNode();
        zookeeperService.createEphemeralNode();
        if(zookeeperService.isLeader()) {
            System.out.println("We are started!");
        }
    }
    public static void main(String[] args) {
        if(args.length < 2) {
            throw  new IllegalArgumentException("Arguments are missing");
        }
        try {

            // Initialise Zookeeper Client
            ZookeeperClient zookeeperClient = new ZookeeperClient(
                    new ZookeeperClientConfig(args[0]));

            // Start the Application
            App app = new App(new ZookeeperServiceImpl(zookeeperClient,
                    new DistributedLock(zookeeperClient, "/mlock")));
            app.startNodeAndPrintMessage();

            // This sleep is for simulating work
            Thread.sleep(Long.parseLong(args[1]));

            // closing the connection
            zookeeperClient.closeConnection();
        } catch (Exception exception){
            System.out.printf("Excception occured:%s\n", exception);
            System.exit(1);
        }
    }
}
