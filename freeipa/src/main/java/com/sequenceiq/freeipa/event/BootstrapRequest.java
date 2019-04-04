package com.sequenceiq.freeipa.event;

public class BootstrapRequest extends ClusterPlatformRequest {
    public BootstrapRequest(Long stackId) {
        super(stackId);
    }
}
