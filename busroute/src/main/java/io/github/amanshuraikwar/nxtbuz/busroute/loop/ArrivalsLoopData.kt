package io.github.amanshuraikwar.nxtbuz.busroute.loop

import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusStopArrival

data class ArrivalsLoopData(
    val busStopCode: String,
    val busServiceNumber: String,
    val busStopArrival: BusStopArrival
)