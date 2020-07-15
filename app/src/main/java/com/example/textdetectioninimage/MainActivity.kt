package com.example.textdetectioninimage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import android.graphics.Bitmap
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText

class MainActivity : AppCompatActivity() {

    var imageView:ImageView? = null
    var textView:TextView? = null
    var uploadButtom: Button? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        imageView = findViewById(R.id.imageView)
        uploadButtom = findViewById(R.id.uploadButtom)
        textView = findViewById(R.id.textView)
        uploadButtom!!.setOnClickListener{
            checkPermission()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            val permissions = arrayOf(Manifest.permission.CAMERA)
            this.requestPermissions(permissions, 1)
        }else{
            getImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode === Activity.RESULT_OK) {

            if (requestCode === 2) {

                val photo = data!!.getExtras()!!.get("data") as Bitmap
                imageView!!.setImageBitmap(photo)
                imageView!!.visibility = View.VISIBLE
                uploadButtom!!.visibility = View.GONE
                var image = FirebaseVisionImage.fromBitmap(photo)
                val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

                val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
                    .setLanguageHints(listOf("en", "hi"))
                    .build()

                val result = detector.processImage(image)
                    .addOnSuccessListener { firebaseVisionText ->
                        extractText(firebaseVisionText)
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        // ...
                    }
            }
        }

    }


    fun getImage(){
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 2)
    }


    fun extractText(result:FirebaseVisionText){

        val resultText = result.text
        var string:String = ""
        for (block in result.textBlocks) {
            val blockText = block.text
            string += blockText+" \n"
        }

        textView!!.setText(resultText)
        textView!!.visibility = View.VISIBLE

    }

}
