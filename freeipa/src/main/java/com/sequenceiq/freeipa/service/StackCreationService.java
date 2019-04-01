package com.sequenceiq.freeipa.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.AdjustmentType;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.InstanceGroupType;
import com.sequenceiq.cloudbreak.cloud.context.CloudContext;
import com.sequenceiq.cloudbreak.cloud.event.platform.GetPlatformTemplateRequest;
import com.sequenceiq.cloudbreak.cloud.event.platform.GetPlatformTemplateResult;
import com.sequenceiq.cloudbreak.cloud.event.resource.LaunchStackRequest;
import com.sequenceiq.cloudbreak.cloud.model.AvailabilityZone;
import com.sequenceiq.cloudbreak.cloud.model.CloudCredential;
import com.sequenceiq.cloudbreak.cloud.model.CloudInstance;
import com.sequenceiq.cloudbreak.cloud.model.CloudStack;
import com.sequenceiq.cloudbreak.cloud.model.Group;
import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.cloud.model.InstanceAuthentication;
import com.sequenceiq.cloudbreak.cloud.model.InstanceStatus;
import com.sequenceiq.cloudbreak.cloud.model.InstanceTemplate;
import com.sequenceiq.cloudbreak.cloud.model.Location;
import com.sequenceiq.cloudbreak.cloud.model.Network;
import com.sequenceiq.cloudbreak.cloud.model.PortDefinition;
import com.sequenceiq.cloudbreak.cloud.model.Region;
import com.sequenceiq.cloudbreak.cloud.model.Security;
import com.sequenceiq.cloudbreak.cloud.model.SecurityRule;
import com.sequenceiq.cloudbreak.cloud.model.Subnet;
import com.sequenceiq.cloudbreak.cloud.model.Volume;
import com.sequenceiq.freeipa.api.CreateFreeIpaRequest;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
public class StackCreationService {

    @Inject
    private EventBus eventBus;

    public void launchStack(CreateFreeIpaRequest request) {
        Location location = Location.location(Region.region(request.getRegion()), AvailabilityZone.availabilityZone(request.getAvailabilityZone()));
        CloudContext cloudCtx = new CloudContext(1L, "testIpa", "AWS", "AWS", location, "1", 1L);
        CloudCredential cloudCredential = new CloudCredential(1L, "aws-key",
                Map.of("accessKey", request.getAccessKey(),
                        "secretKey", request.getSecretKey(),
                        "govCloud", false,
                        "selector", "key-based",
                        "smartSenseId", "null"));

        GetPlatformTemplateRequest getPlatformTemplateRequest = new GetPlatformTemplateRequest(cloudCtx, cloudCredential);
        GetPlatformTemplateResult templateResult = null;
        try {
            eventBus.notify(getPlatformTemplateRequest.selector(), new Event<>(getPlatformTemplateRequest));
            templateResult = getPlatformTemplateRequest.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Security security = new Security(Collections.singletonList(new SecurityRule("0.0.0.0/0",
                new PortDefinition[] {new PortDefinition("22", "22")}, "TCP")), Collections.emptyList());
        InstanceAuthentication instanceAuthentication = new InstanceAuthentication(request.getPublicKey(), null, "cloudbreak");
        List<Volume> volumes = Collections.singletonList(new Volume("/mnt/vol","standard", 100));
        Map<String, Object> parameters = Collections.emptyMap();
        InstanceTemplate instanceTemplate = new InstanceTemplate("m5.large", "testGroup", 1234L, volumes,
                InstanceStatus.CREATE_REQUESTED, parameters, 1234L, request.getImageId());
        CloudInstance cloudInstance = new CloudInstance("asdf1234", instanceTemplate, instanceAuthentication);
        List<CloudInstance> instances = Collections.singletonList(cloudInstance);
        Group group = new Group("testGroup", InstanceGroupType.GATEWAY, instances, security, cloudInstance, instanceAuthentication,
                "coudbreak", request.getPublicKey(), 50);
        Image image = new Image("ami-00a6ece830a9d2cfb", Collections.singletonMap(InstanceGroupType.GATEWAY, "asdfasdfasdfasdf"), "amazonlinux2", "amazonlinux2",
                "https://cloudbreak-imagecatalog.s3.amazonaws.com/v2-prod-cb-image-catalog.json", "imagecatalog",
                request.getImageId(), Collections.emptyMap());
        Network network = new Network(new Subnet("10.10.0.0/16"));
        CloudStack cloudStack = new CloudStack(Collections.singleton(group), network, image,
                Collections.emptyMap(), Map.of("Owner", "test"), templateResult.getTemplate(), instanceAuthentication, "cloudbreak",
                request.getPublicKey(), null);
        LaunchStackRequest launchStackRequest = new LaunchStackRequest(cloudCtx, cloudCredential, cloudStack, AdjustmentType.EXACT, 1L);
        eventBus.notify(launchStackRequest.selector(), new Event<>(launchStackRequest));
    }
}
