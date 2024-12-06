package io.mustelidae.otter.lutrogale.api.domain.migration.client

import io.mustelidae.otter.lutrogale.api.domain.migration.openapi.SwaggerSpec
import io.mustelidae.otter.lutrogale.utils.Jackson
import io.mustelidae.otter.lutrogale.utils.RestClientSupport
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.core5.http.ContentType
import org.slf4j.LoggerFactory

/**
 * HTTP 스펙을 순정스럽게 대표하는 클라이언트. HttpSpecClient와 RestClientSupport를 상속합니다.
 *
 * @property restClient HTTP 요청을 보내기 위해 사용되는 CloseableHttpClient입니다.
 * @property writeLog HTTP 요청에 대한 세부사항을 로그로 남길지 여부를 나타내는 부울 플래그입니다.
 */
class StableHttpSpecClient(
    private val restClient: CloseableHttpClient,
    writeLog: Boolean,
) : HttpSpecClient, RestClientSupport(
    Jackson.getMapper(),
    writeLog,
    LoggerFactory.getLogger(StableHttpSpecClient::class.java),
) {
    /**
     * Open API 스펙을 가져오는 함수.
     *
     * @param url 해당하는 API의 URL.
     * @param type SwaggerSpec의 타입.
     * @param headers 요청에 추가할 헤더들 list.
     * @return API 스펙의 String 표현값.
     */
    override fun getOpenAPISpec(url: String, type: SwaggerSpec.Type, headers: List<Pair<String, Any>>?): String {
        val requestHeader = mutableListOf<Pair<String, Any>>()

        headers?.let {
            requestHeader.addAll(it)
        }

        val contentType = when (type) {
            SwaggerSpec.Type.JSON -> Pair("Content-Type", ContentType.APPLICATION_JSON)
            SwaggerSpec.Type.YAML -> Pair("Content-Type", "application/yaml")
        }

        requestHeader.add(contentType)
        return restClient.get(url, requestHeader)
            .orElseThrow()
    }

    /**
     * GraphQL 스펙을 가져오는 함수.
     *
     * @param url 해당하는 API의 URL.
     * @param headers 요청에 추가할 헤더들 list.
     * @return API 스펙의 String 표현값.
     */
    override fun getGraphQLSpec(url: String, headers: List<Pair<String, Any>>?): String {
        val requestHeader = mutableListOf<Pair<String, Any>>()

        headers?.let {
            requestHeader.addAll(it)
        }

        requestHeader.add(Pair("Content-Type", ContentType.TEXT_PLAIN))
        return restClient.get(url, requestHeader)
            .orElseThrow()
    }
}
