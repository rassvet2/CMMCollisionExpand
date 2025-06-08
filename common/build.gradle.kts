architectury {
    @Suppress("GroovyAssignabilityCheck")
    common(property("enabled_platforms").toString().split(","))
}

dependencies {
    // We depend on Fabric Loader here to use the Fabric @Environment annotations,
    // which get remapped to the correct annotations on each platform.
    // Do NOT use other classes from Fabric Loader.
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    modImplementation("dev.architectury:architectury:${property("architectury_api_version")}")

    modImplementation("dev.isxander:yet-another-config-lib:${property("yacl_version")}+${property("minecraft_version")}-fabric")
}
