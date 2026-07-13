package de.ba.services.github.configuration

import de.ba.services.configuration.Api
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource


@Configuration
@PropertySource(value = ["classpath:application.yml"])
@ConfigurationProperties(prefix = "prometheus-exporter.github")
class GithubConfiguration {
    var api: Api = Api()
    var projects: List<Project> = emptyList()
    var branches: List<String> = emptyList()
}

class Project {
    var path: String = ""
    var template: String = ""
}


