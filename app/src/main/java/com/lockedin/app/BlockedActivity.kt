package com.lockedin.app

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BlockedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked)

        // Candado con brillo pulsante
        val glow = findViewById<android.view.View>(R.id.glow)
        val pulse = ObjectAnimator.ofFloat(glow, "alpha", 0.4f, 1f)
        pulse.duration = 900
        pulse.repeatMode = ValueAnimator.REVERSE
        pulse.repeatCount = ValueAnimator.INFINITE
        pulse.start()

        findViewById<android.widget.Button>(R.id.btnGoToTasks).setOnClickListener {
            val i = android.content.Intent(this, MainActivity::class.java)
            i.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    override fun onBackPressed() {
        // No dejamos salir de esta pantalla con "atrás" hacia la app bloqueada.
        moveTaskToBack(true)
    }
}
