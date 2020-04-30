package me.rosuh.filepicker.config

import android.content.Intent
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import me.rosuh.filepicker.FilePickerActivity
import me.rosuh.filepicker.R

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
class FilePickerConfig(private val pickerManager: FilePickerManager) {

    private val contextRes = pickerManager.contextRef!!.get()!!.resources

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
     * 是否是单选
     * 如果是单选，则隐藏顶部【全选/取消全选按钮】
     */
    internal var singleChoice = false
    /**
     * 最大可被选中数量
     */
    internal var maxSelectable = Int.MAX_VALUE
    /**
     * 存储类型
     */
    internal var mediaStorageName = contextRes.getString(R.string.file_picker_tv_sd_card)

    /**
     * 自定义存储类型，根据此返回根目录
     */
    @get:StorageMediaType
    @set:StorageMediaType
    internal var mediaStorageType: String = STORAGE_EXTERNAL_STORAGE
    /**
     * 自定义根目录路径，需要先设置 [mediaStorageType] 为 [STORAGE_CUSTOM_ROOT_PATH]
     */
    internal var customRootPath: String = ""
    /**
     * 自定义过滤器
     */
    internal var selfFilter: AbstractFileFilter? = null
    /**
     * 自定文件类型甄别器和默认类型甄别器
     */
    internal var selfFileType: AbstractFileType? = null
    internal val defaultFileType: DefaultFileType by lazy { DefaultFileType() }
    /**
     * 点击操作接口，采用默认实现
     */
    internal var fileItemOnClickListener: FileItemOnClickListener? = null
    /**
     * 主题
     */
    internal var themeId: Int = R.style.FilePickerThemeRail
    /**
     * 全选文字，取消全选文字，返回文字，已选择文字，确认按钮，选择限制提示语，空列表提示
     */
    internal var selectAllText: String = contextRes.getString(R.string.file_picker_tv_select_all)

    internal var deSelectAllText: String =
        contextRes.getString(R.string.file_picker_tv_deselect_all)
    @StringRes
    internal var hadSelectedText: Int = R.string.file_picker_selected_count
    internal var confirmText: String = contextRes.getString(R.string.file_picker_tv_select_done)
    @StringRes
    internal var maxSelectCountTips: Int = R.string.max_select_count_tips
    internal var emptyListTips: String = contextRes.getString(R.string.empty_list_tips_file_picker)

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

    fun maxSelectable(max: Int): FilePickerConfig {
        maxSelectable = if (max < 0) Int.MAX_VALUE else max
        return this
    }

    @JvmOverloads
    fun storageType(volumeName: String = "", @StorageMediaType storageMediaType: String): FilePickerConfig {
        mediaStorageName = volumeName
        mediaStorageType = storageMediaType
        return this
    }

    fun setCustomRootPath(path: String): FilePickerConfig {
        customRootPath = path
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

    /**
     * 是否启用单选模式
     */
    fun enableSingleChoice(): FilePickerConfig {
        this.singleChoice = true
        return this
    }

    /**
     * 设置界面的字符串，包括：
     * 选中全部[selectAllString]
     * 取消选中[unSelectAllString]
     * 已选择[hadSelectedStrRes]
     * 确认[confirmText]
     * 多选限制提示：“您只能选择 1 个条目”[maxSelectCountTipsStrRes]
     * 空试图体视：“空空如也”[emptyListTips]
     * 注意：
     * [hadSelectedStrRes] 和 [maxSelectCountTipsStrRes] 是 String format 限制的字符串，你需要传入 [R.string.file_picker_selected_count] 类似的
     * 中的 id，并且包含一个可传入 Int 类型参数的占位符
     *----------------------------------------------------------------------------------------------
     * Set the string of the interface, including:
     * Select all [selectAllString]
     * Uncheck [unSelectAllString]
     * Selected [hadSelectedStrRes]
     * Confirm [confirmText]
     * Multiple selection limit prompt: "You can only select 1 item" [maxSelectCountTipsStrRes]
     * Empty tries to look at the stereo: "empty as well" [emptyListTips]
     * Note:
     * [hadSelectedStrRes] and [maxSelectCountTipsStrRes] are strings of String format restrictions, you need to pass some string like [R.string.file_picker_selected_count]
     * The id in * and contains a placeholder for passing in an Int type parameter
     */
    fun setText(
        @NonNull selectAllString: String = contextRes.getString(R.string.file_picker_tv_select_all),
        @NonNull unSelectAllString: String = contextRes.getString(R.string.file_picker_tv_deselect_all),
        @NonNull @StringRes hadSelectedStrRes: Int = R.string.file_picker_selected_count,
        @NonNull confirmText: String = contextRes.getString(R.string.file_picker_tv_select_done),
        @NonNull @StringRes maxSelectCountTipsStrRes: Int = R.string.max_select_count_tips,
        @NonNull emptyListTips: String = contextRes.getString(R.string.empty_list_tips_file_picker)
    ): FilePickerConfig {
        this.selectAllText = selectAllString
        this.deSelectAllText = unSelectAllString
        this.hadSelectedText = hadSelectedStrRes
        this.confirmText = confirmText
        this.maxSelectCountTips = maxSelectCountTipsStrRes
        this.emptyListTips = emptyListTips
        return this
    }

    fun forResult(requestCode: Int) {
        val activity = pickerManager.contextRef?.get()!!
        val fragment = pickerManager.fragmentRef?.get()

        val intent = Intent(activity, FilePickerActivity::class.java)
        if (fragment == null) {
            activity.startActivityForResult(intent, requestCode)
        } else {
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    companion object {
        /**
         * 手机内部的外置存储，也就是内置 SD 卡
         */
        @get:StorageMediaType
        const val STORAGE_EXTERNAL_STORAGE = "STORAGE_EXTERNAL_STORAGE"
        /**
         * TODO 可拔插的 SD 卡
         */
        @get:StorageMediaType
        const val STORAGE_UUID_SD_CARD = "STORAGE_UUID_SD_CARD"
        /**
         * TODO 可拔插 U 盘
         */
        @get:StorageMediaType
        const val STORAGE_UUID_USB_DRIVE = "STORAGE_UUID_USB_DRIVE"
        /**
         * 自定义路径
         */
        @get:StorageMediaType
        const val STORAGE_CUSTOM_ROOT_PATH = "STORAGE_CUSTOM_ROOT_PATH"

        /**
         * 存储类型，目前仅支持 [STORAGE_EXTERNAL_STORAGE] 和 [STORAGE_CUSTOM_ROOT_PATH]
         */
        @Retention(AnnotationRetention.SOURCE)
        annotation class StorageMediaType
    }
}