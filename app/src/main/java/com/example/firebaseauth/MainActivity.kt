package com.example.firebaseauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btn_signUp
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.profile_pic

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        btn_signUp.setOnClickListener{
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }

        btn_login.setOnClickListener{
            doLogin()
        }
    }

    private fun doLogin() {
        if (et_email.text.toString().isEmpty()) {
            et_email.error = "Please enter email"
            et_email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(et_email.text.toString()).matches()) {
            et_email.error = "Please enter valid email"
            et_email.requestFocus()
            return
        }

        if (et_password.text.toString().isEmpty()) {
            et_password.error = "Please enter password"
            et_password.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = auth.currentUser
                    updateUI(user)

                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(baseContext, "Authentication failed trying to login",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }//EmailPasswordActivity.kt
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
       /* var pic_url = SignUp().uri
        Glide.with(this@MainActivity).load(pic_url).into(profile_pic)*/
    }//EmailPasswordActivity.kt


    fun updateUI(currentUser: FirebaseUser?) {

        if(currentUser != null){

           /* var pic_url = SignUp().uri
            Glide.with(this@MainActivity).load(pic_url).into(profile_pic)*/

            if(currentUser.isEmailVerified){

                startActivity(Intent(this,DashBoard::class.java))
                finish()
            }else{
                Toast.makeText(this, "Please verify your email address", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }
}
