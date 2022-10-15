package com.example.fakebook.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.fakebook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(loginActivityToolbar)
        supportActionBar?.title = "Login Page"

        mAuth = FirebaseAuth.getInstance()
    }

    fun loginActivityBtnLoginOnClick(view: View) {
        val email = loginActivityEtEmail.text.toString()
        val password = loginActivityEtPassword.text.toString()
        if (email == "" || password == "") {
            Toast.makeText(this@LoginActivity, "Please enter email and password", Toast.LENGTH_SHORT).show()
        } else {
            val progressDialog = Dialog(this@LoginActivity)
            progressDialog.setContentView(R.layout.dialog_progressbar)
            progressDialog.show()
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseUser = mAuth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
        }
    }

    fun loginActivityBtnRegisterOnClick(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun loginActivityTvForgotPasswordOnClick(view: View) {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        firebaseUser = mAuth.currentUser
        if (firebaseUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

fun showProgressDialog(context: Context) {
    val progressDialog = Dialog(context)
    progressDialog.setContentView(R.layout.dialog_progressbar)
    progressDialog.show()
}
