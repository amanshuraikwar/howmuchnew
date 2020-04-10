package io.github.amanshuraikwar.nxtbuz.util

import org.threeten.bp.format.DateTimeFormatter

object TimeUtil {

    // todo
    @JvmStatic
    val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    @JvmStatic
    val TIME_READABLE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
}