package com.example.akartadmin.allActivity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.akartadmin.R
import com.example.akartadmin.databinding.ActivitySliderBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class Slider : AppCompatActivity() {

    private lateinit var binding:ActivitySliderBinding
    private var imageUri: Uri? =null
    private lateinit var dialog: Dialog

    private var launchGalleryActivity=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode== Activity.RESULT_OK)
        {
            imageUri = it.data!!.data
            binding.imageView.setImageURI(imageUri)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySliderBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dialog=Dialog(this)
        dialog.setContentView(R.layout.loading_layout)
        dialog.setCancelable(false)

        binding.apply {

            imageView.setOnClickListener {
                val intent= Intent("android.intent.action.GET_CONTENT")
                intent.type= "image/*"
                launchGalleryActivity.launch(intent)
            }
            btnUpload.setOnClickListener {
                if (imageUri != null){
                    upload_image(imageUri!!)
                }else{
                    Toast.makeText(applicationContext, "please select image", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun upload_image(imageUri: Uri) {

        dialog.show()

        val filename= UUID.randomUUID().toString()+".jpg"
        val data_Store= FirebaseStorage.getInstance().reference.child("slider/$filename")
        data_Store.putFile(imageUri)

            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    storeMyData(image.toString())
                }
            }

            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "something went wrong with your storage!!", Toast.LENGTH_SHORT).show()
            }

    }

    private fun storeMyData(image: String) {

        val db= Firebase.firestore

        val data= hashMapOf<String, Any>(
            "img" to image
        )

        db.collection("slider").document("item").set(data)
            .addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(this, "Slider uploaded successfully", Toast.LENGTH_SHORT).show()
            }

            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }


}