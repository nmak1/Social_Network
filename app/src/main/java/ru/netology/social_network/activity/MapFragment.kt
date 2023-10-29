package ru.netology.social_network.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.MapKitFactory.getInstance
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.ui_view.ViewProvider
import ru.netology.social_network.BuildConfig.MAPS_API_KEY
import ru.netology.social_network.R
import ru.netology.social_network.adapter.MapAdapter
import ru.netology.social_network.model.MapModel

class MapFragment : Fragment(), LocationListener, InputListener {

    private lateinit var mapView: MapView
    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var marksObject: MapObjectCollection
    private lateinit var locationManager: LocationManager

    private var position: Point? = null
    private var open: String? = null
    lateinit var listview: ListView
    var list = mutableListOf<MapModel>()
    lateinit var adapter: MapAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                mapView.apply {
                    userLocationLayer.isVisible = true
                    userLocationLayer.isHeadingEnabled = false
                }
            } else {
                Toast.makeText(context, R.string.no_location, Toast.LENGTH_LONG)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(MAPS_API_KEY)
        MapKitFactory.initialize(requireContext())


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        (activity as AppCompatActivity).supportActionBar?.title =
            context?.getString(R.string.title_map)

        mapView = view.findViewById(R.id.mapview_map)
        userLocationLayer = getInstance().createUserLocationLayer(mapView.mapWindow)
        locationManager = getInstance().createLocationManager()
        marksObject = mapView.map.mapObjects.addCollection()
        mapView.map.addInputListener(this)

        view.findViewById<View>(R.id.button_location).setOnClickListener {
            try {
                moveCamera(userLocationLayer.cameraPosition()?.target!!)
            } catch (e: Exception) {
                Toast.makeText(context, R.string.retry, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val lat = arguments?.getDouble("lat")
        val long = arguments?.getDouble("long")
        open = arguments?.getString("open")
        position =
            if (open == "newEvent") {
                Point(56.319504, 44.013038)
            } else Point(lat!!, long!!)

        if (lat != null && long != null)
            addMarker(Point(lat, long))
        checkPermission()
        position?.let {
            moveCamera(it)
        }

        return view
    }

    private fun moveCamera(point: Point) {
        mapView.map.move(CameraPosition(point, 10F, 0F, 0F))
    }

    private fun addMarker(point: Point) {
        val marker = View(context).apply {
            background = AppCompatResources
                .getDrawable(context, R.drawable.ic_baseline_place_24)
        }
        mapView.map.mapObjects.addPlacemark(
            point,
            ViewProvider(marker)
        )
    }

    private fun checkPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            -> {
                mapView.apply {
                    userLocationLayer.isVisible = true
                    userLocationLayer.isHeadingEnabled = false
                }

                val fusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(requireActivity())

                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    println(it)
                }
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        getInstance().onStop()
    }

    override fun onMapTap(map: Map, point: Point) {
        mapView.map.deselectGeoObject()
    }

    override fun onMapLongTap(map: Map, point: Point) {
        mapView.map.mapObjects.clear()
        view?.findViewById<View>(R.id.button_set_point)?.visibility = View.VISIBLE
        addMarker(point)

        view?.findViewById<View>(R.id.button_set_point)?.setOnClickListener {
            val bundle = Bundle().apply {
                putDouble("lat", point.latitude)
                putDouble("long", point.longitude)
            }
            when (open) {
                "newEvent" ->
                    findNavController().navigate(R.id.nav_new_event_fragment, bundle)
            }
        }
    }

    override fun onLocationChanged(location: Location) {}
}