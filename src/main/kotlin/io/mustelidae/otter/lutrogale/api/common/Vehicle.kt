package io.mustelidae.otter.lutrogale.api.common

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Common.Vehicle")
data class Vehicle(
    val gradeId: Long,
    val name: String,
    val platNumber: String
)
