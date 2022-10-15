package com.example.fakebook.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.fakebook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUser: DatabaseReference
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(registerActivityToolbar)
        supportActionBar?.title = "Register page"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        registerActivityToolbar.setNavigationOnClickListener {
            finish()
            onBackPressed()
        }

        mAuth = FirebaseAuth.getInstance()
    }

    fun registerActivityBtnConfirmOnClick(view: View) {
        val username = registerActivityEtUsername.text.toString()
        val email = registerActivityEtEmail.text.toString()
        val password = registerActivityEtPassword.text.toString()
        val confirmPassword = registerActivityEtConfirmPassword.text.toString()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Please fill up the fields", Toast.LENGTH_SHORT).show()
        } else if (password != confirmPassword) {
            Toast.makeText(this@RegisterActivity, "Please confirm correct password", Toast.LENGTH_SHORT).show()
        } else {
            showProgressDialog(this@RegisterActivity)
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        firebaseUserID = mAuth.currentUser!!.uid
                        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["username"] = username
                        userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/fakebook-f11ad.appspot.com/o/default_set%2Fcover.jpg?alt=media&token=e57b7368-9799-40e4-856c-bf2a3adab54f"
                        userHashMap["avatar"] = "https://firebasestorage.googleapis.com/v0/b/fakebook-f11ad.appspot.com/o/default_set%2Fuserprofile.jpg?alt=media&token=5b5610f7-8da5-471f-b1dd-16a0934ce678"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.lowercase(Locale.getDefault())
                        userHashMap["facebook"] = "https://m.facebook.com"
                        userHashMap["instagram"] = "https://m.instagram.com"
                        userHashMap["tiktok"] = "https://m.tiktok.com"
                        userHashMap["email"] = email
                        userHashMap["password"] = password
                        userHashMap["phone"] = "0000000000"

                        Toast.makeText(this@RegisterActivity, "Successfully registered", Toast.LENGTH_LONG).show()

                        refUser.updateChildren(userHashMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    finish()
                                } else {
                                    Toast.makeText(this@RegisterActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}