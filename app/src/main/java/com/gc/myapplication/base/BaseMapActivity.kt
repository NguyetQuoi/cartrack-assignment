package com.gc.myapplication.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import com.gc.myapplication.R
import com.gc.myapplication.util.PermissionUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import timber.log.Timber


/**
 * A base activity for CP
 * @author n.quoi
 * @date 10.19.2021
 * @param VM view-model
 * @param VDB view-data-binding
 */

abstract class BaseMapActivity<VM : BindingViewModel, VDB : ViewDataBinding> :
    BaseActivity<VM, VDB>(),
    OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    protected var map: GoogleMap? = null

    private val myLocationRequestCode = 999

    private var locationRequest: LocationRequest = LocationRequest()
    protected var lastLocation: MutableLiveData<LatLng> = MutableLiveData()
    private var fusedLocationClient: FusedLocationProviderClient? = null

    init {
        locationRequest.interval = 10 * 1000L
        locationRequest.fastestInterval = 10 * 1000L
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    abstract fun locationEnabled()

    abstract fun lastLocationUpdated(latLng: LatLng)

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.let {
            setMapStyle()
            requestLocationPermission()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onPause() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        super.onPause()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map?.isMyLocationEnabled = false
            locationEnabled()
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        } else {
            showDialog(
                PermissionUtils.RationaleDialog.newInstance(
                    myLocationRequestCode, false,
                    R.string.location_request_sms, R.string.location_request_title
                ), "RationaleDialog"
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == myLocationRequestCode) {
            if (permissions.size == 1 &&
                permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                map?.isMyLocationEnabled = false
                locationEnabled()
                fusedLocationClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            } else {
                showDialog(
                    PermissionUtils.PermissionDeniedDialog.newInstance(
                        false,
                        R.string.location_permission_denied,
                        R.string.location_denied
                    ), "PermissionDeniedDialog"
                )
            }
        }
    }

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.let { result ->
                if (result.size > 0) {

                    //The last location in the list is the newest
                    val location = result[result.size - 1]
                    lastLocation.value = LatLng(location.latitude, location.longitude)
                    lastLocation.value?.let {
                        lastLocationUpdated(it)
                    }
                }
            }
        }
    }

    private fun setMapStyle() {
        map?.let {
            val result = it.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!result) {
                Timber.e("Apply style fail")
            }
        }
    }

//    protected fun createMarkerForLocation(location: Location, selectedCategories: List<Category>): Marker? {
//        val markerOptions = MarkerOptions()
//        markerOptions.position(location.getLatLng())
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createBitmapMarkerFromLocation(location, selectedCategories)))
//        return map?.addMarker(markerOptions)
//    }

//    protected fun createMarkerWithBorder(latLng: LatLng, categoryId: Int, colorId: Int): Marker? {
//        val markerOptions = MarkerOptions()
//        markerOptions.position(latLng)
//
//        val bitmap = createBitmapMarkerWithBorder(categoryId, colorId)
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//
//        return map?.addMarker(markerOptions)
//    }

//    private fun createBitmapMarkerFromLocation(location: Location, selectedCategories: List<Category>): Bitmap {
//        val marker = LayoutMarkerBinding.inflate(layoutInflater)
//        marker.viewModel = MarkerViewModel(location, selectedCategories)
//        marker.executePendingBindings()
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        marker.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        marker.root.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
//        marker.root.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
//        marker.root.buildDrawingCache()
//        val bitmap = Bitmap.createBitmap(marker.root.measuredWidth, marker.root.measuredHeight, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        marker.root.draw(canvas)
//
//        return bitmap
//    }

//    private fun createBitmapMarkerWithBorder(categoryId: Int, colorId: Int): Bitmap {
//        var bitmap = decodeResource(resources, categoryId)
//        bitmap = BitmapUtils.getRoundedCornerBitmap(this, bitmap, colorId, 7, 4)
//        return bitmap
//    }

//    protected fun setKeyboardVisibilityListener(onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
//        val parentView = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
//        parentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//
//            private var alreadyOpen: Boolean = false
//            private val defaultKeyboardHeightDP = 100
//            private val EstimatedKeyboardDP = defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
//            private val rect = Rect()
//
//            override fun onGlobalLayout() {
//                val estimatedKeyboardHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP.toFloat(), parentView.resources.displayMetrics).toInt()
//                parentView.getWindowVisibleDisplayFrame(rect)
//                val heightDiff = parentView.rootView.height - (rect.bottom - rect.top)
//                val isShown = heightDiff >= estimatedKeyboardHeight
//
//                if (isShown == alreadyOpen) {
//                    return
//                }
//                alreadyOpen = isShown
//                onKeyboardVisibilityListener.onKeyboardVisibilityChanged(isShown)
//            }
//        })
//    }

//    protected fun animateMarker(marker: Marker, toPosition: LatLng, hideMarker: Boolean) {
//        val handler = Handler()
//        val start = SystemClock.uptimeMillis()
//        val projection = map?.projection
//        projection?.let { pro ->
//            val startPoint = pro.toScreenLocation(marker.position)
//            val startLatLng = pro.fromScreenLocation(startPoint)
//            val duration = 500
//            val interpolator = LinearInterpolator()
//
//            handler.post(object : Runnable {
//                override fun run() {
//                    val elapsed = SystemClock.uptimeMillis() - start
//                    val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
//                    val lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude
//                    val lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude
//                    marker.position = LatLng(lat, lng)
//
//                    if (t < 1.0) {
//                        // Post again 16ms later.
//                        handler.postDelayed(this, 16)
//                    } else {
//                        marker.isVisible = !hideMarker
//                    }
//                }
//            })
//        }
//    }
}
