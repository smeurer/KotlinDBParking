package de.smeurer.kotlin_db_parking.api

import com.google.gson.Gson
import java.net.URL

/**
 * Created by Simon Meurer on 26.02.16.
 */
class SitesRequest() {
    fun execute(): SitesResult {
        val sitesResultJsonString = URL("http://opendata.dbbahnpark.info/api/beta/sites").readText()
        return Gson().fromJson(sitesResultJsonString, SitesResult::class.java)
    }
}

class OccupancyRequest() {
    fun execute(): OccupancyResult {
        val occupancyResultJsonString = URL("http://opendata.dbbahnpark.info/api/beta/occupancy").readText()
        return Gson().fromJson(occupancyResultJsonString, OccupancyResult::class.java)
    }
}