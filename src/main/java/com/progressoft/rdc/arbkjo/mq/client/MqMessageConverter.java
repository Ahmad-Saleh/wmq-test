package com.progressoft.rdc.arbkjo.mq.client;

import com.ibm.jms.JMSBytesMessage;
import com.progressoft.rdc.arbkjo.mq.message.MqMessage;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class MqMessageConverter implements MessageConverter {

    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        String messageText = null;
        Map<String, String> headers = null;

        // this is ugly, but its here for backward compatibility
        if (object instanceof String) {
            messageText = (String) object;
        } else {
            MqMessage msg = (MqMessage) object;
            messageText = msg.getContent();
            headers = msg.getHeaders();
        }

        // create new JMS TextMessage
        TextMessage textMessage = session.createTextMessage();
        // set the message body
        textMessage.setText(messageText);
        // set the properties
        if (headers != null) {
            Iterator<String> headerNames = headers.keySet().iterator();
            while (headerNames.hasNext()) {
                String headerName = (String) headerNames.next();
                String headerValue = headers.get(headerName);
                textMessage.setStringProperty(headerName, headerValue);
            }
        }
        return textMessage;
    }

    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        MqMessage msg = new MqMessage();
        if (message instanceof JMSBytesMessage) {
            JMSBytesMessage bytesMessage = (JMSBytesMessage) message;
            int length = new Long(bytesMessage.getBodyLength()).intValue();
            byte[] b = new byte[length];
            bytesMessage.readBytes(b, length);
            try {
                String text = new String(b, "UTF-8");
                msg.setContent(text);
                msg.setCorrelationId(bytesMessage.getJMSCorrelationID());
                Enumeration<?> propertyNames = message.getPropertyNames();
                while (propertyNames.hasMoreElements()) {
                    String propertyName = propertyNames.nextElement().toString();
                    msg.addHeader(propertyName, message.getStringProperty(propertyName));
                }
                return msg;
            } catch (UnsupportedEncodingException e) {
                throw new MessageConversionException(e.getMessage());
            }
        } else if (message instanceof TextMessage) {
            msg.setContent(((TextMessage) message).getText());
            Enumeration<?> propertyNames = message.getPropertyNames();
            msg.setCorrelationId(message.getJMSCorrelationID());
            while (propertyNames.hasMoreElements()) {
                String propertyName = propertyNames.nextElement().toString();
                msg.addHeader(propertyName, message.getStringProperty(propertyName));
            }
            return msg;
        }
        throw new MessageConversionException("unknown message type: " +
                "" + message.getClass().getName());
    }
}
