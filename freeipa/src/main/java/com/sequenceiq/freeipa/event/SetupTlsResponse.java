package com.sequenceiq.freeipa.event;

public class SetupTlsResponse extends ClusterPlatformResult<SetupTlsRequest> {
    protected SetupTlsResponse(SetupTlsRequest request) {
        super(request);
    }
}
