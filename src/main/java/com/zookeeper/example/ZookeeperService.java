package com.zookeeper.example;

import org.apache.zookeeper.KeeperException;

import java.util.List;

public interface ZookeeperService {
    void createParentNode() throws InterruptedException, KeeperException;
    void createEphemeralNode() throws InterruptedException, KeeperException;
    boolean isLeader() throws InterruptedException, KeeperException;
    List<String> getChildNodes(String parentZnode) throws InterruptedException, KeeperException;
}
