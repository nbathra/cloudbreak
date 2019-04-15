package com.cloudera.flow.config;


import com.cloudera.flow.FlowEvent;

public interface RetryableFlowConfiguration<E extends FlowEvent> {
    E getFailHandledEvent();
}
