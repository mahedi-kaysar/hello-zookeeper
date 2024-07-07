package com.zookeeper.example;

import org.apache.zookeeper.*;
import java.util.List;

/**
 * This Class is to demonstrate node co-ordination using apache zookeeper.
 */
public class HelloZookeeper {
    private static final String START_ZNODE = "/parentnode";
    private ZooKeeper zk;
    private String myZnodeName;

    public HelloZookeeper(String hosts) throws Exception {
        zk = new ZooKeeper(hosts, 3000, null);
    }

    public void start() throws Exception {
        // Create the parent znode if it does not exist
        if (zk.exists(START_ZNODE, false) == null) {
            zk.create(START_ZNODE, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        }

        // Create an ephemeral, sequential znode
        myZnodeName = zk.create(START_ZNODE + "/n_", null,
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println(myZnodeName);

        // Check if we are the leader
        checkIfLeader();

        // Simulate work
        Thread.sleep(Long.MAX_VALUE);
    }

    private void checkIfLeader() throws Exception {
        int myZnodeNumber = Integer.parseInt(myZnodeName.substring(myZnodeName.lastIndexOf('/') + 3));

        // Get all children of the start znode
        List<String> children = zk.getChildren(START_ZNODE, false);

        int minZnodeNumber = children.stream()
                .map(n -> Integer.parseInt(n.substring(2)))
                .min(Integer::compare)
                .orElse(-1);

        // If we are the smallest znode, we are the leader
        if (myZnodeNumber == minZnodeNumber) {
            System.out.println("We are started!");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1)
            throw new IllegalArgumentException("Number of Arguments is less than 1");

        System.out.println(args[0]);

        HelloZookeeper ds = new HelloZookeeper(args[0]); // pass ZooKeeper hosts as argument
        ds.start();
    }
}
