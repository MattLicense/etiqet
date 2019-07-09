package com.neueda.etiqet.transport.rabbitmq.config.model;

import com.neueda.etiqet.core.transport.delegate.StringBinaryMessageConverterDelegate;

import java.util.ArrayList;
import java.util.List;

public class AmqpConfigBuilder {
    private String host;
    private List<ExchangeConfig> exchangeConfigs;
    private Class binaryMessageConverterDelegateClass;

    private AmqpConfigBuilder() {
        host = "localhost";
        exchangeConfigs = new ArrayList<>();
        binaryMessageConverterDelegateClass = StringBinaryMessageConverterDelegate.class;
    }

    public static AmqpConfigBuilder aAmqpConfig() {
        return new AmqpConfigBuilder();
    }

    public AmqpConfigBuilder addExchangeConfig(ExchangeConfig exchangeConfig) {
        exchangeConfigs.add(exchangeConfig);
        return this;
    }

    public AmqpConfigBuilder withBinaryMessageConverterDelegateClass(Class binaryMessageConverterDelegateClass) {
        this.binaryMessageConverterDelegateClass = binaryMessageConverterDelegateClass;
        return this;
    }

    public AmqpConfig build() {
        return new AmqpConfig(
            host,
            exchangeConfigs,
            binaryMessageConverterDelegateClass
        );
    }
}
