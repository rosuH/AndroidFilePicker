package me.rosuh.filepicker.config

import android.app.Activity
import android.support.v4.app.Fragment
import me.rosuh.filepicker.R
import me.rosuh.filepicker.config.StorageMediaTypeEnum.EXTERNAL_STORAGE
import java.lang.ref.WeakReference

/**
 *
 * @author rosu
 * @date 2018/11/22
 */
class FilePickerManager private constructor(){

    companion object {
        val instance: FilePickerManager by lazy { FilePickerManager() }
    }

    /**
     * 启动 Launcher Activity 所需的 request code
     */
    val REQUEST_CODE = 10401
    /**
     * 获取 context 返回结果时，所需的 intent.extra key
     */
    val RESULT_KEY = "FILE_PICKER_MANAGER"

    var context:WeakReference<Activity> ?= null
    var fragment:WeakReference<Fragment> ?= null

    fun from(activity: Activity):FilePickerConfig{
        this.context = WeakReference(activity)
        return FilePickerConfig.getInstance(this)
    }

    /**
     * 不能使用 fragment.getContext()，因为无法保证外部的代码环境
     */
    fun from(fragment: Fragment):FilePickerConfig{
        this.fragment = WeakReference(fragment)
        this.context = WeakReference(fragment.activity!!)
        return FilePickerConfig.getInstance(this)
    }

    private var dataList: List<String> = ArrayList()

    /**
     * 保存数据@param list List<String>到本类中
     */
    fun saveData(list: List<String>) {
        dataList = list
    }

    /**
     * 供调用者获取结果
     * @return List<String>
     */
    fun obtainData(): List<String> {
        return dataList
    }
}