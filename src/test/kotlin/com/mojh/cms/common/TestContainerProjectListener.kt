package com.mojh.cms.common

import io.kotest.core.annotation.AutoScan
import io.kotest.core.listeners.ProjectListener
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File

@AutoScan
object TestContainerProjectListener : ProjectListener {

    val dockerComposeContainer = DockerComposeContainer<Nothing>(File("src/test/resources/compose-test.yml")).apply {
        withExposedService("mysql", 3306)
        withExposedService("redis", 6379)
        withOptions("--compatibility")
        withLocalCompose(true)
        waitingFor("mysql", Wait.forListeningPort())
        waitingFor("redis", Wait.forListeningPort())
    }

    override suspend fun beforeProject() {
        dockerComposeContainer.start()
    }

    override suspend fun afterProject() {
        dockerComposeContainer.stop()
    }
}