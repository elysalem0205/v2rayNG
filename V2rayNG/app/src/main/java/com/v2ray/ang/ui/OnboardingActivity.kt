package com.v2ray.ang.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.v2ray.ang.R

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // هل هذه أول مرة؟
        if (sharedPref.getBoolean("first_time", true)) {
            // نعم، أول مرة. نعرض الواجهة
            findViewById<Button>(R.id.getStartedButton).setOnClickListener {
                // نحفظ أن المستخدم شاهد الترحيب
                sharedPref.edit().putBoolean("first_time", false).apply()
                // ننتقل للصفحة الرئيسية
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        } else {
            // ليس أول مرة، انتقل مباشرة للصفحة الرئيسية
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}