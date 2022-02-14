package com.progressoft.rdc.arbkjo.mq.client;

import com.ibm.mq.jms.MQConnectionFactory;
import com.progressoft.rdc.arbkjo.mq.config.MqCommConfig;
import com.progressoft.rdc.arbkjo.mq.exceptions.AsyncSendException;
import com.progressoft.rdc.arbkjo.mq.message.MqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class MqClient {

    private static final long RECEIVE_TIMEOUT_INDEFINITE_WAIT = 0;
    private static final long TIME_TO_LIVE_INDEFINITE = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(MqClient.class);
    private JmsTemplate jmsTemplate;
    private JmsTemplate transactedJmsTemplate;
    private MqMessageConverter messageConverter;
    private MQConnectionFactory mqFactory;

    public MqClient() {
        this.jmsTemplate = new JmsTemplate();
        this.transactedJmsTemplate = new JmsTemplate();
        this.messageConverter = new MqMessageConverter();
        this.mqFactory = new MQConnectionFactory();
    }

    public void init(MqCommConfig config) throws JMSException {

        if ("true".equals(config.getSslEnabled())) {
            System.setProperty("javax.net.debug", "ssl:handshake");
            System.setProperty("javax.net.ssl.trustStore", config.getSslTrustStore());
            System.setProperty("javax.net.ssl.trustStorePassword", config.getSslTrustStorePassword());
            System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", config.getSslIbmCipherMappings());
            System.setProperty("javax.net.ssl.keyStore", config.getSslTrustStore());
            System.setProperty("javax.net.ssl.keyStorePassword", config.getSslTrustStorePassword());

            mqFactory.setSSLCipherSuite(config.getSslCipherSuite());
        }

        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setCacheConsumers(false);
        factory.setCacheProducers(false);
        factory.setSessionCacheSize(2);

        UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
        adapter.setUsername(config.getUsername());
        adapter.setPassword(config.getPassword());

        mqFactory.setQueueManager(config.getQueueManager());
        mqFactory.setHostName(config.getHostName());
        mqFactory.setPort(config.getPort());
        mqFactory.setTransportType(config.getTransportType());
        mqFactory.setChannel(config.getChannel());
        mqFactory.setSSLFipsRequired(Boolean.parseBoolean(config.getSslFipsRequired()));

        adapter.setTargetConnectionFactory(mqFactory);

        factory.setTargetConnectionFactory(adapter);
        jmsTemplate.setConnectionFactory(factory);

        jmsTemplate.setDestinationResolver(new DynamicDestinationResolver());
        jmsTemplate.setSessionTransacted(false);
        jmsTemplate.setMessageConverter(messageConverter);
        //////////
        transactedJmsTemplate.setConnectionFactory(factory);
        transactedJmsTemplate.setDestinationResolver(new DynamicDestinationResolver());
        transactedJmsTemplate.setSessionTransacted(false);
        transactedJmsTemplate.setMessageConverter(messageConverter);
    }

    public Object syncSend(MqCommConfig commConfig, MqMessage reqMsg) throws JMSException {
        try {
            LOGGER.info("request correlationId: {}", reqMsg.getCorrelationId());
            // timeout settings
            if (commConfig.getReceiveTimeout() != RECEIVE_TIMEOUT_INDEFINITE_WAIT) {
                jmsTemplate.setReceiveTimeout(commConfig.getReceiveTimeout());
            }
            if (commConfig.getTimeToLive() != TIME_TO_LIVE_INDEFINITE) {
                jmsTemplate.setExplicitQosEnabled(true);
                jmsTemplate.setTimeToLive(commConfig.getTimeToLive());
            }
            jmsTemplate.setMessageIdEnabled(true);

            AtomicReference<Message> atomicReference = new AtomicReference<>();

            jmsTemplate.convertAndSend(commConfig.getRequestQueueName(), reqMsg.getContent(),
                    messagePostProcessor -> {
                        atomicReference.set(messagePostProcessor);
                        return messagePostProcessor;
                    });

            String selector = "JMSCorrelationID='".concat(atomicReference.get().getJMSMessageID()).concat("'");
            return jmsTemplate.receiveSelectedAndConvert(commConfig.getResponseQueueName(), selector);
        } catch (JMSException e) {
            LOGGER.error("Error while sending sync message!", e);
            throw e;
        } finally {
            ((CachingConnectionFactory) jmsTemplate.getConnectionFactory()).resetConnection();
        }
    }

    public void asyncSend(MqCommConfig commConfig, MqMessage reqMsg) throws AsyncSendException {
        try {
            // timeout settings
            if (commConfig.getTimeToLive() != TIME_TO_LIVE_INDEFINITE) {
                transactedJmsTemplate.setExplicitQosEnabled(true);
                transactedJmsTemplate.setTimeToLive(commConfig.getTimeToLive());
            }
            String correlationId = reqMsg.getCorrelationId();
            if (correlationId == null) {
                correlationId = UUID.randomUUID().toString();
            }
            transactedJmsTemplate.convertAndSend(commConfig.getRequestQueueName(), reqMsg.getContent(),
                    new MqCorrelationIdPostProcessor(correlationId, reqMsg.getHeaders()));
        } catch (JmsException e) {
            LOGGER.error("Error while sending async message!", e);
            throw new AsyncSendException();
        } finally {
            ((CachingConnectionFactory) transactedJmsTemplate.getConnectionFactory()).resetConnection();
        }
    }

    public String receiveSelected(MqCommConfig commConfig, String selector) {
        // timeout settings
        transactedJmsTemplate.setReceiveTimeout(commConfig.getReceiveTimeout());
        MqMessage respMsg = (MqMessage) transactedJmsTemplate.receiveSelectedAndConvert(commConfig.getResponseQueueName(), selector);
        return respMsg.getContent();
    }
}
