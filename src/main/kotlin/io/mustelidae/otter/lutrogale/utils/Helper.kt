package io.mustelidae.otter.lutrogale.utils

import java.net.URLDecoder
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun LocalDateTime.toDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
    return formatter.format(this)
}

fun String.toDecode(): String {
    return URLDecoder.decode(this, Charset.defaultCharset())
}
