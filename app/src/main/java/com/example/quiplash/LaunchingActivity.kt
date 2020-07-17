package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


/**
 * Class for displaying the splash-screen when starting the app
 */

class LaunchingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_launching)

        val splashanim = AnimationUtils.loadAnimation(this, R.anim.shakeback_splash)
        val interpolator = BounceInterpolator(0.2, 20.0)
        splashanim.interpolator = interpolator
        val splashHeader: ImageView = findViewById(R.id.splashLogo)
        splashHeader.startAnimation(splashanim)

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000L)

    }
}

