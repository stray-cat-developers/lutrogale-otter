package io.mustelidae.otter.lutrogale.config

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Common.Error")
data class GlobalErrorFormat(
    val timestamp: String,
    @Schema(description = "Http Status Code")
    val status: Int,
    @Schema(description = "error code")
    val code: String,
    @Schema(description = "text displayed to the user")
    val description: String? = null,
    @Schema(description = "exception message")
    val message: String,
    @Schema(description = "exception name")
    val type: String,
    @Schema(description = "reference error code")
    val refCode: String? = null
)
