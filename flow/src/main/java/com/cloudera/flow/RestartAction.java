package com.cloudera.flow;

public interface RestartAction {

    void restart(String flowId, String flowChainId, String event, Object payload);
}
