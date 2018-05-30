package com.sequenceiq.cloudbreak.api.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public enum ExposedService {

    ALL("Every Service", "ALL", "", "", true),

    AMBARI("Ambari", "AMBARI_SERVER", "AMBARI", "/ambari/", true),
    WEBHDFS("WebHDFS", "NAMENODE", "WEBHDFS", "/webhdfs/", false),
    NAMENODE("Name Node", "NAMENODE", "HDFSUI", "/hdfs/", true),
    RESOURCEMANAGER_WEB("Resource Manager", "RESOURCEMANAGER", "YARNUI", "/yarn/", true),
    JOB_HISTORY_SERVER("Job History Server", "HISTORYSERVER", "JOBHISTORYUI", "/jobhistory/", true),
    HIVE_SERVER("Hive Server", "HIVE_SERVER", "HIVE", "", false),
    HIVE_SERVER_INTERACTIVE("Hive Server Interactive", "HIVE_SERVER_INTERACTIVE", "HIVE_INTERACTIVE", "", false),
    ATLAS("Atlas", "ATLAS_SERVER", "ATLAS", "/atlas/", true),
    SPARK_HISTORY_SERVER("Spark History Server", "SPARK_JOBHISTORYSERVER", "SPARKHISTORYUI", "/sparkhistory/", true),
    ZEPPELIN("Zeppelin", "ZEPPELIN_MASTER", "ZEPPELIN", "/zeppelin/", false),
    RANGER("Ranger", "RANGER_ADMIN", "RANGERUI", "/ranger/", true),
    DP_PROFILER_AGENT("DP Profiler Agent", "DP_PROFILER_AGENT", "PROFILER-AGENT", "", true),
    BEACON_SERVER("Beacon", "BEACON_SERVER", "BEACON", "", true);

    private final String serviceName;
    private final String portName;
    private final String knoxService;
    private final String knoxUrl;
    private final boolean ssoSupported;

    ExposedService(String portName, String serviceName, String knoxService, String knoxUrl, boolean ssoSupported) {
        this.portName = portName;
        this.serviceName = serviceName;
        this.knoxService = knoxService;
        this.knoxUrl = knoxUrl;
        this.ssoSupported = ssoSupported;
    }

    public static boolean isKnoxExposed(String knoxService) {
        return getAllKnoxExposed().contains(knoxService);
    }

    public static Collection<ExposedService> filterSupportedKnoxServices() {
        return Arrays.stream(values()).filter(x -> !Strings.isNullOrEmpty(x.knoxService)).collect(Collectors.toList());
    }

    public static Collection<ExposedService> knoxServicesForComponents(Collection<String> components) {
        Collection<ExposedService> supportedKnoxServices = ExposedService.filterSupportedKnoxServices();
        return supportedKnoxServices.stream()
                .filter(exposedService ->
                        components.contains(exposedService.getServiceName())
                                || "AMBARI_SERVER".equals(exposedService.getServiceName()))
                .collect(Collectors.toList());
    }

    public static List<String> getAllKnoxExposed() {
        List<String> allKnoxExposed = filterSupportedKnoxServices().stream().map(ExposedService::getKnoxService).collect(Collectors.toList());
        return ImmutableList.copyOf(allKnoxExposed);
    }

    public static List<String> getAllServiceName() {
        List<String> allServiceName = Arrays.stream(values()).filter(x -> !Strings.isNullOrEmpty(x.serviceName))
                .map(ExposedService::getServiceName).collect(Collectors.toList());
        return ImmutableList.copyOf(allServiceName);
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPortName() {
        return portName;
    }

    public String getKnoxService() {
        return knoxService;
    }

    public String getKnoxUrl() {
        return knoxUrl;
    }

    public boolean isSSOSupported() {
        return ssoSupported;
    }
}
