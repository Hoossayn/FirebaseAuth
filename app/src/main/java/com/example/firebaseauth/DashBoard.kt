package com.example.firebaseauth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.activity_dash_board.profile_pic
import kotlinx.android.synthetic.main.activity_sign_up.*

class DashBoard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var storageReference: StorageReference
    lateinit var storage: FirebaseStorage
    lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        storageReference =  FirebaseStorage.getInstance().getReference("image_upload")

        btn_signOut.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

//       var ss = storageReference.child("image_upload").downloadUrl.toString()

        storageReference = storage.getReference("image_upload")
        storageReference.downloadUrl.addOnSuccessListener {
            var ss = it.toString()

            Glide.with(this@DashBoard).load(ss).into(profile_pic)
        }



    }


}
