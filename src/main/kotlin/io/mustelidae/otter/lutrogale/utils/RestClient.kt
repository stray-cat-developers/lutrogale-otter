package io.mustelidae.grantotter.utils

import io.mustelidae.otter.lutrogale.config.AppEnvironment
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.util.TimeValue
import java.util.concurrent.TimeUnit

object RestClient {

    fun new(connInfo: ConnectionConfig): CloseableHttpClient {
        val manager = PoolingHttpClientConnectionManagerBuilder.create()
            .setMaxConnPerRoute(connInfo.perRoute)
            .setMaxConnTotal(connInfo.connTotal)
            .setConnectionTimeToLive(TimeValue.ofSeconds(connInfo.connLiveDuration))
            .build()

        return HttpClients.custom()
            .setConnectionManager(manager)
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setConnectTimeout(connInfo.connTimeout.toLong(), TimeUnit.SECONDS)
                    .setResponseTimeout(connInfo.readTimeout * 2, TimeUnit.SECONDS)
                    .build(),
            )
            .build()
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
        fun from(connection: AppEnvironment.Connection): ConnectionConfig {
            return connection.run {
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
}
