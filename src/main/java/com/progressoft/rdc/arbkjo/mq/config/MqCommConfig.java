package com.progressoft.rdc.arbkjo.mq.config;

public class MqCommConfig {

    private String requestQueueName;
    private String responseQueueName;
    private long receiveTimeout;
    private long timeToLive;

    private String queueManager;
    private String hostName;
    private int port;
    private int transportType;
    private String channel;
    private String username;
    private String password;

    private String sslEnabled;
    private String sslCipherSuite;
    private String sslTrustStore;
    private String sslTrustStorePassword;
    private String sslIbmCipherMappings;
    private String sslFipsRequired;


    public String getSslFipsRequired() {
        return sslFipsRequired;
    }

    public void setSslFipsRequired(String sslFipsRequired) {
        this.sslFipsRequired = sslFipsRequired;
    }

    public String getSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(String sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public String getSslCipherSuite() {
        return sslCipherSuite;
    }

    public void setSslCipherSuite(String sslCipherSuite) {
        this.sslCipherSuite = sslCipherSuite;
    }

    public String getSslTrustStore() {
        return sslTrustStore;
    }

    public void setSslTrustStore(String sslTrustStore) {
        this.sslTrustStore = sslTrustStore;
    }

    public String getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }

    public void setSslTrustStorePassword(String sslTrustStorePassword) {
        this.sslTrustStorePassword = sslTrustStorePassword;
    }

    public String getSslIbmCipherMappings() {
        return sslIbmCipherMappings;
    }

    public void setSslIbmCipherMappings(String sslIbmCipherMappings) {
        this.sslIbmCipherMappings = sslIbmCipherMappings;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTransportType() {
        return transportType;
    }

    public void setTransportType(int transportType) {
        this.transportType = transportType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRequestQueueName() {
        return requestQueueName;
    }

    public void setRequestQueueName(String requestQueueName) {
        this.requestQueueName = requestQueueName;
    }

    public String getResponseQueueName() {
        return responseQueueName;
    }

    public void setResponseQueueName(String responseQueueName) {
        this.responseQueueName = responseQueueName;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public String toString() {
        return "MqCommConfig{" +
                "requestQueueName='" + requestQueueName + '\'' +
                ", responseQueueName='" + responseQueueName + '\'' +
                ", receiveTimeout=" + receiveTimeout +
                ", timeToLive=" + timeToLive +
                ", queueManager='" + queueManager + '\'' +
                ", hostName='" + hostName + '\'' +
                ", port=" + port +
                ", transportType=" + transportType +
                ", channel='" + channel + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sslEnabled='" + sslEnabled + '\'' +
                ", sslCipherSuite='" + sslCipherSuite + '\'' +
                ", sslTrustStore='" + sslTrustStore + '\'' +
                ", sslTrustStorePassword='" + sslTrustStorePassword + '\'' +
                ", sslIbmCipherMappings='" + sslIbmCipherMappings + '\'' +
                ", sslFipsRequired='" + sslFipsRequired + '\'' +
                '}';
    }
}
