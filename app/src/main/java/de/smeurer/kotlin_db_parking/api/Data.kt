package de.smeurer.kotlin_db_parking.api

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Simon Meurer on 26.02.16.
 */
data class Site(val parkraumId: String, val parkraumGeoLatitude: String,
                val parkraumGeoLongitude: String, val parkraumStellplaetze: String,
                val parkraumBahnhofName: String, val parkraumOeffnungszeiten: String,
                val zahlungMedien: String, val tarif30Min: String, val tarif1Std : String,
                val tarif1Tag: String, val tarif1Woche: String, val tarifParkdauer: String) {
    fun getPosition(): LatLng {
        val latitude = parkraumGeoLatitude.toDouble()
        val longitude = parkraumGeoLongitude.toDouble()

        return LatLng(latitude, longitude)
    }
}

data class SitesResult(val totalCount: Int, val count: Int, val results: List<Site>) : Iterable<Site> {

    override fun iterator(): Iterator<Site> {
        return results.iterator()
    }

}

data class OccupancyResult(val allocations: List<Occupancy>): Iterable<Occupancy>{
    override fun iterator(): Iterator<Occupancy> {
        return allocations.iterator()
    }
}

data class Occupancy(val site: OccupancySite, val allocation: OccupancyAllocation)

data class OccupancySite(val id: Int, val siteId: Int, val flaechenNummer: Int, val stationName: String, val siteName:String,
                         val displayName: String)

data class OccupancyAllocation(val timestamp: String, val timeSegment: String, val category: Int, val text: String)