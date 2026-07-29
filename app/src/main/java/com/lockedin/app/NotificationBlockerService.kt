package com.lockedin.app

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationBlockerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val pkg = sbn?.packageName ?: return
        if (Prefs.isLocked(this) && Prefs.getBlockedApps(this).contains(pkg)) {
            cancelNotification(sbn.key)
        }
    }
}
