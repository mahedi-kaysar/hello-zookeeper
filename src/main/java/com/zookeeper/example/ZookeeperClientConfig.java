package com.zookeeper.example;

public class ZookeeperClientConfig {
    private int timeout = 3000;
    private int maxRetry = 3;
    private int retryDelay = 2000;
    private String connectionString = "localhost:2181";

    public ZookeeperClientConfig( String connectionString) {
        this.connectionString = connectionString;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }
}
