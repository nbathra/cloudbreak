package com.sequenceiq.freeipa.event;

import com.sequenceiq.cloudbreak.cloud.event.Selectable;

public abstract class ClusterPlatformRequest implements Selectable {

    private final Long stackId;

    protected ClusterPlatformRequest(Long stackId) {
        this.stackId = stackId;
    }

    @Override
    public Long getStackId() {
        return stackId;
    }

    @Override
    public String selector() {
        return getClass().getSimpleName().toUpperCase();
    }
}
