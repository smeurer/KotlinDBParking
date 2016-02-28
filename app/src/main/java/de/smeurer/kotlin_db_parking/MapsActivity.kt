package de.smeurer.kotlin_db_parking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import de.smeurer.kotlin_db_parking.api.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.async
import org.jetbrains.anko.onUiThread
import java.util.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {


    private var mMap: GoogleMap? = null

    private var markerMap: HashMap<Marker, Site> = HashMap()
    private var occupancyMap: HashMap<Site, Occupancy> = HashMap()

    var behavior: BottomSheetBehavior<LinearLayout>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        map_view.onCreate(savedInstanceState)
        map_view.getMapAsync(this)

        behavior = BottomSheetBehavior.from(bottom_sheet)
        behavior?.isHideable = true

        behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // React to state change

                Log.d("Test", "newState: $newState")
                if (newState == BottomSheetBehavior.STATE_EXPANDED || newState == BottomSheetBehavior.STATE_SETTLING) {
                    fab.show()
                } else {
                    fab.hide()
                }

                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    selectedMarker = null
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })

        fab.setOnClickListener {
            startDirectionsIntent()
        }
    }

    fun startDirectionsIntent() {
        if (selectedMarker != null) {
            val gmmIntentUri = Uri.parse(
                    "google.navigation:q=" + selectedMarker!!.position.latitude + "," + selectedMarker!!.position.longitude + "&mode=d")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            startActivity(mapIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        map_view.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setOnMarkerClickListener(this)
        mMap?.setOnMapClickListener(this)

        async() {
            loadData()
        }
    }

    private fun loadData() {
        val sitesResult = SitesRequest().execute()

        val occupancyResult = OccupancyRequest().execute()

        showSites(sitesResult, occupancyResult)

    }

    private fun getOccupancyForSite(site: Site, occupancyResult: OccupancyResult): Occupancy? {
        for (occupancy in occupancyResult) {
            if (occupancy.site.id.equals(site.parkraumId.toInt())) {
                return occupancy
            }
        }
        return null
    }

    private fun showSites(sitesResult: SitesResult, occupancyResult: OccupancyResult) {
        var builder = LatLngBounds.builder()

        for (site in sitesResult) {
            val occupancyForSite = getOccupancyForSite(site, occupancyResult)

            onUiThread {
                var marker: Marker
                if (occupancyForSite != null) {
                    marker = mMap?.addMarker(MarkerOptions().icon(
                            BitmapDescriptorFactory.defaultMarker((30 * occupancyForSite.allocation.category).toFloat()))
                            .position(site.getPosition()).title(occupancyForSite.site.displayName)
                            .snippet("Freie Parkplätze: " + occupancyForSite.allocation.text + "\n" + site.parkraumOeffnungszeiten))!!
                    occupancyMap.put(site, occupancyForSite)
                } else {
                    marker = mMap?.addMarker(MarkerOptions().icon(
                            BitmapDescriptorFactory.defaultMarker((BitmapDescriptorFactory.HUE_VIOLET).toFloat()))
                            .position(site.getPosition()).title(site.parkraumBahnhofName)
                            .snippet(site.parkraumOeffnungszeiten))!!
                }
                markerMap.put(marker, site)
                builder.include(marker.position)
            }
        }
        onUiThread {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
        }
    }


    private var selectedMarker: Marker? = null

    override fun onMarkerClick(marker: Marker?): Boolean {
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        //        fab.show()

        selectedMarker = marker

        var site = markerMap.get(marker)
        if (site != null) {
            text_title.text = site.parkraumBahnhofName
            text_opening_hours.text = site.parkraumOeffnungszeiten

            var occupancy = occupancyMap.get(site)
            if (occupancy == null) {
                text_parking_lots.text = "Max. ${site.parkraumStellplaetze}"
            } else {
                text_parking_lots.text = occupancy.allocation.text
            }
        }


        return true
    }

    override fun onMapClick(p0: LatLng?) {
        selectedMarker = null
        fab.hide()
        behavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }
}

enum class BottomSheetState {
    hidden, visible
}
