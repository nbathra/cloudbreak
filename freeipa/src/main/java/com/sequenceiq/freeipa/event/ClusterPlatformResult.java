package com.sequenceiq.freeipa.event;

import com.sequenceiq.cloudbreak.cloud.event.Selectable;
import com.sequenceiq.cloudbreak.cloud.event.model.EventStatus;

public abstract class ClusterPlatformResult<R extends ClusterPlatformRequest> implements Selectable {

    private EventStatus status;

    private String statusReason;

    private Exception errorDetails;

    private R request;

    protected ClusterPlatformResult(R request) {
        init(EventStatus.OK, null, null, request);
    }

    protected ClusterPlatformResult(String statusReason, Exception errorDetails, R request) {
        init(EventStatus.FAILED, statusReason, errorDetails, request);
    }

    protected void init(EventStatus status, String statusReason, Exception errorDetails, R request) {
        this.status = status;
        this.statusReason = statusReason;
        this.errorDetails = errorDetails;
        this.request = request;
    }

    @Override
    public String selector() {
        String className = getClass().getSimpleName().toUpperCase();
        return status == EventStatus.OK ? className : className + "_ERROR";
    }

    public EventStatus getStatus() {
        return status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public Exception getErrorDetails() {
        return errorDetails;
    }

    public R getRequest() {
        return request;
    }

    @Override
    public Long getStackId() {
        return request.getStackId();
    }
}
