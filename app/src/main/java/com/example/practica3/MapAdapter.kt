package com.example.practica3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

class MapAdapter(private val context: Context) :
    RecyclerView.Adapter<MapAdapter.MapViewHolder>() {

    private val locations = listOf(
        GeoPoint(36.7213, -4.4214), // Málaga
        GeoPoint(37.1765, -3.5979), // Granada
        GeoPoint(37.9922, -1.1307)  // Murcia
    )

    init {
        // Configurar OSMDroid
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.maps, parent, false)
        return MapViewHolder(view)
    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        holder.bind(locations)
    }

    override fun getItemCount(): Int = 1

    class MapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mapView: MapView = view.findViewById(R.id.mapView)

        fun bind(locations: List<GeoPoint>) {
            mapView.controller.setZoom(6.5)
            mapView.controller.setCenter(GeoPoint(37.3891, -5.9845)) // Centro aproximado

            // Añadir marcadores
            locations.forEach { location ->
                val marker = Marker(mapView)
                marker.position = location
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(marker)
            }
        }
    }
}
