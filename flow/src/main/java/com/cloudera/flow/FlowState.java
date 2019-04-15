package com.cloudera.flow;


import com.cloudera.flow.restart.DefaultRestartAction;

public interface FlowState {
    default Class<? extends AbstractAction<?, ?, ?, ?>> action() {
        return null;
    }

    default Class<? extends RestartAction> restartAction() {
        return DefaultRestartAction.class;
    }

    String name();
}
