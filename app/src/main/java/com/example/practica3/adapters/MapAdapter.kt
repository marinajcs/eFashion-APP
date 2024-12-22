package com.example.practica3.adapters

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.practica3.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapAdapter(private val context: Context) :
    RecyclerView.Adapter<MapAdapter.MapViewHolder>() {

    private val locations = listOf(
        Pair(GeoPoint(36.7213, -4.4214), "Almacén Málaga"),
        Pair(GeoPoint(37.1765, -3.5979), "Almacén Granada"),
        Pair(GeoPoint(37.9922, -1.1307), "Almacén Murcia")
    )

    init {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.maps, parent, false)
        return MapViewHolder(view)
    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        holder.bind(locations, context)
    }

    override fun getItemCount(): Int = 1

    class MapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mapView: MapView = view.findViewById(R.id.mapView)
        private lateinit var myLocationOverlay: MyLocationNewOverlay

        fun bind(locations: List<Pair<GeoPoint, String>>, context: Context) {
            mapView.controller.setZoom(6.5)
            mapView.controller.setCenter(GeoPoint(37.3891, -5.9845))

            locations.forEach { (location, title) ->
                val marker = Marker(mapView)
                marker.position = location
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = title
                mapView.overlays.add(marker)
            }

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
                myLocationOverlay.enableMyLocation()
                myLocationOverlay.enableFollowLocation()
                mapView.overlays.add(myLocationOverlay)

                myLocationOverlay.runOnFirstFix {
                    mapView.post {
                        mapView.controller.animateTo(myLocationOverlay.myLocation)
                    }
                }
            }

            mapView.invalidate()
        }
    }
}
