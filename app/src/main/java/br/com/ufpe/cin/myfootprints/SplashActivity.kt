package br.com.ufpe.cin.myfootprints

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val ctx = this
        Handler().postDelayed({
            val intent = Intent(ctx, LoginActivity::class.java)
            startActivity(intent)
        }, 4000)

    }
}
