package com.sequenceiq.freeipa.handler;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.handler.CloudPlatformEventHandler;
import com.sequenceiq.freeipa.event.BootstrapRequest;
import com.sequenceiq.freeipa.service.BootstrapService;

import reactor.bus.Event;

@Component
public class BootstrapRequestHandler implements CloudPlatformEventHandler<BootstrapRequest> {

    @Inject
    private BootstrapService bootstrapService;

    @Override
    public Class<BootstrapRequest> type() {
        return BootstrapRequest.class;
    }

    @Override
    public void accept(Event<BootstrapRequest> bootstrapRequestEvent) {
        Long stackId = bootstrapRequestEvent.getData().getStackId();
        bootstrapService.bootstrap(stackId);
    }
}
