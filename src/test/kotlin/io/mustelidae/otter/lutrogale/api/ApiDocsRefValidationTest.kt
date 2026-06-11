package io.mustelidae.otter.lutrogale.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mustelidae.otter.lutrogale.api.config.FlowTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class ApiDocsRefValidationTest : FlowTestSupport() {
    @Test
    fun `all ref targets must exist in components schemas`() {
        val mapper = jacksonObjectMapper()
        val refPattern = Regex(""""\${"$"}ref"\s*:\s*"#/components/schemas/([^"]+)"""")

        for (path in listOf("/v3/api-docs/API", "/v3/api-docs/Maintenance")) {
            val result =
                mockMvc
                    .get(path)
                    .andReturn()
                    .response.contentAsString
            val tree = mapper.readTree(result)

            val registeredSchemas = tree["components"]["schemas"].fieldNames().asSequence().toSet()
            val specText = tree.toString()
            val referencedSchemas = refPattern.findAll(specText).map { it.groupValues[1] }.toSet()
            val missing = referencedSchemas - registeredSchemas

            assertThat(missing)
                .describedAs(
                    "[$path] ref로 참조되지만 components/schemas에 없는 스키마 - Missing: $missing | " +
                        "Registered(${registeredSchemas.size}): ${registeredSchemas.sorted()} | " +
                        "Referenced(${referencedSchemas.size}): ${referencedSchemas.sorted()}",
                ).isEmpty()
        }
    }
}
