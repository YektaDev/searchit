import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
    id("com.moriatsushi.cacheable") version "0.0.3"
}

group = "dev.yekta.searchit"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("Powered by Kobweb")
        }
    }
}

kotlin {
    // This example is frontend only. However, for a fullstack app, you can uncomment the includeServer parameter
    // and the `jvmMain` source set below.
    configAsKobwebApplication("searchit", includeServer = true)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(compose.runtime)
                implementation("com.moriatsushi.cacheable:cacheable-core:0.0.3")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(compose.html.core)
                implementation(libs.kobweb.core)
                implementation(libs.kobweb.silk)
                // This default template uses built-in SVG icons, but what's available is limited.
                // Uncomment the following if you want access to a large set of font-awesome icons:
                // implementation(libs.silk.icons.fa)
                implementation(libs.kobwebx.markdown)
            }
        }

        val sqliteJdbcVersion = "3.44.0.0"
        val exposedVersion = "0.45.0"

        // Uncomment the following if you pass `includeServer = true` into the `configAsKobwebApplication` call.
        val jvmMain by getting {
            dependencies {
                compileOnly(libs.kobweb.api) // Provided by Kobweb backend at runtime

                implementation("org.xerial:sqlite-jdbc:$sqliteJdbcVersion")

                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

                implementation("org.jsoup:jsoup:1.17.1")
            }
        }
    }
}
