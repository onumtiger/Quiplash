package com.example.quiplash

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


/**
 * Class for displaying the start screen when opening the app
 */

class LaunchingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_launching)
        super.onCreate(savedInstanceState)

        val splashanim = AnimationUtils.loadAnimation(this, R.anim.shakeback_splash)
        val interpolator = BounceInterpolator(0.2, 20.0)
        splashanim.interpolator = interpolator
        val splashHeader: ImageView = findViewById(R.id.splashLogo)
        splashHeader.startAnimation(splashanim)

        }


    }