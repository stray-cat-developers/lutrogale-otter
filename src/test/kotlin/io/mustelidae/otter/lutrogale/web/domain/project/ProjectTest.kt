package io.mustelidae.otter.lutrogale.web.domain.project

class ProjectTest

fun Project.Companion.aFixture(apiKey: String): Project {
    return Project("Sample", null, apiKey)
}
