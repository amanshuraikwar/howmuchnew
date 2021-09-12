//package io.github.amanshuraikwar.nxtbuz.common.model.room
//
//import androidx.room.Entity
//import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivalStatus
//import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusLoad
//import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusType
//import org.threeten.bp.OffsetDateTime
//
//@Entity(primaryKeys = ["busServiceNumber", "busStopCode", "seqNumber"])
//data class BusArrivalEntity(
//    val busServiceNumber: String,
//    val busStopCode: String,
//    val seqNumber: Int,
//    val busArrivalStatus: BusArrivalStatus = BusArrivalStatus.ARRIVING,
//    val originCode: String,
//    val destinationCode: String,
//    val estimatedArrivalTimestamp: OffsetDateTime,
//    val latitude: Double,
//    val longitude: Double,
//    val visitNumber: Int,
//    val load: BusLoad,
//    val feature: String,
//    val type: BusType,
//    val lastUpdatedOn: OffsetDateTime = OffsetDateTime.now()
//) {
//    companion object {
//
//        fun notOperating(
//            busServiceNumber: String,
//            busStopCode: String,
//            seqNumber: Int,
//        ): BusArrivalEntity =
//            error(
//                busServiceNumber,
//                busStopCode,
//                seqNumber,
//                BusArrivalStatus.NOT_OPERATING
//            )
//
//        fun noData(
//            busServiceNumber: String,
//            busStopCode: String,
//            seqNumber: Int,
//        ): BusArrivalEntity =
//            error(
//                busServiceNumber,
//                busStopCode,
//                seqNumber,
//                BusArrivalStatus.NO_DATA
//            )
//
//        fun error(
//            busServiceNumber: String,
//            busStopCode: String,
//            seqNumber: Int,
//            busArrivalStatus: BusArrivalStatus
//        ): BusArrivalEntity =
//            BusArrivalEntity(
//                busServiceNumber = busServiceNumber,
//                busStopCode = busStopCode,
//                seqNumber = seqNumber,
//                busArrivalStatus = busArrivalStatus,
//                originCode = "N/A",
//                destinationCode = "N/A",
//                estimatedArrivalTimestamp = OffsetDateTime.MIN,
//                latitude = -1.0,
//                longitude = -1.0,
//                visitNumber = -1,
//                load = BusLoad.SEA,
//                feature = "N/A",
//                type = BusType.BD,
//            )
//    }
//}