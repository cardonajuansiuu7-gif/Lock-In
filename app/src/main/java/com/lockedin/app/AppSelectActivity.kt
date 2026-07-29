package com.lockedin.app

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class AppInfo(val label: String, val pkg: String, val icon: android.graphics.drawable.Drawable)

class AppSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_select)

        val pm = packageManager
        val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 || pm.getLaunchIntentForPackage(it.packageName) != null }
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null && it.packageName != packageName }
            .map { AppInfo(pm.getApplicationLabel(it).toString(), it.packageName, pm.getApplicationIcon(it)) }
            .sortedBy { it.label.lowercase() }

        val selected = Prefs.getBlockedApps(this).toMutableSet()

        val recycler = findViewById<RecyclerView>(R.id.recyclerApps)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int) =
                object : RecyclerView.ViewHolder(
                    layoutInflater.inflate(R.layout.item_app, parent, false)
                ) {}

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val app = installed[position]
                val cb = holder.itemView.findViewById<CheckBox>(R.id.cbApp)
                val label = holder.itemView.findViewById<TextView>(R.id.tvAppName)
                val icon = holder.itemView.findViewById<ImageView>(R.id.ivAppIcon)

                label.text = app.label
                icon.setImageDrawable(app.icon)
                cb.setOnCheckedChangeListener(null)
                cb.isChecked = selected.contains(app.pkg)
                cb.setOnCheckedChangeListener { _, checked ->
                    if (checked) selected.add(app.pkg) else selected.remove(app.pkg)
                    Prefs.saveBlockedApps(this@AppSelectActivity, selected)
                }
            }

            override fun getItemCount() = installed.size
        }
    }
}
