package com.gc.myapplication.ui.detail

import android.location.Location
import android.os.Bundle
import androidx.core.os.bundleOf
import com.gc.myapplication.R
import com.gc.myapplication.base.BaseMapActivity
import com.gc.myapplication.databinding.ActivityUserDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * [UserDetailActivity] - show the detail of a location
 * @author n.quoi
 * @date 10.19.2021
 */

class UserDetailActivity : BaseMapActivity<UserDetailViewModel, ActivityUserDetailBinding>() {
    override val viewModel by viewModel<UserDetailViewModel>()

    override val layoutId: Int = R.layout.activity_user_detail

    var location: Location? = null

    companion object {
        private const val LOCATION_EXTRA = "location_extra"
        fun createBundleExtra(location: Location): Bundle {
            return bundleOf(LOCATION_EXTRA to location)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        super.onMapReady(googleMap)
        location?.let {
            val locationLatLng = LatLng(it.latitude, it.longitude)
            //createMarkerWithBorder(locationLatLng, it.getCategoryResourceId(), Color.WHITE)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 16f))
        }
    }

    override fun locationEnabled() {}
    override fun lastLocationUpdated(latLng: LatLng) {}

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
    }
}
