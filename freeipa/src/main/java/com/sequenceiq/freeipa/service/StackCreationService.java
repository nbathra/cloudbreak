package com.sequenceiq.freeipa.service;

import static com.sequenceiq.cloudbreak.cloud.model.Platform.platform;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.AdjustmentType;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.InstanceGroupType;
import com.sequenceiq.cloudbreak.client.PkiUtil;
import com.sequenceiq.cloudbreak.cloud.context.CloudContext;
import com.sequenceiq.cloudbreak.cloud.event.platform.GetPlatformTemplateRequest;
import com.sequenceiq.cloudbreak.cloud.event.platform.GetPlatformTemplateResult;
import com.sequenceiq.cloudbreak.cloud.event.platform.PlatformParameterRequest;
import com.sequenceiq.cloudbreak.cloud.event.platform.PlatformParameterResult;
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
import com.sequenceiq.cloudbreak.cloud.model.Platform;
import com.sequenceiq.cloudbreak.cloud.model.PortDefinition;
import com.sequenceiq.cloudbreak.cloud.model.Region;
import com.sequenceiq.cloudbreak.cloud.model.Security;
import com.sequenceiq.cloudbreak.cloud.model.SecurityRule;
import com.sequenceiq.cloudbreak.cloud.model.Subnet;
import com.sequenceiq.cloudbreak.cloud.model.Volume;
import com.sequenceiq.cloudbreak.service.OperationException;
import com.sequenceiq.freeipa.api.CreateFreeIpaRequest;
import com.sequenceiq.freeipa.entity.SaltSecurityConfig;
import com.sequenceiq.freeipa.entity.SecurityConfig;
import com.sequenceiq.freeipa.entity.Stack;
import com.sequenceiq.freeipa.entity.StackAuthentication;
import com.sequenceiq.freeipa.repository.SaltSecurityConfigRepository;
import com.sequenceiq.freeipa.repository.SecurityConfigRepository;
import com.sequenceiq.freeipa.repository.StackAuthenticationRepository;
import com.sequenceiq.freeipa.repository.StackRepository;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
public class StackCreationService {

    @Inject
    private EventBus eventBus;

    @Inject
    private UserDataBuilder userDataBuilder;

    @Inject
    private TlsSecurityService tlsSecurityService;

    @Inject
    private StackRepository stackRepository;

    @Inject
    private SecurityConfigRepository securityConfigRepository;

    @Inject
    private SaltSecurityConfigRepository saltSecurityConfigRepository;

    @Inject
    private StackAuthenticationRepository stackAuthenticationRepository;

    public void launchStack(CreateFreeIpaRequest request) {
        Stack stack = createStack(request);
        Location location = Location.location(Region.region(request.getRegion()), AvailabilityZone.availabilityZone(request.getAvailabilityZone()));
        CloudContext cloudCtx = new CloudContext(1L, request.getName(), "AWS", "AWS", location, "1", 1L);
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
        CloudInstance cloudInstance = new CloudInstance(null, instanceTemplate, instanceAuthentication);
        List<CloudInstance> instances = Collections.singletonList(cloudInstance);
        Group group = new Group("testGroup", InstanceGroupType.GATEWAY, instances, security, cloudInstance, instanceAuthentication,
                "coudbreak", request.getPublicKey(), 50);
        Map<InstanceGroupType, String> userData = userData(stack, cloudCtx, cloudCredential);
        Image image = new Image("ami-00a6ece830a9d2cfb", userData, "amazonlinux2", "amazonlinux2",
                "https://cloudbreak-imagecatalog.s3.amazonaws.com/v2-prod-cb-image-catalog.json", "imagecatalog",
                request.getImageId(), Collections.emptyMap());
        Network network = new Network(new Subnet("10.10.0.0/16"));
        CloudStack cloudStack = new CloudStack(Collections.singleton(group), network, image,
                Collections.emptyMap(), Map.of("Owner", "test"), templateResult.getTemplate(), instanceAuthentication, "cloudbreak",
                request.getPublicKey(), null);
        LaunchStackRequest launchStackRequest = new LaunchStackRequest(cloudCtx, cloudCredential, cloudStack, AdjustmentType.EXACT, 1L);
        eventBus.notify(launchStackRequest.selector(), new Event<>(launchStackRequest));
    }

    private Map<InstanceGroupType, String> userData(Stack stack, CloudContext cloudContext, CloudCredential cloudCredential) {
        PlatformParameterRequest parameterRequest = new PlatformParameterRequest(cloudContext, cloudCredential);
        eventBus.notify(parameterRequest.selector(), new Event<>(parameterRequest));
        PlatformParameterResult res;
        try {
            res = parameterRequest.await();
        } catch (InterruptedException e) {
            throw new OperationException(e);
        }
        Platform platform = platform(stack.getCloudplatform());
        String region = stack.getRegion();
        SecurityConfig securityConfig = stack.getSecurityConfig();
        SaltSecurityConfig saltSecurityConfig = securityConfig.getSaltSecurityConfig();
        String cbPrivKey = saltSecurityConfig.getSaltBootSignPrivateKey();
        byte[] cbSshKeyDer = PkiUtil.getPublicKeyDer(new String(Base64.decodeBase64(cbPrivKey)));
        String sshUser = stack.getStackAuthentication().getLoginUserName();
        String cbCert = securityConfig.getClientCert();
        String saltBootPassword = saltSecurityConfig.getSaltBootPassword();
        return userDataBuilder.buildUserData(platform, cbSshKeyDer, sshUser, res.getPlatformParameters(), saltBootPassword, cbCert);
    }

    private Stack createStack(CreateFreeIpaRequest request) {
        Stack stack = new Stack();
        stack.setName(request.getName());
        stack.setRegion(request.getRegion());
        stack.setAvailabilityzone(request.getAvailabilityZone());
        stack.setCloudplatform("AWS");
        stack.setPlatformvariant("AWS");
        StackAuthentication stackAuthentication = new StackAuthentication();
        stackAuthentication.setLoginUserName("cloudbreak");
        stackAuthentication.setPublicKey(request.getPublicKey());
        stack.setStackAuthentication(stackAuthentication);
        stack = stackRepository.save(stack);
        SecurityConfig securityConfig = tlsSecurityService.generateSecurityKeys();
        securityConfig.setStack(stack);
        securityConfig = securityConfigRepository.save(securityConfig);
        stack.setSecurityConfig(securityConfig);
        return stackRepository.save(stack);
    }
}
