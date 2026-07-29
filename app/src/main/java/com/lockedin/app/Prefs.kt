package com.lockedin.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Task(var text: String, var done: Boolean = false)

object Prefs {
    private const val FILE = "locked_in_prefs"
    private const val KEY_TASKS = "tasks"
    private const val KEY_BLOCKED_APPS = "blocked_apps"
    private const val KEY_LOCKED = "is_locked"

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun getTasks(ctx: Context): MutableList<Task> {
        val raw = prefs(ctx).getString(KEY_TASKS, "[]") ?: "[]"
        val arr = JSONArray(raw)
        val list = mutableListOf<Task>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(Task(o.getString("text"), o.getBoolean("done")))
        }
        return list
    }

    fun saveTasks(ctx: Context, tasks: List<Task>) {
        val arr = JSONArray()
        tasks.forEach {
            val o = JSONObject()
            o.put("text", it.text)
            o.put("done", it.done)
            arr.put(o)
        }
        prefs(ctx).edit().putString(KEY_TASKS, arr.toString()).apply()
    }

    fun getBlockedApps(ctx: Context): MutableSet<String> {
        return prefs(ctx).getStringSet(KEY_BLOCKED_APPS, emptySet())?.toMutableSet()
            ?: mutableSetOf()
    }

    fun saveBlockedApps(ctx: Context, packages: Set<String>) {
        prefs(ctx).edit().putStringSet(KEY_BLOCKED_APPS, packages).apply()
    }

    fun isLocked(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_LOCKED, false)

    fun setLocked(ctx: Context, locked: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_LOCKED, locked).apply()
    }

    fun allTasksDone(ctx: Context): Boolean {
        val tasks = getTasks(ctx)
        return tasks.isNotEmpty() && tasks.all { it.done }
    }
}
