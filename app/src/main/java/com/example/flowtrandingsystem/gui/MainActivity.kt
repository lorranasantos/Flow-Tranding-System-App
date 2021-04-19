package com.example.flowtrandingsystem.gui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.flowtrandingsystem.R
import kotlinx.android.synthetic.main.employer_register_activity.*
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)


        btn_login_screen.setOnClickListener {

            goToLogin()
        }

    }

    private fun goToLogin(){

        val loginScreen = Intent(this, LoginActivity::class.java)
        startActivity(loginScreen)
    }

}
