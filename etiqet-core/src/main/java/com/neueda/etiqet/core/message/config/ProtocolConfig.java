package com.neueda.etiqet.core.message.config;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.config.dtos.Client;
import com.neueda.etiqet.core.config.dtos.Delegate;
import com.neueda.etiqet.core.config.dtos.Dictionary;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.config.dtos.Protocol;
import com.neueda.etiqet.core.config.dtos.StopEvent;
import com.neueda.etiqet.core.config.dtos.UrlExtension;
import com.neueda.etiqet.core.config.xml.XmlParser;
import com.neueda.etiqet.core.util.StringUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolConfig implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3777143195512896407L;

    private static final transient Logger LOG = LoggerFactory.getLogger(ProtocolConfig.class);

    private Protocol protocol;

    private AbstractDictionary dictionary;

    private Map<String, Message> messageMap;

    public ProtocolConfig() {
    }

    public ProtocolConfig(Protocol protocol) throws EtiqetException {
        setProtocol(protocol);
        commonInit();
    }

    public ProtocolConfig(String path) throws EtiqetException {
        try {
            setProtocol(new XmlParser().parse(path, Protocol.class));
            commonInit();
        } catch (Exception e) {
            throw new EtiqetException("Unable to load messages configuration: " + path, e);
        }
    }

    public String getProtocolName() {
        return protocol.getName();
    }

    private void commonInit() throws EtiqetException {
        // Setup the ProtocolConfig based on the Protocol parsed from the XML
        messageMap = new HashMap<>();
        if (getProtocol().getMessages() != null) {
            for (Message message : getProtocol().getMessages().getMessage()) {
                messageMap.put(message.getName(), message);
            }
        }
        Dictionary protocolDictionary = getProtocol().getDictionary();
        if (protocolDictionary != null) {
            String handlerPath = protocolDictionary.getHandler();
            try {
                setDictionary((AbstractDictionary) Class.forName(handlerPath)
                    .getConstructor(String.class)
                    .newInstance(protocolDictionary.getValue()));
            } catch (Exception e) {
                LOG.error("Error loading dictionaryHandler: {}", handlerPath);
                throw new EtiqetException("Error loading dictionaryHandler: " + handlerPath, e);
            }
        }
    }

    public Message getMessage(String name) {
        return messageMap.get(name);
    }

    public Message[] getMessages() {
        return getProtocol().getMessages().getMessage();
    }

    public String getComponentPackage() {
        String componentPackage = getProtocol().getComponentsPackage();
        return componentPackage + (componentPackage.endsWith(".") ? "" : ".");
    }

    public String getMsgType(String messageName) {
        String msgType = null;
        if (getDictionary() != null) {
            msgType = getDictionary().getMsgType(messageName);
        }
        if (StringUtils.isNullOrEmpty(msgType)) {
            Message message = messageMap.get(messageName);
            if (message != null) {
                msgType = message.getMsgtype();
            }
        }
        return msgType;
    }

    public String getNameForTag(Integer t) {
        return getDictionary() != null ? getDictionary().getNameForTag(t) : null;
    }

    public Integer getTagForName(String name) throws UnknownTagException {
        return getDictionary() != null ? getDictionary().getTagForName(name) : null;
    }

    public boolean tagContains(Integer tag) {
        return getDictionary() != null && getDictionary().tagContains(tag);
    }

    public String getMsgName(String msgType) {
        return getDictionary() != null ? getDictionary().getMsgName(msgType) : null;
    }

    public boolean isAdmin(String msgName) {
        return (getDictionary() != null) && getDictionary().isAdmin(msgName);
    }

    public boolean isHeaderField(String fieldName) {
        return getDictionary() != null && getDictionary().isHeaderField(fieldName);
    }

    public boolean isHeaderField(Integer tag) {
        return getDictionary() != null && getDictionary().isHeaderField(tag);
    }

    @Override
    public String toString() {
        return getProtocol().toString();
    }

    public List<Delegate> getClientDelegates() {
        return getProtocol().getClient().getDelegates();
    }

    public StopEvent getStopEvent() {
        return getProtocol().getClient().getStopEvent();
    }

    public List<UrlExtension> getClientUrlExtensions() {
        return getProtocol().getClient().getUrlExtensions();
    }

    public Client getClient() {
        return getProtocol().getClient();
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public AbstractDictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(AbstractDictionary dictionary) {
        this.dictionary = dictionary;
    }
}
