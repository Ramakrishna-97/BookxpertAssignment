package com.example.assessment_asset_telematics

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assessment_asset_telematics.model.Account
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(){

     lateinit var recyclerViewAccounts:RecyclerView
    private lateinit var allAccounts: ArrayList<Account>
    private lateinit var imageView: ImageView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            recyclerViewAccounts =findViewById(R.id.recyclerViewAccounts)
            val btnOpenPdf: Button = findViewById(R.id.btnOpenPdf)
            val btnCaptureImage: Button = findViewById(R.id.btnCaptureImage)
            val btnSelectImage: Button = findViewById(R.id.btnSelectImage)
            imageView = findViewById(R.id.imageView)

            recyclerViewAccounts.layoutManager = LinearLayoutManager(this)

            allAccounts = ArrayList()
            getData(this@MainActivity)

            val adapter = RecyclerAdapter(allAccounts)

            recyclerViewAccounts.adapter = adapter


            btnOpenPdf.setOnClickListener {
                // startActivity(Intent(this, PDFViewerActivity::class.java))
            }

            btnCaptureImage.setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 101)
            }

            btnSelectImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 102)
            }
        } catch (e: Exception) {
            print(e.message)
        }
    }

    private fun getData(mainActivity: MainActivity) {
        try {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://fssservices.bookxpert.co/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(ApiService::class.java)

            apiService.getAccounts().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            try {
                                val jsonString = responseBody.string() //Convert ResponseBody to String
                                Log.d("RAW_RESPONSE", jsonString)

                                val jsonArrayString = jsonString.trim('"')
                                    .replace("\\\"", "\"") // Fix escaped quotes

                                val gson = Gson()
                                val listType = object : TypeToken<List<Account>>() {}.type
                                val accounts: List<Account> = gson.fromJson(jsonArrayString, listType)


                                accounts.forEach {
                                    Log.d("PARSED_ACCOUNT", "Name: ${it.ActName}, ID: ${it.actid}")
                                   // txtActName.setText("${it.ActName}")
                                    allAccounts.addAll(accounts)
                                }

                            } catch (e: Exception) {
                                Log.e("PARSE_ERROR", "Failed to parse JSON: ${e.message}")
                            }
                        }
                    } else {
                        Log.e("API_ERROR", "Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("API_ERROR", "Network Error: ${t.message}")
                }
            })


         } catch (e: Exception) {
             print(e.message)
         }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                101 -> { // Capture Image
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(imageBitmap)
                }

                102 -> { // Select Image
                    val imageUri: Uri? = data?.data
                    Glide.with(this).load(imageUri).into(imageView)
                }

                1001 -> { // Speech-to-Text
                    val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (!result.isNullOrEmpty()) {
                        val spokenText = result[0]
                        Toast.makeText(this, "Recognized: $spokenText", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
}
}

