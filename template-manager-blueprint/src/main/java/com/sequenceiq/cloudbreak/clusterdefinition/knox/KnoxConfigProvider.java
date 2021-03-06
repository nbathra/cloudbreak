package com.sequenceiq.cloudbreak.clusterdefinition.knox;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.InstanceGroupType;
import com.sequenceiq.cloudbreak.template.ClusterDefinitionComponentConfigProvider;
import com.sequenceiq.cloudbreak.template.TemplatePreparationObject;
import com.sequenceiq.cloudbreak.template.processor.ClusterDefinitionTextProcessor;
import com.sequenceiq.cloudbreak.template.views.HostgroupView;

@Component
public class KnoxConfigProvider implements ClusterDefinitionComponentConfigProvider {

    private static final String KNOX_GATEWAY = "KNOX_GATEWAY";

    // TODO-MASTER-REPAIR knox should be enabled
    @Override
    public ClusterDefinitionTextProcessor customTextManipulation(TemplatePreparationObject source, ClusterDefinitionTextProcessor blueprintProcessor) {
        Set<String> hostGroupNames = source.getHostgroupViews()
                .stream()
                .filter(hostgroupView -> InstanceGroupType.isGateway(hostgroupView.getInstanceGroupType()))
                .map(HostgroupView::getName)
                .collect(Collectors.toSet());
        return blueprintProcessor.addComponentToHostgroups(KNOX_GATEWAY, hostGroupNames::contains);
    }

    @Override
    public boolean specialCondition(TemplatePreparationObject source, String clusterDefinitionText) {
        return source.getGeneralClusterConfigs().isGatewayInstanceMetadataPresented();
    }
}
