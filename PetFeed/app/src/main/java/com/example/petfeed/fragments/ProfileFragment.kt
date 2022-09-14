package com.example.petfeed.fragments

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.petfeed.*
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*


class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        val logoutBTN = view.findViewById<ImageButton>(R.id.logoutBTN)
        val profileEditBtn = view.findViewById<ImageButton>(R.id.profileEditBtn)

        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            profileEditBtn.visibility = View.GONE
            logoutBTN.visibility = View.GONE
        }
        else{
            val email = firebaseUser.email

            profileEditBtn.visibility = View.VISIBLE
            logoutBTN.visibility = View.VISIBLE
        }

        val nameTv = view.findViewById<TextView>(R.id.nameTv)
        val emailTv = view.findViewById<TextView>(R.id.emailTv)
        val memberDateTv = view.findViewById<TextView>(R.id.memberDateTv)
        val profileIv = view.findViewById<ShapeableImageView>(R.id.profileIv)
        val addLoc = view.findViewById<AppCompatButton>(R.id.addLocation)

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"

                    val formattedDate = formatTimeStamp(timestamp.toLong())

                    nameTv.text = name
                    emailTv.text = email
                    memberDateTv.text = formattedDate

                    try {
                        Glide.with(this@ProfileFragment)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person_gray)
                            .into(profileIv)
                    }
                    catch (e: Exception){

                    }

                }
                override fun onCancelled(error: DatabaseError) {
                }
            })


        logoutBTN.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(context, MainActivity::class.java))
        }

        profileEditBtn.setOnClickListener {
            startActivity(Intent(context, ProfileEditActivity::class.java))
        }

        addLoc.setOnClickListener {
            startActivity(Intent(context, AddLocationActivity::class.java))
        }


        val checkVol = view.findViewById<AppCompatButton>(R.id.checkVol)

        checkVol.setOnClickListener {
            startActivity(Intent(context, VolunteerDoneActivity::class.java))
        }



        return view
    }


    fun formatTimeStamp(timestamp: Long): String{
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp
        return DateFormat.format("dd/MM/yyyy",cal).toString()
    }


}