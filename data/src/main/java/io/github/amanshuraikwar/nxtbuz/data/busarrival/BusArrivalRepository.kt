package io.github.amanshuraikwar.nxtbuz.data.busarrival

import io.github.amanshuraikwar.ltaapi.LtaApi
import io.github.amanshuraikwar.ltaapi.model.ArrivingBusItemDto
import io.github.amanshuraikwar.ltaapi.model.BusArrivalItemDto
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.datasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.common.datasource.OperatingBusEntity
import io.github.amanshuraikwar.nxtbuz.common.model.*
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.*
import io.github.amanshuraikwar.nxtbuz.common.model.exception.IllegalDbStateException
import io.github.amanshuraikwar.nxtbuz.common.util.TimeUtil
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetTime
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusArrivalRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val busApi: LtaApi,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {
    suspend fun getBusArrivals(busStopCode: String): List<BusStopArrival> {
        return withContext(dispatcherProvider.io) {
            // map of busServiceNumber -> OperatingBusEntity
            val operatingBusServiceNumberMap =
                localDataSource.findOperatingBuses(busStopCode)
                    .groupBy { it.busServiceNumber }
                    .mapValues { (_, v) -> v[0] }
                    .toMutableMap()

            val busStopArrivalList = mutableListOf<BusStopArrival>()

            busApi.getBusArrivals(busStopCode)
                .busArrivals
                .forEach { busArrivalItemDto ->

                    @Suppress("ControlFlowWithEmptyBody")
                    if (
                        operatingBusServiceNumberMap.containsKey(busArrivalItemDto.serviceNumber)
                    ) {
                        operatingBusServiceNumberMap.remove(busArrivalItemDto.serviceNumber)
                        busStopArrivalList.add(
                            busArrivalItemDto.toBusArrival(
                                busStopCode = busStopCode
                            )
                        )
                    } else {
                        // bus service number returned from remote api is not in local db
                        // TODO-amanshuraikwar (26 May 2021 11:58:35 AM): schedule DB update
                    }
                }

            // add remaining buses as Arrivals.DataNotAvailable or Arrivals.NotOperating
            operatingBusServiceNumberMap.forEach { (_, operatingBusEntity) ->
                busStopArrivalList.add(
                    operatingBusEntity.toBusArrivalError()
                )
            }

            busStopArrivalList
        }
    }

    suspend fun getBusArrivals(busStopCode: String, busServiceNumber: String): BusStopArrival {
        return withContext(dispatcherProvider.io) {
            val operatingBusEntity =
                localDataSource.findOperatingBus(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber
                )
                    ?: run {
                        // bus service number returned from remote api is not in local db
                        throw IllegalDbStateException(
                            "No operating bus row found for service number " +
                                    "$busServiceNumber and stop code " +
                                    "$busStopCode in local DB."
                        )
                    }

            val busArrivalItemList =
                busApi.getBusArrivals(busStopCode, busServiceNumber).busArrivals

            if (busArrivalItemList.isNotEmpty()) {

                val busArrivalItem = busArrivalItemList[0]
                return@withContext busArrivalItem.toBusArrival(busStopCode)

            } else {

                return@withContext operatingBusEntity.toBusArrivalError()
            }
        }
    }

    private suspend inline fun BusArrivalItemDto.toBusArrival(
        busStopCode: String,
    ): BusStopArrival {
        val (_, _, direction, stopSequence, distance) =
            localDataSource
                .findBusRoute(busStopCode = busStopCode, busServiceNumber = serviceNumber)
                ?: throw IllegalDbStateException(
                    "No bus route row found for service number " +
                            "$serviceNumber and stop code " +
                            "$busStopCode in local DB."
                )

        val arrivals = toArrivals(busStopCode)

        return BusStopArrival(
            busStopCode = busStopCode,
            busServiceNumber = serviceNumber,
            operator = operator,
            direction = direction,
            stopSequence = stopSequence,
            distance = distance,
            busArrivals = arrivals
        )
    }

    private suspend inline fun BusArrivalItemDto.toArrivals(busStopCode: String): BusArrivals {
        val arrivingBusDto = arrivingBus

        if (arrivingBusDto == null || arrivingBusDto.estimatedArrival == "") {
            return BusArrivals.Error(
                message = "First arriving bus is null for bus stop $busStopCode " +
                        "and service $serviceNumber"
            )
        }

        val nextArrivingBus = arrivingBusDto.toArrivingBus()

        val followingArrivingBusList = mutableListOf<ArrivingBus>()

        var followArrivingBusDto = arrivingBus1

        if (followArrivingBusDto != null) {
            if (followArrivingBusDto.estimatedArrival != "") {
                followingArrivingBusList.add(followArrivingBusDto.toArrivingBus())
            }
        }

        followArrivingBusDto = arrivingBus2

        if (followArrivingBusDto != null) {
            if (followArrivingBusDto.estimatedArrival != "") {
                followingArrivingBusList.add(followArrivingBusDto.toArrivingBus())
            }
        }

        return BusArrivals.Arriving(nextArrivingBus, followingArrivingBusList)
    }

    private suspend inline fun ArrivingBusItemDto.toArrivingBus(): ArrivingBus {
        val time =
            ChronoUnit.MINUTES.between(
                OffsetDateTime.now(), OffsetDateTime.parse(estimatedArrival)
            )

        val origin: ArrivingBusStop =
            localDataSource
                .findBusStopByCode(busStopCode = originCode)
                ?.let { busStopEntity ->
                    ArrivingBusStop(
                        busStopCode = busStopEntity.code,
                        roadName = busStopEntity.roadName,
                        busStopDescription = busStopEntity.description
                    )
                }
                ?: throw IllegalDbStateException(
                    "No bus stop row found for stop code $originCode (origin) in local DB."
                )

        val destination: ArrivingBusStop =
            localDataSource
                .findBusStopByCode(busStopCode = destinationCode)
                ?.let { busStopEntity ->
                    ArrivingBusStop(
                        busStopCode = busStopEntity.code,
                        roadName = busStopEntity.roadName,
                        busStopDescription = busStopEntity.description
                    )
                }
                ?: throw IllegalDbStateException(
                    "No bus stop row found for stop code " +
                            "$destinationCode (destination) in local DB."
                )

        return ArrivingBus(
            origin,
            destination,
            time.toInt().coerceAtLeast(0),
            latitude.toDouble(),
            longitude.toDouble(),
            visitNumber.toInt(),
            BusLoad.valueOf(load),
            feature == "WAB",
            BusType.valueOf(type)
        )
    }

    private suspend inline fun OperatingBusEntity.toBusArrivalError(): BusStopArrival {
        val (_, _, direction, stopSequence, distance) =
            localDataSource
                .findBusRoute(
                    busServiceNumber = busServiceNumber,
                    busStopCode = busStopCode
                )
                ?: throw IllegalDbStateException(
                    "No bus route row found for service number " +
                            "$busServiceNumber and stop code " +
                            "$busStopCode in local DB."
                )

        val (
            _,
            _,
            wdFirstBus: OffsetTime?,
            wdLastBus: OffsetTime?,
            satFirstBus: OffsetTime?,
            satLastBus: OffsetTime?,
            sunFirstBus: OffsetTime?,
            sunLastBus: OffsetTime?
        ) = this

        val busArrivals: BusArrivals = when {
            TimeUtil.isWeekday() -> {
                getBusArrivalsError(wdFirstBus, wdLastBus)
            }
            TimeUtil.isSaturday() -> {
                getBusArrivalsError(satFirstBus, satLastBus)
            }
            TimeUtil.isSunday() -> {
                getBusArrivalsError(sunFirstBus, sunLastBus)
            }
            else -> {
                throw IllegalDbStateException(
                    "This day is neither a weekday nor a saturday or sunday."
                )
            }
        }

        return BusStopArrival(
            busStopCode = busStopCode,
            busServiceNumber = busServiceNumber,
            operator = "N/A",
            direction = direction,
            stopSequence = stopSequence,
            distance = distance,
            busArrivals = busArrivals
        )
    }

    private fun getBusArrivalsError(
        firstBus: OffsetTime?,
        lastBus: OffsetTime?
    ): BusArrivals {
        if (firstBus != null) {
            if (OffsetTime.now().isBefore(firstBus)) {
                return BusArrivals.NotOperating
            }
        }
        if (lastBus != null) {
            if (OffsetTime.now().isAfter(lastBus)) {
                return BusArrivals.NotOperating
            }
        }
        return BusArrivals.DataNotAvailable
    }
}