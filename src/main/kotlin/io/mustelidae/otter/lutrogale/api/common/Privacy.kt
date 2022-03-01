package io.mustelidae.otter.lutrogale.api.common

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Common.Privacy")
data class Privacy(
    val location: Location? = null,
    val vehicle: Vehicle? = null
)
