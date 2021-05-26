package io.github.amanshuraikwar.nxtbuz.busroute.ui.model

import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivals

sealed class BusRouteListItemData {
    data class Header(val title: String) : BusRouteListItemData()

    data class BusRoutePreviousAll(val title: String) : BusRouteListItemData()

    sealed class BusRouteNode(
        val busStopCode: String,
        val busStopDescription: String,
        val position: Position,
        val arrivalState: ArrivalState,
    ) : BusRouteListItemData() {

        enum class Position { ORIGIN, DESTINATION, MIDDLE }

        class Current(
            busStopCode: String,
            busStopDescription: String,
            position: Position = Position.MIDDLE,
            arrivalState: ArrivalState = ArrivalState.Fetching,
        ) : BusRouteNode(busStopCode, busStopDescription, position, arrivalState) {
            fun copy(arrivalState: ArrivalState): Current {
                return Current(
                    busStopCode = busStopCode,
                    busStopDescription = busStopDescription,
                    position = position,
                    arrivalState = arrivalState
                )
            }
        }

        class Previous(
            busStopCode: String,
            busStopDescription: String,
            position: Position = Position.MIDDLE,
            arrivalState: ArrivalState = ArrivalState.Inactive,
        ) : BusRouteNode(busStopCode, busStopDescription, position, arrivalState) {
            fun copy(arrivalState: ArrivalState): Previous {
                return Previous(
                    busStopCode = busStopCode,
                    busStopDescription = busStopDescription,
                    position = position,
                    arrivalState = arrivalState
                )
            }
        }

        class Next(
            busStopCode: String,
            busStopDescription: String,
            position: Position = Position.MIDDLE,
            arrivalState: ArrivalState = ArrivalState.Inactive,
        ) : BusRouteNode(busStopCode, busStopDescription, position, arrivalState) {
            fun copy(
                arrivalState: ArrivalState,
            ): Next {
                return Next(
                    busStopCode = busStopCode,
                    busStopDescription = busStopDescription,
                    position = position,
                    arrivalState = arrivalState,
                )
            }
        }
    }

    sealed class ArrivalState {
        object Inactive : ArrivalState()
        object Fetching : ArrivalState()
        data class Active(
            val busArrivals: BusArrivals,
            val lastUpdatedOn: String,
        ) : ArrivalState()
    }
}