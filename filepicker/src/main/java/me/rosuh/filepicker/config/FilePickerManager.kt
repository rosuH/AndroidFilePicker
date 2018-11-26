package me.rosuh.filepicker.config

import me.rosuh.filepicker.R
import me.rosuh.filepicker.config.StorageMediaTypeEnum.EXTERNAL_STORAGE

/**
 *
 * @author rosu
 * @date 2018/11/22
 */
object FilePickerManager {
    /**
     * 启动 Launcher Activity 所需的 request code
     */
    val REQUEST_CODE = 10401
    /**
     * 获取 activity 返回结果时，所需的 intent.extra key
     */
    val RESULT_KEY = "FILE_PICKER_MANAGER"
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
    var mediaStorageType: StorageMediaTypeEnum = EXTERNAL_STORAGE

    var selfFilter: FileFilter?= null

    var themeId:Int = R.style.FilePickerThemeRail
}