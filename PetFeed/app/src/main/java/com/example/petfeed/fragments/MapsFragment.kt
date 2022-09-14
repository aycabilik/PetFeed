package com.example.petfeed.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction

import com.example.petfeed.AddLocationActivity
import com.example.petfeed.ProfileEditActivity
import com.example.petfeed.R
import com.example.petfeed.VolunteerActivity
import com.example.petfeed.adapters.AdapterLocation
import com.example.petfeed.models.ModelPlaces


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapsFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var locationArrayList: ArrayList<ModelPlaces>
    private lateinit var adapterLocation: AdapterLocation
    private lateinit var intent: Intent

    private val callback = OnMapReadyCallback { googleMap ->

        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        getDataFromDatabase(googleMap)


        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@OnMapReadyCallback
        }

        googleMap.isMyLocationEnabled

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_maps, container, false)

        firebaseAuth = FirebaseAuth.getInstance()


        val addLocationButton = view.findViewById<FloatingActionButton>(R.id.addLocationButton)
        val listLocationBtn = view.findViewById<ImageButton>(R.id.listLocationBtn)

        addLocationButton.setOnClickListener {
            startActivity(Intent(context, AddLocationActivity::class.java))
        }

        listLocationBtn.setOnClickListener {
            val mapListFragment = MapListFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.mapCont, mapListFragment)
            transaction.commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    fun getDataFromDatabase(googleMap: GoogleMap){
        locationArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Locations")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                locationArrayList.clear()
                for (ds in snapshot.children){

                    val model = ds.getValue(ModelPlaces::class.java)
                    locationArrayList.add(model!!)

                    googleMap.uiSettings.isZoomControlsEnabled = true
                    var address: LatLng? = null
                    for(i in locationArrayList){
                        try {
                            var strAdd : String = i.street+ ","+ i.state + "," + i.country
                            address = getLatLongFromAddress(requireContext(), strAdd)

                            val markerOptions = MarkerOptions().position(address!!).title(i.street)
                            googleMap.addMarker(markerOptions)!!.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.paw))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 12.0f))


                            googleMap.setOnInfoWindowClickListener {
                                intent =  Intent(context, VolunteerActivity::class.java)
                                intent.putExtra("locationId",i.street)
                                startActivity(intent)

                            }


                        }catch (e: Exception){

                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getLatLongFromAddress(context: Context, straddress: String): LatLng? {
        val geocoder = Geocoder(context)
        val address: List<Address>
        var latLng: LatLng? = null
        try {
            address = geocoder.getFromLocationName(straddress,2)
            if(address == null){
                return null
            }
            val loc : Address = address.get(0)
            latLng = LatLng(loc.latitude, loc.longitude)

        }catch (e: Exception){

        }
        return latLng
    }

}