package com.sequenceiq.freeipa.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.client.SaltClientConfig;
import com.sequenceiq.cloudbreak.common.service.HostDiscoveryService;
import com.sequenceiq.cloudbreak.orchestrator.exception.CloudbreakOrchestratorException;
import com.sequenceiq.cloudbreak.orchestrator.host.HostOrchestrator;
import com.sequenceiq.cloudbreak.orchestrator.model.BootstrapParams;
import com.sequenceiq.cloudbreak.orchestrator.model.GatewayConfig;
import com.sequenceiq.cloudbreak.orchestrator.model.Node;
import com.sequenceiq.cloudbreak.orchestrator.state.ExitCriteriaModel;
import com.sequenceiq.freeipa.entity.InstanceMetaData;
import com.sequenceiq.freeipa.entity.SaltSecurityConfig;
import com.sequenceiq.freeipa.entity.SecurityConfig;
import com.sequenceiq.freeipa.entity.Stack;
import com.sequenceiq.freeipa.repository.InstanceMetaDataRepository;
import com.sequenceiq.freeipa.repository.StackRepository;

@Service
public class BootstrapService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapService.class);

    @Inject
    private HostOrchestrator hostOrchestrator;

    @Inject
    private InstanceMetaDataRepository instanceMetaDataRepository;

    @Inject
    private StackRepository stackRepository;

    @Inject
    private TlsSecurityService tlsSecurityService;

    @Inject
    private HostDiscoveryService hostDiscoveryService;

    public void bootstrap(Long stackId) {
        InstanceMetaData instanceMetaData = instanceMetaDataRepository.findAllInStack(stackId).iterator().next();
        Stack stack = stackRepository.findById(stackId).get();
        SaltClientConfig saltClientConfig = getSaltClientConfig(stack);
        GatewayConfig gatewayConfig = tlsSecurityService.buildGatewayConfig(stackId, instanceMetaData, 9443, saltClientConfig, false);
        Node node = new Node(instanceMetaData.getPrivateIp(), instanceMetaData.getPublicIp(), null, "testGroup");
        BootstrapParams params = new BootstrapParams();
        params.setCloud("AWS");
        params.setOs("amazonlinux2");
        try {
            byte[] stateConfigZip = getStateConfigZip();
            hostOrchestrator.bootstrapNewNodes(Collections.singletonList(gatewayConfig), Collections.singleton(node), Collections.singleton(node),
                    stateConfigZip, params, new MyExitCriteriaModel());
            hostOrchestrator.runService(Collections.singletonList(gatewayConfig), Collections.singleton(node), null, new MyExitCriteriaModel());
        } catch (IOException e) {
            LOGGER.error("Couldnt read state config", e);
        } catch (CloudbreakOrchestratorException e) {
            LOGGER.error("Bootstrap failed", e);
        }
    }

    private byte[] getStateConfigZip() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ZipOutputStream zout = new ZipOutputStream(baos)) {
                ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Map<String, List<Resource>> structure = new TreeMap<>();
                for (Resource resource : resolver.getResources("classpath*:freeipa-salt/**")) {
                    String path = resource.getURL().getPath();
                    String dir = path.substring(path.indexOf("/freeipa-salt") + "/freeipa-salt".length(), path.lastIndexOf('/') + 1);
                    List<Resource> list = structure.get(dir);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    structure.put(dir, list);
                    if (!path.endsWith("/")) {
                        list.add(resource);
                    }
                }
                for (Map.Entry<String, List<Resource>> entry : structure.entrySet()) {
                    zout.putNextEntry(new ZipEntry(entry.getKey()));
                    for (Resource resource : entry.getValue()) {
                        LOGGER.debug("Zip salt entry: {}", resource.getFilename());
                        zout.putNextEntry(new ZipEntry(entry.getKey() + resource.getFilename()));
                        InputStream inputStream = resource.getInputStream();
                        byte[] bytes = IOUtils.toByteArray(inputStream);
                        zout.write(bytes);
                        zout.closeEntry();
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Failed to zip salt configurations", e);
                throw new IOException("Failed to zip salt configurations", e);
            }
            return baos.toByteArray();
        }
    }

    private SaltClientConfig getSaltClientConfig(Stack stack) {
        SecurityConfig securityConfig = stack.getSecurityConfig();
        SaltSecurityConfig saltSecurityConfig = securityConfig.getSaltSecurityConfig();
        String privateKey = saltSecurityConfig.getSaltBootSignPrivateKey();
        String saltBootPassword = saltSecurityConfig.getSaltBootPassword();
        String saltPassword = saltSecurityConfig.getSaltPassword();
        return new SaltClientConfig(saltPassword, saltBootPassword, new String(Base64.decodeBase64(privateKey)));
    }

    private static class MyExitCriteriaModel extends ExitCriteriaModel {
    }
}
