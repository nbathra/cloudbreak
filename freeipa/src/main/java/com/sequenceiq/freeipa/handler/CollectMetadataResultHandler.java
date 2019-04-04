package com.sequenceiq.freeipa.handler;

import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.InstanceStatus;
import com.sequenceiq.cloudbreak.cloud.event.instance.CollectMetadataResult;
import com.sequenceiq.cloudbreak.cloud.handler.CloudPlatformEventHandler;
import com.sequenceiq.cloudbreak.cloud.model.CloudInstance;
import com.sequenceiq.cloudbreak.cloud.model.CloudInstanceMetaData;
import com.sequenceiq.freeipa.entity.InstanceMetaData;
import com.sequenceiq.freeipa.event.SetupTlsRequest;
import com.sequenceiq.freeipa.repository.InstanceMetaDataRepository;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Component
public class CollectMetadataResultHandler implements CloudPlatformEventHandler<CollectMetadataResult> {

    @Inject
    private InstanceMetaDataRepository instanceMetaDataRepository;

    @Inject
    private EventBus eventBus;

    @Override
    public Class<CollectMetadataResult> type() {
        return CollectMetadataResult.class;
    }

    @Override
    public void accept(Event<CollectMetadataResult> collectMetadataResultEvent) {
        CollectMetadataResult result = collectMetadataResultEvent.getData();
        Set<InstanceMetaData> instanceMetaDatas = instanceMetaDataRepository.findAllInStack(result.getRequest().getCloudContext().getId());
        Iterator<InstanceMetaData> instanceMetaDataIterator = instanceMetaDatas.iterator();
        result.getResults().forEach(cloudVmMetaDataStatus -> {
            InstanceMetaData instanceMetaData = instanceMetaDataIterator.next();
            CloudInstanceMetaData metaData = cloudVmMetaDataStatus.getMetaData();
            CloudInstance cloudInstance = cloudVmMetaDataStatus.getCloudVmInstanceStatus().getCloudInstance();
            instanceMetaData.setPrivateIp(metaData.getPrivateIp());
            instanceMetaData.setPublicIp(metaData.getPublicIp());
            instanceMetaData.setInstanceId(cloudInstance.getInstanceId());
            instanceMetaData.setPrivateId(1L);
            instanceMetaData.setInstanceName(cloudInstance.getStringParameter(CloudInstance.INSTANCE_NAME));
            instanceMetaData.setInstanceStatus(InstanceStatus.CREATED);
            instanceMetaData.setSshPort(22);
            instanceMetaDataRepository.save(instanceMetaData);
        });

        SetupTlsRequest setupTlsRequest = new SetupTlsRequest(result.getStackId());
        eventBus.notify(setupTlsRequest.selector(), new Event<>(setupTlsRequest));
    }
}
