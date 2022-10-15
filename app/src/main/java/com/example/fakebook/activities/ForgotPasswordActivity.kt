package com.example.fakebook.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fakebook.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setSupportActionBar(forgotPasswordActivityToolbar)
        supportActionBar?.title = "Find password page"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        forgotPasswordActivityToolbar.setNavigationOnClickListener {
            finish()
            onBackPressed()
        }
    }

}