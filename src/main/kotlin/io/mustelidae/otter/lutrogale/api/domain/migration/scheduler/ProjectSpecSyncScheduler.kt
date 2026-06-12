package io.mustelidae.otter.lutrogale.api.domain.migration.scheduler

import io.mustelidae.otter.lutrogale.api.domain.migration.GraphQLMigrationInteraction
import io.mustelidae.otter.lutrogale.api.domain.migration.OpenAPIMigrationInteraction
import io.mustelidae.otter.lutrogale.config.redis.distributedlock.DistributedLock
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ProjectSpecSyncScheduler(
    private val projectFinder: ProjectFinder,
    private val openAPIMigrationInteraction: OpenAPIMigrationInteraction,
    private val graphQLMigrationInteraction: GraphQLMigrationInteraction,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 5 * 60 * 1000L)
    @DistributedLock(qualifier = "project-spec-sync")
    fun syncAll() {
        val projects = projectFinder.findAllBySyncEnabled()
        if (projects.isEmpty()) return

        log.info("Sync scheduler: syncing ${projects.size} project(s)")

        for (project in projects) {
            val projectId = project.id!!
            try {
                when (project.specType) {
                    Project.SpecType.GRAPHQL -> graphQLMigrationInteraction.sync(projectId)

                    Project.SpecType.OPENAPI_JSON,
                    Project.SpecType.OPENAPI_YAML,
                    -> openAPIMigrationInteraction.sync(projectId)

                    null -> log.warn("Project '${project.name}' has syncEnabled=true but no specType configured")
                }
            } catch (e: Exception) {
                log.error("Failed to sync project '${project.name}': ${e.message}", e)
            }
        }
    }
}
