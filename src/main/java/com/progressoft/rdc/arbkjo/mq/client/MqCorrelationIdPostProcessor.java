package com.progressoft.rdc.arbkjo.mq.client;

import org.springframework.jms.core.MessagePostProcessor;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Iterator;
import java.util.Map;

public class MqCorrelationIdPostProcessor implements MessagePostProcessor {

    private final String correlationId;
    private final Map<String, String> headers;

    public MqCorrelationIdPostProcessor(final String correlationId, Map<String, String> headers) {
        this.correlationId = correlationId;
        this.headers = headers;
    }

    public Message postProcessMessage(final Message msg) throws JMSException {
        msg.setJMSCorrelationID(correlationId);
        if (headers != null) {
            Iterator<String> headerNames = headers.keySet().iterator();
            while (headerNames.hasNext()) {
                String headerName = headerNames.next();
                String headerValue = headers.get(headerName);
                msg.setStringProperty(headerName, headerValue);
            }
        }
        return msg;
    }
}
