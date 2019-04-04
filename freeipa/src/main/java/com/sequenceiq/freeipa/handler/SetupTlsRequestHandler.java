package com.sequenceiq.freeipa.handler;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.handler.CloudPlatformEventHandler;
import com.sequenceiq.cloudbreak.service.CloudbreakException;
import com.sequenceiq.freeipa.event.BootstrapRequest;
import com.sequenceiq.freeipa.event.SetupTlsRequest;
import com.sequenceiq.freeipa.service.TlsSetupService;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Component
public class SetupTlsRequestHandler implements CloudPlatformEventHandler<SetupTlsRequest> {

    @Inject
    private TlsSetupService tlsSetupService;

    @Inject
    private EventBus eventBus;

    @Override
    public Class<SetupTlsRequest> type() {
        return SetupTlsRequest.class;
    }

    @Override
    public void accept(Event<SetupTlsRequest> tlsSetupRequestEvent) {
        try {
            tlsSetupService.setupTls(tlsSetupRequestEvent.getData().getStackId());
            BootstrapRequest bootstrapRequest = new BootstrapRequest(tlsSetupRequestEvent.getData().getStackId());
            eventBus.notify(bootstrapRequest.selector(), new Event<>(bootstrapRequest));
        } catch (CloudbreakException e) {
            e.printStackTrace();
        }
    }
}
