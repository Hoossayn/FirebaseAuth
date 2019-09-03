package com.example.firebaseauth

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btn_signUp
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val PERMISSION_REQUEST_CODE = 1001
    val PICK_IMAGE_REQUEST = 900
    //lateinit var filePath
     var uri: String = ""
    lateinit var storageReference: StorageReference

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth

        auth = FirebaseAuth.getInstance()
        storageReference =  FirebaseStorage.getInstance().getReference("image_upload")

        btn_signUp.setOnClickListener{
            signUpUser()
        }

        profile_pic.setOnClickListener{
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(this@SignUp, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                    }else{
                        chooseFile()
                    }
                }

                else -> chooseFile()
            }


        }
    }

    private fun signUpUser() {
        if (tv_username.text.toString().isEmpty()) {
            tv_username.error = "Please enter email"
            tv_username.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tv_username.text.toString()).matches()) {
            tv_username.error = "Please enter valid email"
            tv_username.requestFocus()
            return
        }

        if (tv_password.text.toString().isEmpty()) {
            tv_password.error = "Please enter password"
            tv_password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val intent = Intent(this, DashBoard::class.java)
                                intent.putExtra("profilePic", uri)
                                startActivity(intent)
                                finish()
                            }
                        }


                } else {
                    Toast.makeText(baseContext, "Sign Up failed. Try again after some time.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun chooseFile() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            PERMISSION_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this@SignUp, "Oops! Permission Denied!!",Toast.LENGTH_SHORT).show()
                else
                    chooseFile()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode){
            PICK_IMAGE_REQUEST -> {
                //filePath = data!!.getData()!!

                val uploadTask = storageReference!!.putFile(data!!.data!!)
                val task = uploadTask.continueWithTask{
                    task -> if(!task.isSuccessful){
                    Toast.makeText(this, "Failed",Toast.LENGTH_SHORT).show()
                }
                    storageReference!!.downloadUrl


                }.addOnCompleteListener{
                    task -> if (task.isSuccessful){
                    val downloadUri = task.result
                     uri = downloadUri.toString()
                    Glide.with(this@SignUp).load(uri).into(profile_pic)
                }
                }
               // uploadFile()

            }
        }
    }

    private fun uploadFile() {
        val progress = ProgressDialog(this).apply {
            setTitle("Loading....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }

       /* val data = FirebaseStorage.getInstance()
        var value = 0.0
        var storage = data.getReference().child("mypic.jpg").putFile(filePath)
            .addOnProgressListener { taskSnapshot ->
                value = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount

                progress.setMessage("Uploaded.. " + value.toInt() + "%")
            }
            .addOnSuccessListener { taskSnapshot -> progress.dismiss()
                val uri = taskSnapshot.uploadSessionUri  //if anything goes wrong na here


                Glide.with(this@SignUp).load(uri).into(profile_pic)
            }
            .addOnFailureListener{
                    exception -> exception.printStackTrace()
            }*/

    }
}
