package io.mustelidae.otter.lutrogale

import io.mustelidae.otter.lutrogale.config.AppEnvironment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.session.JdbcSessionProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator

@SpringBootApplication
@EnableConfigurationProperties(JdbcSessionProperties::class, AppEnvironment::class)
@ComponentScan(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
