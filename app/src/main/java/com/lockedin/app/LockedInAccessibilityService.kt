package com.lockedin.app

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class LockedInAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        if (pkg == packageName) return // nunca te bloquees a vos mismo

        if (!Prefs.isLocked(this)) return
        if (Prefs.allTasksDone(this)) {
            // ya se completaron todas las tareas: desbloqueamos automáticamente
            Prefs.setLocked(this, false)
            return
        }

        val blocked = Prefs.getBlockedApps(this)
        if (blocked.contains(pkg)) {
            // Saca al usuario a la pantalla de bloqueo
            val intent = Intent(this, BlockedActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun onInterrupt() {}
}
