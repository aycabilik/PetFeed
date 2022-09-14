package com.example.petfeed

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import com.example.petfeed.databinding.ActivityAddLocationBinding
import com.google.android.gms.tasks.Task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLocationBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private var imageUri: Uri?= null

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.addPhoto.setOnClickListener {
            showImageAttachMenu()
        }

        binding.addLocBtn.setOnClickListener {
            validateData()
        }
    }

    private var streetEt = ""
    private var stateEt = ""
    private var countryEt = ""

    private fun validateData() {
        streetEt = binding.streetEt.text.toString().trim()
        stateEt = binding.stateEt.text.toString().trim()
        countryEt = binding.countryEt.text.toString().trim()

        if(streetEt.isEmpty()){
            Toast.makeText(this,"Please enter street/city name",Toast.LENGTH_LONG).show()
        }
        else if(stateEt.isEmpty()){
            Toast.makeText(this,"Please enter state name",Toast.LENGTH_LONG).show()
        }
        else if(countryEt.isEmpty()){
            Toast.makeText(this,"Please enter country name",Toast.LENGTH_LONG).show()
        }
        else{
            if (imageUri == null){
                //update without image
                updateLocation("")
            }
            else{
                //update with image
                uploadLocationWithImage()
            }
        }
    }

    private fun uploadLocationWithImage() {
        progressDialog.setMessage("Uploading location image")
        progressDialog.show()

        val filePathAndName = "LocationImages/"+firebaseAuth.uid
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot->

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadImageUrl = "${uriTask.result}"

                updateLocation(uploadImageUrl)

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to upload image due to ${e.message}",Toast.LENGTH_LONG).show()

            }
    }

    private fun updateLocation(uploadImageUrl: String) {
        progressDialog.setMessage("Adding location...")

        val timestamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["street"] = streetEt
        hashMap["state"] = stateEt
        hashMap["country"] = countryEt

        if (imageUri != null){
            hashMap["placeImage"] = uploadImageUrl
        }
        else{
            hashMap["placeImage"] = ""
        }
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Locations")
        ref.child(streetEt)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Location is saved",Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to save location due to ${e.message}",Toast.LENGTH_LONG).show()
            }

    }

    private fun showImageAttachMenu() {
        val popupMenu = PopupMenu(this,binding.addPhoto)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item->
            val id = item.itemId

            if (id == 0){
                pickImageCamera()
            }
            else if (id == 1){
                pickImageGallery()
            }
            true
        }
    }

    private fun pickImageCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private fun pickImageGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }


    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->

            if (result.resultCode == RESULT_OK){
                val data = result.data
                //imageUri = data!!.data

                binding.addPhoto.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        }
    )


    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->

            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data

                binding.addPhoto.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        }
    )
}