package com.cloudera.flow.config;

import com.cloudera.flow.Flow;
import com.cloudera.flow.FlowEvent;
import com.cloudera.flow.FlowTriggerCondition;
import com.cloudera.flow.RestartAction;

public interface FlowConfiguration<E extends FlowEvent> {
    Flow createFlow(String flowId, Long stackId);

    FlowTriggerCondition getFlowTriggerCondition();

    E[] getEvents();

    E[] getInitEvents();

    RestartAction getRestartAction(String event);
}
