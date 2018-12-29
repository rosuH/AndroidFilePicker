package me.rosuh.filepicker.config

import android.content.Intent
import me.rosuh.filepicker.App
import me.rosuh.filepicker.FilePickerActivity
import me.rosuh.filepicker.R

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
class FilePickerConfig (private val pickerManager: FilePickerManager) {
    /**
     * 是否显示隐藏文件，默认隐藏
     * 以符号 . 开头的文件或文件夹视为隐藏
     */
    internal var isShowHiddenFiles = false
    /**
     * 是否显示选中框，默认显示
     */
    internal var isShowingCheckBox = true
    /**
     * 在选中时是否忽略文件夹
     */
    internal var isSkipDir = true
    /**
     * 最大可被选中数量
     */
    internal var maxSelectable = Int.MAX_VALUE
    /**
     * 存储类型
     */
    internal var mediaStorageName = App.appResources.getString(R.string.file_picker_tv_sd_card)
    internal var mediaStorageType: StorageMediaTypeEnum = StorageMediaTypeEnum.EXTERNAL_STORAGE
    /**
     * 自定义过滤器
     */
    internal var selfFilter: AbstractFileFilter? = null
    /**
     * 自定文件类型甄别器和默认类型甄别器
     */
    internal var selfFileType: AbstractFileType? = null
    internal val defaultFileType:DefaultFileType by lazy { DefaultFileType() }
    /**
     * 点击操作接口，采用默认实现
     */
    internal var fileItemOnClickListener: FileItemOnClickListener = FileItemOnClickListenerImpl()
    /**
     * 主题
     */
    internal var themeId: Int = R.style.FilePickerThemeRail
    /**
     * 全选文字，取消全选文字，返回文字，已选择文字
     */
    internal var selectAllText: String? = App.appResources.getString(R.string.file_picker_tv_select_all)
    internal var unSelectAllText: String? = App.appResources.getString(R.string.file_picker_tv_unselect_all)
    internal var goBackText: String? = App.appResources.getString(R.string.file_picker_go_back)
    internal var hadSelectedText: String? = App.appResources.getString(R.string.file_picker_selected_count)

    private fun reset():FilePickerConfig{
        isShowHiddenFiles = false
        isShowingCheckBox = true
        isSkipDir = true
        maxSelectable = Int.MAX_VALUE
        mediaStorageName = App.appResources.getString(R.string.file_picker_tv_sd_card)
        mediaStorageType = StorageMediaTypeEnum.EXTERNAL_STORAGE
        selfFilter = null
        selfFileType = null
        fileItemOnClickListener = FileItemOnClickListenerImpl()
        themeId  = R.style.FilePickerThemeRail
        selectAllText = App.appResources.getString(R.string.file_picker_tv_select_all)
        unSelectAllText =  App.appResources.getString(R.string.file_picker_tv_unselect_all)
        goBackText = App.appResources.getString(R.string.file_picker_go_back)
        hadSelectedText = App.appResources.getString(R.string.file_picker_tv_selected)
        return this
    }

    fun showHiddenFiles(isShow: Boolean): FilePickerConfig {
        isShowHiddenFiles = isShow
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

    fun maxSelectable(max:Int):FilePickerConfig{
        maxSelectable = if (max == -1) Int.MAX_VALUE else max
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

    fun fileType(fileType: AbstractFileType): FilePickerConfig {
        selfFileType = fileType
        return this
    }

    fun setItemClickListener(fileItemOnClickListener: FileItemOnClickListener): FilePickerConfig {
        this.fileItemOnClickListener = fileItemOnClickListener
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
}