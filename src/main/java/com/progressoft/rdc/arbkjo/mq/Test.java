package com.progressoft.rdc.arbkjo.mq;

import com.progressoft.rdc.arbkjo.mq.client.MqClient;
import com.progressoft.rdc.arbkjo.mq.config.MqCommConfig;
import com.progressoft.rdc.arbkjo.mq.exceptions.AsyncSendException;
import com.progressoft.rdc.arbkjo.mq.message.MqMessage;

import javax.jms.JMSException;
import java.util.HashMap;

public class Test {

    /**
     * docker run \
     *   --env LICENSE=accept \
     *   --env MQ_QMGR_NAME=QM1 \
     *   --publish 1414:1414 \
     *   --publish 9443:9443 \
     *   --detach \
     *   ibmcom/mq:9.2.4.0-r1-amd64
     */

    public static void main(String[] args) throws JMSException, AsyncSendException, InterruptedException {
        MqCommConfig config = buildConfig("DEV.QUEUE.1", "DEV.QUEUE.2");

        new Thread(() -> {
            try {
                sendMessage("koko", "AAAAAAAA",config);
                String response = receive("AAAAAAAA", config);
                System.out.println(response);
            } catch (JMSException | AsyncSendException e) {
                e.printStackTrace();
            }
        }).start();
        sendMessage("fofo", "AAAAAAAA", config);
    }

    private static void sendMessage(String messageText, String correlationId, MqCommConfig config) throws JMSException, AsyncSendException {
        MqClient mqClient = buildClient(config);

        MqMessage mqMessage = buildMessage(messageText, correlationId);
        mqClient.asyncSend(config, mqMessage);
    }

    private static String syncSendMessage(String messageText, String correlationId, MqCommConfig config) throws JMSException, AsyncSendException {
        MqClient mqClient = buildClient(config);

        MqMessage mqMessage = buildMessage(messageText, correlationId);
        return String.valueOf(mqClient.syncSend(config, mqMessage));
    }

    private static String receive(String correlationId, MqCommConfig config) throws JMSException, AsyncSendException {
        MqClient mqClient = buildClient(config);
        String selector = "JMSCorrelationID='".concat(correlationId).concat("'");

        return mqClient.receiveSelected(config, selector);
    }

    private static MqClient buildClient(MqCommConfig config) throws JMSException {
        MqClient mqClient = new MqClient();
        mqClient.init(config);
        return mqClient;
    }

    private static MqMessage buildMessage(String messageText, String correlationId) {
        MqMessage mqMessage = new MqMessage();
        mqMessage.setContent(messageText);
        mqMessage.setCorrelationId(correlationId);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("key", "val");
        mqMessage.setHeaders(headers);
        return mqMessage;
    }

    private static MqCommConfig buildConfig(String requestQueue, String responseQueue) {
        MqCommConfig config = new MqCommConfig();
        config.setUsername("app");
        config.setPassword("");
        config.setQueueManager("QM1");
        config.setRequestQueueName(requestQueue);
        config.setResponseQueueName(responseQueue);
        config.setChannel("DEV.APP.SVRCONN");
        config.setHostName("localhost");
        config.setPort(1414);
        config.setTransportType(1);
        config.setReceiveTimeout(20000);
        return config;
    }
}
