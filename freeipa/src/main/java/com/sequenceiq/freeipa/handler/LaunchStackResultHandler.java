package com.sequenceiq.freeipa.handler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.event.CloudPlatformRequest;
import com.sequenceiq.cloudbreak.cloud.event.instance.CollectMetadataRequest;
import com.sequenceiq.cloudbreak.cloud.event.resource.LaunchStackResult;
import com.sequenceiq.cloudbreak.cloud.handler.CloudPlatformEventHandler;
import com.sequenceiq.cloudbreak.cloud.model.CloudInstance;
import com.sequenceiq.cloudbreak.cloud.model.InstanceStatus;
import com.sequenceiq.cloudbreak.cloud.model.InstanceTemplate;
import com.sequenceiq.cloudbreak.cloud.model.Volume;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Component
public class LaunchStackResultHandler implements CloudPlatformEventHandler<LaunchStackResult> {

    @Inject
    private EventBus eventBus;

    @Override
    public void accept(Event<LaunchStackResult> launchStackResultEvent) {
        CloudPlatformRequest<?> request = launchStackResultEvent.getData().getRequest();
        List<Volume> volumes = Collections.singletonList(new Volume("/mnt/vol","standard", 100));
        Map<String, Object> parameters = Collections.emptyMap();
        InstanceTemplate instanceTemplate = new InstanceTemplate("m5.large", "testGroup", 1234L, volumes,
                InstanceStatus.CREATE_REQUESTED, parameters, 1234L, "image_id");
        CollectMetadataRequest collectMetadataRequest = new CollectMetadataRequest(request.getCloudContext(), request.getCloudCredential(), Collections.emptyList(),
                Collections.singletonList(new CloudInstance(null, instanceTemplate, null)), Collections.emptyList());
        eventBus.notify(collectMetadataRequest.selector(), new Event<>(collectMetadataRequest));
    }

    @Override
    public Class<LaunchStackResult> type() {
        return LaunchStackResult.class;
    }
}
