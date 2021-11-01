package com.example.samah.victo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.samah.victo.Product.ProductActivity
import com.example.samah.victo.Raw.RawActivity

class OptionActivity : AppCompatActivity() {
    var clicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)
    }

    override fun onResume() {
        super.onResume()
        clicked = false
    }

    fun OnClick(view: View) {
        if (clicked) return
        clicked = true
        //Flash, Pulse, RubberBand, Shake, Swing, Wobble, Bounce, Tada, StandUp, Wave
        YoYo.with(Techniques.Pulse).duration(150).playOn(view)
        var aClass: Class<*>? = null
        if (view == findViewById(R.id.calButton)) aClass = CalculatorActivity::class.java
        else if (view == findViewById(R.id.proButton)) aClass = ProductActivity::class.java
        else if (view == findViewById(R.id.rawButton)) aClass = RawActivity::class.java
        val finalAClass = aClass
        Handler().postDelayed({
            val intent = Intent(this, finalAClass)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }, 150)
    }
}