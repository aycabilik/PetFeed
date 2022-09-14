package com.example.petfeed.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeed.AddLocationActivity
import com.example.petfeed.R
import com.example.petfeed.adapters.AdapterLives
import com.example.petfeed.adapters.AdapterLocation
import com.example.petfeed.models.ModelLives
import com.example.petfeed.models.ModelPlaces
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MapListFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var locationArrayList: ArrayList<ModelPlaces>
    private lateinit var adapterLocation: AdapterLocation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_map_list, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        val backBtn = view.findViewById<ImageButton>(R.id.backBtn)
        val addLocationButton = view.findViewById<FloatingActionButton>(R.id.addLocationButton2)
        val locations = view.findViewById<RecyclerView>(R.id.locations)

        addLocationButton.setOnClickListener {
            startActivity(Intent(context, AddLocationActivity::class.java))
        }

        backBtn.setOnClickListener {
            val mapsFragment = MapsFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.mapListCont, mapsFragment)
            transaction.commit()
        }

        locationArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Locations")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                locationArrayList.clear()
                for (ds in snapshot.children){

                    val model = ds.getValue(ModelPlaces::class.java)
                    locationArrayList.add(model!!)
                }
                adapterLocation = AdapterLocation(context!!,locationArrayList)

                locations.adapter = adapterLocation
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return view
    }





}