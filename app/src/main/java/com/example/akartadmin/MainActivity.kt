package com.example.akartadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.akartadmin.allActivity.Category
import com.example.akartadmin.allActivity.Product
import com.example.akartadmin.allActivity.Slider
import com.example.akartadmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.button1.setOnClickListener {
            startActivity(Intent(this, Category::class.java))

        }

        binding.button2.setOnClickListener {
            startActivity(Intent(this, Product::class.java))

        }
            binding.button3.setOnClickListener {
                startActivity(Intent(this, Slider::class.java))

            }
        }
    }