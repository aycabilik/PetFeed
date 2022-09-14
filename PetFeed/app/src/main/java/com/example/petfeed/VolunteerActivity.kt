package com.example.petfeed

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.petfeed.databinding.ActivityVolunteerBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*

class VolunteerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVolunteerBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)


        val locationId: String = intent.getStringExtra("locationId").toString()
        intent.removeExtra("locationId")

        firebaseAuth = FirebaseAuth.getInstance()
        locationGet(locationId)

        binding.closeVolunteer.setOnClickListener {
            intent.removeExtra("locationId")
            onBackPressed()
            finish()
        }

        binding.volunteerBtn.setOnClickListener {
            Toast.makeText(this,"Your volunteering has been saved. Thank you!", Toast.LENGTH_LONG).show()
            onBackPressed()
        }

        val cal =  Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)

        binding.datePicker.setOnClickListener{
            val datePickerDialog = DatePickerDialog(this@VolunteerActivity, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                binding.date.setText(""+ mDay + "/" + mMonth+ "/" + mYear)
            }, year, month, day )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }

    private fun locationGet(locationId: String) {
        val refName = FirebaseDatabase.getInstance().getReference("Locations")
        refName.child(locationId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val street = "${snapshot.child("street").value}"
                    val state = "${snapshot.child("state").value}"
                    val country = "${snapshot.child("country").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val placeImage = "${snapshot.child("placeImage").value}"

                    try {
                        Glide.with(this@VolunteerActivity)
                            .load(placeImage)
                            .placeholder(R.drawable.ic_add_location_white)
                            .into(binding.locationPhotoRow)
                    }
                    catch (e: Exception){

                    }
                    binding.streetTv.text = street
                    binding.stateText.text = state
                    binding.countryText.text = country
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


}