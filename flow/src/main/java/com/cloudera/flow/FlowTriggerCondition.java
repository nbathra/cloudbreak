package com.cloudera.flow;

public interface FlowTriggerCondition {
    boolean isFlowTriggerable(Long stackId);
}
