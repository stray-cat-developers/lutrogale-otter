package io.mustelidae.grantotter.utils

import io.mustelidae.otter.lutrogale.config.AppEnvironment
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.util.TimeValue
import org.apache.hc.core5.util.Timeout
import org.apache.hc.client5.http.config.ConnectionConfig as HttpConnectionConfig

object RestClient {
    fun new(connInfo: ConnectionConfig): CloseableHttpClient {
        val httpConnConfig =
            HttpConnectionConfig
                .custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connInfo.connTimeout.toLong()))
                .setTimeToLive(TimeValue.ofSeconds(connInfo.connLiveDuration))
                .build()

        val manager =
            PoolingHttpClientConnectionManagerBuilder
                .create()
                .setMaxConnPerRoute(connInfo.perRoute)
                .setMaxConnTotal(connInfo.connTotal)
                .setDefaultConnectionConfig(httpConnConfig)
                .build()

        return HttpClients
            .custom()
            .setConnectionManager(manager)
            .setDefaultRequestConfig(
                RequestConfig
                    .custom()
                    .setResponseTimeout(Timeout.ofMilliseconds(connInfo.readTimeout * 2))
                    .build(),
            ).build()
    }
}

data class ConnectionConfig(
    val connTimeout: Int,
    val readTimeout: Long,
    val perRoute: Int,
    val connTotal: Int,
    val connLiveDuration: Long,
) {
    companion object {
        fun from(connection: AppEnvironment.Connection): ConnectionConfig =
            connection.run {
                ConnectionConfig(
                    connectionTimeout.toInt(),
                    responseTimeout,
                    50,
                    100,
                    30,
                )
            }
    }
}
