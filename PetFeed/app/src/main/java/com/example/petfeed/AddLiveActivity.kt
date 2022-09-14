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
import com.bumptech.glide.Glide
import com.example.petfeed.databinding.ActivityAddLiveBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception


class AddLiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLiveBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private var imageUri: Uri?= null

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        nameGet()

        binding.closeLivePost.setOnClickListener {
            onBackPressed()
        }

        binding.addPhoto.setOnClickListener {
            showImageAttachMenu()
        }

        binding.postTxt.setOnClickListener {
            validateData()
        }
    }

    private fun nameGet() {
        val refName = FirebaseDatabase.getInstance().getReference("Users")
        refName.child(firebaseAuth.uid!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = "${snapshot.child("name").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"

                    binding.nameTv.text = name
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    private var text = ""

    private fun validateData() {
        text = binding.liveText.text.toString().trim()

        if(text.isEmpty()){
            Toast.makeText(this,"Please enter live information",Toast.LENGTH_LONG).show()
        }
        else{
            if (imageUri == null){
                //update without image
                updateLive("")
            }
            else{
                //update with image
                uploadLiveWithImage()
            }
        }
    }

    private fun uploadLiveWithImage() {
        progressDialog.setMessage("Uploading live image")
        progressDialog.show()

        val filePathAndName = "LiveImages/"+firebaseAuth.uid
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot->

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadImageUrl = "${uriTask.result}"

                updateLive(uploadImageUrl)

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to upload image due to ${e.message}",Toast.LENGTH_LONG).show()

            }
    }

    private fun updateLive(uploadImageUrl: String) {
        progressDialog.setMessage("Sharing live...")

        val timestamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid


        val refName = FirebaseDatabase.getInstance().getReference("Users")

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["text"] = text
        hashMap["name"] = binding.nameTv.text.toString()
        if (imageUri != null){
            hashMap["liveImage"] = uploadImageUrl
        }
        else{
            hashMap["liveImage"] = ""
        }
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Lives")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Live is shared",Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to share live due to ${e.message}",Toast.LENGTH_LONG).show()
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