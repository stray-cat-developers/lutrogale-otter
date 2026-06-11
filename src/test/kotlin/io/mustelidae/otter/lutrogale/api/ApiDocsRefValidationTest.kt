package io.mustelidae.otter.lutrogale.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class ApiDocsRefValidationTest : FlowTestSupport() {
    @Test
    fun `all ref targets must exist in components schemas`() {
        val result =
            mockMvc
                .get("/v3/api-docs")
                .andReturn()
                .response.contentAsString
        val mapper = jacksonObjectMapper()
        val tree = mapper.readTree(result)

        val registeredSchemas = tree["components"]["schemas"].fieldNames().asSequence().toSet()

        val specText = tree.toString()
        val refPattern = Regex(""""\\${"$"}ref"\s*:\s*"#/components/schemas/([^"]+)"""")
        val referencedSchemas = refPattern.findAll(specText).map { it.groupValues[1] }.toSet()

        val missing = referencedSchemas - registeredSchemas
        assertThat(missing)
            .describedAs(
                "ref로 참조되지만 components/schemas에 없는 스키마 - Missing: $missing | " +
                    "Registered(${registeredSchemas.size}): ${registeredSchemas.sorted()} | " +
                    "Referenced(${referencedSchemas.size}): ${referencedSchemas.sorted()}",
            ).isEmpty()
    }
}
