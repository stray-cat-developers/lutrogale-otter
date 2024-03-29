package io.mustelidae.otter.lutrogale.api.domain

import io.kotest.matchers.shouldBe
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import org.junit.jupiter.api.Test

internal class SampleControllerTest : FlowTestSupport() {

    @Test
    fun helloWorld() {
        // Given
        val sampleControllerFlow = SampleControllerFlow(mockMvc)
        // When
        val reply = sampleControllerFlow.helloWorld()
        // Then
        reply shouldBe "Hello World"
    }

    @Test
    fun helloWorld2() {
        // Given
        val sampleControllerFlow = SampleControllerFlow(mockMvc)
        // When
        val replies = sampleControllerFlow.helloWorld2()
        // Then
        replies.joinToString(",") shouldBe "a,b,c"
    }
}
