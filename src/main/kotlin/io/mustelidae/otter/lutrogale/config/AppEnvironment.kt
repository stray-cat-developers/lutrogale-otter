package io.mustelidae.otter.lutrogale.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
class AppEnvironment {
    var default = Default()

    class Default : Connection()

    open class Connection {
        var connectionTimeout: Long = 1000
        var responseTimeout: Long = 1000
        var logging: Boolean = false
        var useDummy: Boolean = false
    }
}
