package com.zookeeper.example;

import org.apache.zookeeper.KeeperException;

/**
 * This class print a message on startup once across the cluster.
 * It uses Leader Election receipies provided by apache zookeeper
 * to make sure only one node will print the message accross cluster.
 */
public class App {
    ZookeeperService zookeeperService;

    public App(ZookeeperService zookeeperService){
        this.zookeeperService = zookeeperService;
    }

    /**
     * This method creates a prent node (if not present) which is persistent and ephemeral sequential node
     * The node of lowest sequence number will be leader and if its matches will the currentNode then return true.
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
    public static void main(String[] args) throws Exception {
        if(args.length < 2) {
            throw  new IllegalArgumentException("Arguments are missing");
        }

        ZookeeperClient zookeeperClient = new ZookeeperClient(
                new ZookeeperClientConfig(args[0]));
        App app = new App(new ZookeeperServiceImpl(zookeeperClient));
        app.startNodeAndPrintMessage();

        // This sleep is for simulating work
        Thread.sleep(Long.parseLong(args[1]));
    }
}
