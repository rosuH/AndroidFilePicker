package me.rosuh.filepicker.utils

import android.util.Log
import me.rosuh.filepicker.config.ILog

internal object DefaultLogger : ILog {
    override fun d(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    override fun e(tag: String, msg: String, tr: Throwable?) {
        Log.e(tag, msg, tr)
    }

    override fun i(tag: String, msg: String) {
        Log.i(tag, msg)
    }

    override fun v(tag: String, msg: String) {
        Log.v(tag, msg)
    }

    override fun w(tag: String, msg: String) {
        Log.w(tag, msg)
    }

    override fun wtf(tag: String, msg: String) {
        Log.wtf(tag, msg)
    }
}