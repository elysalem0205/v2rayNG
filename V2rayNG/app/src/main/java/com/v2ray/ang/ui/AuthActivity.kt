package com.v2ray.ang.ui

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.v2ray.ang.R
import com.v2ray.ang.network.*
import kotlinx.coroutines.*

class AuthActivity : AppCompatActivity() {

    private lateinit var loginTabButton: Button
    private lateinit var registerTabButton: Button
    private lateinit var loginLayout: View
    private lateinit var registerLayout: View

    // حقول تسجيل الدخول
    private lateinit var loginPhone: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPassword: TextView

    // حقول التسجيل
    private lateinit var fullName: EditText
    private lateinit var registerPhone: EditText
    private lateinit var registerPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var birthDate: EditText
    private lateinit var telegram: EditText
    private lateinit var citySpinner: Spinner
    private lateinit var termsCheckBox: CheckBox
    private lateinit var termsLink: TextView
    private lateinit var registerButton: Button

    private val cities = arrayOf(
        "اختر المدينة",
        "نواكشوط",
        "نواذيبو",
        "روصو",
        "كيهيدي",
        "أطار",
        "شنقيط",
        "تجكجة"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // ربط العناصر
        loginTabButton = findViewById(R.id.loginTabButton)
        registerTabButton = findViewById(R.id.registerTabButton)
        loginLayout = findViewById(R.id.loginLayout)
        registerLayout = findViewById(R.id.registerLayout)

        // تسجيل الدخول
        loginPhone = findViewById(R.id.loginPhoneEditText)
        loginPassword = findViewById(R.id.loginPasswordEditText)
        loginButton = findViewById(R.id.loginButton)
        forgotPassword = findViewById(R.id.forgotPasswordTextView)

        // إنشاء الحساب
        fullName = findViewById(R.id.fullNameEditText)
        registerPhone = findViewById(R.id.registerPhoneEditText)
        registerPassword = findViewById(R.id.registerPasswordEditText)
        confirmPassword = findViewById(R.id.confirmPasswordEditText)
        birthDate = findViewById(R.id.birthDateEditText)
        telegram = findViewById(R.id.telegramEditText)
        citySpinner = findViewById(R.id.citySpinner)
        termsCheckBox = findViewById(R.id.termsCheckBox)
        termsLink = findViewById(R.id.termsLinkTextView)
        registerButton = findViewById(R.id.registerButton)

        // إعداد قائمة المدن
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cities)
        citySpinner.adapter = adapter

        // التبويب - تسجيل الدخول
        loginTabButton.setOnClickListener {
            loginLayout.visibility = View.VISIBLE
            registerLayout.visibility = View.GONE
            loginTabButton.backgroundTintList = getColorStateList(R.color.blue_dark)
            registerTabButton.backgroundTintList = getColorStateList(R.color.blue_light)
        }

        // التبويب - إنشاء حساب
        registerTabButton.setOnClickListener {
            loginLayout.visibility = View.GONE
            registerLayout.visibility = View.VISIBLE
            registerTabButton.backgroundTintList = getColorStateList(R.color.blue_dark)
            loginTabButton.backgroundTintList = getColorStateList(R.color.blue_light)
        }

        // نسيت كلمة المرور → رابط خارجي
        forgotPassword.setOnClickListener {
            val url = "https://t.me/your_support"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        // رابط شروط الاستخدام
        termsLink.setOnClickListener {
            val url = "https://your-website.com/terms"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        // زر تسجيل الدخول
        loginButton.setOnClickListener {
            val phone = loginPhone.text.toString().trim()
            val password = loginPassword.text.toString().trim()
            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            performLogin(phone, password)
        }

        // زر إنشاء حساب
        registerButton.setOnClickListener {
            if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "يرجى الموافقة على شروط الاستخدام", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val fName = fullName.text.toString().trim()
            val phone = registerPhone.text.toString().trim()
            val pass = registerPassword.text.toString().trim()
            val passConfirm = confirmPassword.text.toString().trim()
            val bDate = birthDate.text.toString().trim()
            val tg = telegram.text.toString().trim()
            val city = if (citySpinner.selectedItemPosition > 0) cities[citySpinner.selectedItemPosition] else ""

            if (fName.isEmpty() || phone.isEmpty() || pass.isEmpty() || passConfirm.isEmpty() || bDate.isEmpty() || city.isEmpty()) {
                Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass != passConfirm) {
                Toast.makeText(this, "كلمتا المرور غير متطابقتين", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            performRegister(fName, phone, pass, bDate, tg, city)
        }
    }

    private fun performLogin(phone: String, password: String) {
        val progress = ProgressDialog(this).apply {
            setMessage("جاري تسجيل الدخول...")
            setCancelable(false)
            show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.instance.login(LoginRequest(phone, password))
                withContext(Dispatchers.Main) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (token != null) {
                            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            prefs.edit().putString("auth_token", token).apply()
                            Toast.makeText(this@AuthActivity, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                            startMainActivity()
                        } else {
                            Toast.makeText(this@AuthActivity, response.body()?.message ?: "خطأ غير معروف", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        handleError(response.code())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progress.dismiss()
                    Toast.makeText(this@AuthActivity, "خطأ في الاتصال: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun performRegister(
        fullName: String,
        phone: String,
        password: String,
        birthDate: String,
        telegram: String,
        city: String
    ) {
        val progress = ProgressDialog(this).apply {
            setMessage("جاري إنشاء الحساب...")
            setCancelable(false)
            show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = RegisterRequest(
                    phone = phone,
                    password = password,
                    telegram_username = telegram,
                    full_name = fullName,
                    city = city,
                    birth_date = birthDate,
                    device_fingerprint = "android_${System.currentTimeMillis()}"
                )
                val response = ApiClient.instance.register(request)
                withContext(Dispatchers.Main) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        Toast.makeText(this@AuthActivity, response.body()?.message ?: "تم إنشاء الحساب بنجاح", Toast.LENGTH_SHORT).show()
                        // العودة لتبويب تسجيل الدخول
                        loginLayout.visibility = View.VISIBLE
                        registerLayout.visibility = View.GONE
                    } else {
                        when (response.code()) {
                            400 -> Toast.makeText(this@AuthActivity, "رقم الهاتف مسجل مسبقاً", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(this@AuthActivity, "خطأ: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progress.dismiss()
                    Toast.makeText(this@AuthActivity, "خطأ في الاتصال: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleError(code: Int) {
        when (code) {
            403 -> Toast.makeText(this, "حسابك قيد المراجعة أو مرفوض", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "خطأ: $code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}