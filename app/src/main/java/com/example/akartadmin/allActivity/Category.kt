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
import com.example.akartadmin.adapter.CategoryAdapter
import com.example.akartadmin.databinding.ActivityCategoryBinding
import com.example.akartadmin.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class Category : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding

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
        binding=ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dialog=Dialog(this)
        dialog.setContentView(R.layout.loading_layout)
        dialog.setCancelable(false)

        getData()

        binding.apply {
            imageView.setOnClickListener {
                val intent= Intent("android.intent.action.GET_CONTENT")
                intent.type= "image/*"
                launchGalleryActivity.launch(intent)
            }

            uploadCategoryBtn.setOnClickListener {
               ValiDT_Data(binding.categoryName.text.toString())
            }
        }
    }

    private fun getData() {
        val list =ArrayList<CategoryModel>()
        Firebase.firestore.collection("category")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents){
                    val data=doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                }

                binding.CategoryRecycler.adapter=CategoryAdapter(this,list)
            }
    }



    private fun ValiDT_Data(categoryName: String) {

        if (categoryName.isEmpty()){
            Toast.makeText(this, "please provide category name", Toast.LENGTH_SHORT).show()
        }
        else if(imageUri==null){
            Toast.makeText(this, "please select image", Toast.LENGTH_SHORT).show()
        }
        else{
            uploadImage(categoryName)
        }
    }

    private fun uploadImage(categoryName: String) {
        dialog.show()

        val filename= UUID.randomUUID().toString()+".jpg"
        val data_Store= FirebaseStorage.getInstance().reference.child("category/$filename")
        data_Store.putFile(imageUri!!)

            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    storeMyData(categoryName, image.toString())
                }
            }

            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "something went wrong with your storage!!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeMyData(categoryName: String, url: String) {

        val db= Firebase.firestore

        val data= hashMapOf<String, Any>(
            "cate" to categoryName,
            "img"  to url
        )

        db.collection("category").add(data)
            .addOnSuccessListener {
                dialog.dismiss()
                binding.imageView.setImageURI(null)
                binding.categoryName.text=null

                getData()

                Toast.makeText(this, "Category uploaded successfully", Toast.LENGTH_SHORT).show()
            }

            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }


}