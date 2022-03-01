package io.mustelidae.otter.lutrogale.utils

import io.mustelidae.otter.lutrogale.web.commons.constant.DateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun LocalDateTime.toDateString(): String {
    val formatter = DateTimeFormatter.ofPattern(DateFormat.localDate).withZone(ZoneId.systemDefault())
    return formatter.format(this)
}
