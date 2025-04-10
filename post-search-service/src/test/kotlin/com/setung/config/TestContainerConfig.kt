package com.setung.config

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Testcontainers

@ExtendWith(SpringExtension::class)
@Testcontainers
@TestConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestContainerConfig {

    companion object {
        private val elasticsearchContainer = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.1")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withExposedPorts(9200)
            .withReuse(true)

        init {
            elasticsearchContainer.start()
            System.setProperty("spring.elasticsearch.uris", elasticsearchContainer.httpHostAddress)
        }
    }
}