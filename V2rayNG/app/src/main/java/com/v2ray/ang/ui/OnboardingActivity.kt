package com.v2ray.ang.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.v2ray.ang.R

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // إذا لم تكن أول مرة، انتقل مباشرة للرئيسية
        if (!sharedPref.getBoolean("first_time", true)) {
            startMainActivity()
            return
        }

        // ربط العناصر
        val whatIsButton = findViewById<Button>(R.id.whatIsButton)
        val whatIsContent = findViewById<TextView>(R.id.whatIsContent)

        val trustButton = findViewById<Button>(R.id.trustButton)
        val trustContent = findViewById<TextView>(R.id.trustContent)

        val securityButton = findViewById<Button>(R.id.securityButton)
        val securityContent = findViewById<TextView>(R.id.securityContent)

        val getStartedButton = findViewById<Button>(R.id.getStartedButton)

        // منطق زر "ما هو كونكتيلي"
        whatIsButton.setOnClickListener {
            whatIsContent.visibility = if (whatIsContent.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // منطق زر "المصداقية"
        trustButton.setOnClickListener {
            trustContent.visibility = if (trustContent.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // منطق زر "الأمان"
        securityButton.setOnClickListener {
            securityContent.visibility = if (securityContent.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // زر البدء
        getStartedButton.setOnClickListener {
            sharedPref.edit().putBoolean("first_time", false).apply()
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}