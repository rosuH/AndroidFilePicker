package me.rosuh.filepicker.utils

import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 *
 * @author rosuh
 * @date 2019/1/7
 */
abstract class BaseActivity:AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}