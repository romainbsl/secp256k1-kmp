plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(project(":jni:jvm"))
}

val copyJni by tasks.creating(Sync::class) {
    onlyIf { org.gradle.internal.os.OperatingSystem.current().isLinux }
    dependsOn(":jni:jvm:buildNativeHost")
    from(rootDir.resolve("jni/jvm/build/linux/libsecp256k1-jni.so"))
    into(buildDir.resolve("jniResources/fr/acinq/secp256k1/jni/native/linux-x86_64"))
}

(tasks["processResources"] as ProcessResources).apply {
    onlyIf { org.gradle.internal.os.OperatingSystem.current().isLinux }
    dependsOn(copyJni)
    from(buildDir.resolve("jniResources"))
}

publishing {
    publications {
        val pub = create<MavenPublication>("jvm") {
            artifactId = "secp256k1-jni-jvm-linux"
            from(components["java"])
        }
        if (!org.gradle.internal.os.OperatingSystem.current().isLinux) {
            tasks.withType<AbstractPublishToMaven>().all { onlyIf { publication != pub } }
        }
    }
}
