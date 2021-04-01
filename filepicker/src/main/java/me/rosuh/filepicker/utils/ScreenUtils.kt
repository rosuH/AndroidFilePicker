package me.rosuh.filepicker.utils

import android.content.res.Resources


val Int.dp: Int
    get() = (Resources.getSystem().displayMetrics.densityDpi * this + 0.5f).toInt()
