package me.rosuh.filepicker.config

import android.content.Intent
import me.rosuh.filepicker.FilePickerActivity
import me.rosuh.filepicker.R

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
class FilePickerConfig private constructor(private val pickerManager: FilePickerManager) {


    /**
     * 是否显示隐藏文件，默认隐藏
     * 以符号 . 开头的文件或文件夹视为隐藏
     */
    var isShowHidingFiles = false
    /**
     * 是否显示选中框，默认显示
     */
    var isShowingCheckBox = true
    /**
     * 在选中时是否忽略文件夹
     */
    var isSkipDir = true
    /**
     * 存储类型
     */
    var mediaStorageName = "SD 存储卡"
    var mediaStorageType: StorageMediaTypeEnum = StorageMediaTypeEnum.EXTERNAL_STORAGE
    /**
     * 自定义过滤器
     */
    var selfFilter: AbstractFileFilter? = null
    /**
     * 自定文件类型甄别器和默认类型甄别器
     */
    var selfFileType: AbstractFileType? = null
    val defaultFileType:DefaultFileType by lazy { DefaultFileType() }
    /**
     * 点击操作接口
     */
    var fileIFileItemOnClickListener: IFileItemOnClickListener? = null
    /**
     * 主题
     */
    var themeId: Int = R.style.FilePickerThemeRail
    /**
     * 全选文字，取消全选文字，返回文字，已选择文字
     */
    var selectAllText: String? = "图片全选"
    var unSelectAllText: String? = "取消全选"
    var goBackText: String? = "返回"
    var hadSelectedText: String? = "已选择·"
    fun showHiddenFiles(isShow: Boolean): FilePickerConfig {
        isShowHidingFiles = isShow
        return this
    }

    fun showCheckBox(isShow: Boolean): FilePickerConfig {
        isShowingCheckBox = isShow
        return this
    }

    fun skipDirWhenSelect(isSkip: Boolean): FilePickerConfig {
        isSkipDir = isSkip
        return this
    }

    fun storageType(volumeName: String, storageMediaTypeEnum: StorageMediaTypeEnum): FilePickerConfig {
        mediaStorageName = volumeName
        mediaStorageType = storageMediaTypeEnum
        return this
    }

    fun filter(fileFilter: AbstractFileFilter): FilePickerConfig {
        selfFilter = fileFilter
        return this
    }

    fun fileTyper(fileType: AbstractFileType): FilePickerConfig {
        selfFileType = fileType
        return this
    }

    fun setItemClickListener(iFileItemOnClickListener: IFileItemOnClickListener): FilePickerConfig {
        fileIFileItemOnClickListener = iFileItemOnClickListener
        return this
    }

    fun setTheme(themeId: Int): FilePickerConfig {
        this.themeId = themeId
        return this
    }

    fun setText(
        selectAllString: String?,
        unSelectAllString: String?,
        goBackString: String?,
        hadSelectedString: String?
    ): FilePickerConfig {
        selectAllText = selectAllString
        unSelectAllText = unSelectAllString
        goBackText = goBackString
        hadSelectedText = hadSelectedString
        return this
    }

    fun forResult(requestCode: Int) {
        val activity = pickerManager.context?.get()!!
        val fragment = pickerManager.fragment?.get()

        val intent = Intent(activity, FilePickerActivity::class.java)
        if (fragment == null) {
            activity.startActivityForResult(intent, requestCode)
        } else {
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    companion object {
        @Volatile
        private var instance: FilePickerConfig? = null

        fun getInstance(pickerManager: FilePickerManager) =
            instance ?: synchronized(this) {
                instance ?: FilePickerConfig(pickerManager).also { instance = it }
            }
    }
}