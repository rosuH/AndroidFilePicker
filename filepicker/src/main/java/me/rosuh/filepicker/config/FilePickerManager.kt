package me.rosuh.filepicker.config

import android.app.Activity
import android.support.v4.app.Fragment
import me.rosuh.filepicker.engine.ImageLoadController
import java.lang.ref.WeakReference

/**
 *
 * @author rosu
 * @date 2018/11/22
 */
object FilePickerManager {
    /**
     * 启动 Launcher Activity 所需的 request code
     */
    const val REQUEST_CODE = 10401

    internal var contextRef: WeakReference<Activity>? = null
    internal var fragmentRef: WeakReference<Fragment>? = null
    internal lateinit var config: FilePickerConfig

    fun from(activity: Activity): FilePickerConfig {
        reset()
        this.contextRef = WeakReference(activity)
        config = FilePickerConfig(this)
        return config
    }

    /**
     * 不能使用 fragmentRef.getContext()，因为无法保证外部的代码环境
     */
    fun from(fragment: Fragment): FilePickerConfig {
        reset()
        this.fragmentRef = WeakReference(fragment)
        this.contextRef = WeakReference(fragment.activity!!)
        config = FilePickerConfig(this)
        return config
    }

    private var dataList: List<String> = ArrayList()

    /**
     * 保存数据@param list List<String>到本类中
     */
    internal fun saveData(list: List<String>) {
        dataList = list
    }

    /**
     * 供调用者获取结果
     * @return List<String>
     */
    @JvmOverloads
    fun obtainData(release: Boolean = false): List<String> {
        if (release) {
            release()
        }
        return dataList
    }

    private fun reset() {
        contextRef?.clear()
        fragmentRef?.clear()
        ImageLoadController.reset()
    }

    /**
     * 释放资源与重置属性
     * Release resources and reset attributes
     */
    fun release() {
        reset()
        if (this::config.isInitialized) {
            config.clear()
        }
    }
}