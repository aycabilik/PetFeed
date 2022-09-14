package com.example.petfeed.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeed.AddLiveActivity
import com.example.petfeed.models.ModelLives
import com.example.petfeed.R
import com.example.petfeed.adapters.AdapterLives
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LiveFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var livesArrayList: ArrayList<ModelLives>
    private lateinit var adapterLives: AdapterLives


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_live, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        val addLiveButton = view.findViewById<FloatingActionButton>(R.id.addLiveButton)
        val lives = view.findViewById<RecyclerView>(R.id.lives)


        val mLinearLayoutManager = LinearLayoutManager(context)
        lives.setLayoutManager(mLinearLayoutManager)
        mLinearLayoutManager.stackFromEnd = true
        mLinearLayoutManager.reverseLayout = true

        addLiveButton.setOnClickListener {
            startActivity(Intent(context, AddLiveActivity::class.java))
        }


        livesArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Lives")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                livesArrayList.clear()
                for (ds in snapshot.children){

                    val model = ds.getValue(ModelLives::class.java)
                    livesArrayList.add(model!!)
                }
                adapterLives = AdapterLives(context!!,livesArrayList)

                lives.adapter = adapterLives

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return view
    }


}