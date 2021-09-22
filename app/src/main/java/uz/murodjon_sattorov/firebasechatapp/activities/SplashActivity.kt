package uz.murodjon_sattorov.firebasechatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.murodjon_sattorov.firebasechatapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var splashBinding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth

    companion object {
        const val SPLASH_TIME_OUT: Int = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        auth = Firebase.auth

    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        Handler().postDelayed(Runnable {
            if (user != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }
}