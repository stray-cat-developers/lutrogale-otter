package io.mustelidae.otter.lutrogale.web.domain.project

class ProjectTest

fun Project.Companion.aFixture(apiKey: String): Project =
    Project("Sample", null, apiKey, io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation.ListStructure.FLAT)
