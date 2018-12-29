package me.rosuh.filepicker

import android.app.Application
import android.content.res.Resources

/**
 *
 * @author rosuh
 * @date 2018/12/29
 */
class App: Application() {

    companion object {
        lateinit var appResources: Resources
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appResources = resources
    }
}