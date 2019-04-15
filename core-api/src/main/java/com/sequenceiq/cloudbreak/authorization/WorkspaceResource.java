package com.sequenceiq.cloudbreak.authorization;

public enum WorkspaceResource {
    ALL("All resources", "all"),
    WORKSPACE("Workspace", "workspace"),
    CLUSTER_DEFINITION("Cluster definition", "clusterdefinition"),
    IMAGECATALOG("Image catalog", "imagecatalog"),
    CREDENTIAL("Credential", "credential"),
    RECIPE("Recipe", "recipe"),
    STACK("Stack", "stack"),
    LDAP("LDAP config", "ldap"),
    DATABASE("database config", "database"),
    PROXY("Proxy config", "proxy"),
    MPACK("MPACK resource", "mpack"),
    KUBERNETES("Kubernetes config", "kube"),
    STRUCTURED_EVENT("Structured event resource", "structuredevent"),
    CLUSTER_TEMPLATE("Cluster template", "clustertemplate"),
    ENVIRONMENT("Environment", "env"),
    KERBEROS_CONFIG("Kerberos Config", "krbconf");

    private final String readableName;

    private final String shortName;

    WorkspaceResource(String readableName, String shortName) {
        this.readableName = readableName;
        this.shortName = shortName;
    }

    public String getReadableName() {
        return readableName;
    }

    public String getShortName() {
        return shortName;
    }
}
