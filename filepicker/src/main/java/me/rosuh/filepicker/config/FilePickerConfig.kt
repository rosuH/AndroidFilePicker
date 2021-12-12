package me.rosuh.filepicker.config

import android.content.Intent
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import me.rosuh.filepicker.FilePickerActivity
import me.rosuh.filepicker.R
import me.rosuh.filepicker.engine.ImageEngine
import me.rosuh.filepicker.filetype.FileType
import java.io.File
import java.util.concurrent.*

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
class FilePickerConfig(private val pickerManager: FilePickerManager) {

    var isAutoFilter: Boolean = false

    private val customFileTypes: ArrayList<FileType> by lazy {
        ArrayList<FileType>(2)
    }

    private val contextRes = pickerManager.contextRef!!.get()!!.resources

    /**
     * 是否显示隐藏文件，默认隐藏
     * 以符号 . 开头的文件或文件夹视为隐藏
     */
    var isShowHiddenFiles = false
        private set

    /**
     * 是否显示选中框，默认显示
     */
    var isShowingCheckBox = true
        private set

    /**
     * 在选中时是否忽略文件夹
     */
    var isSkipDir = true
        private set

    /**
     * 是否是单选
     * 如果是单选，则隐藏顶部【全选/取消全选按钮】
     */
    var singleChoice = false
        private set

    /**
     * 最大可被选中数量
     */
    var maxSelectable = Int.MAX_VALUE
        private set

    internal val defaultStorageName = contextRes.getString(R.string.file_picker_tv_sd_card)

    /**
     * 存储类型
     */
    var mediaStorageName = defaultStorageName
        private set

    /**
     * 自定义存储类型，根据此返回根目录
     */
    @get:StorageMediaType
    @set:StorageMediaType
    var mediaStorageType: String = STORAGE_EXTERNAL_STORAGE
        private set

    /**
     * 自定义根目录路径，需要先设置 [mediaStorageType] 为 [STORAGE_CUSTOM_ROOT_PATH]
     */
    var customRootPath: String = ""
        private set

    internal var customRootPathFile: File? = null
        private set

    /**
     * 自定义过滤器
     */
    var selfFilter: AbstractFileFilter? = null
        private set

    /**
     * 自定文件类型甄别器和默认类型甄别器
     */
    @Deprecated(
        "Use 'register' function instead.",
        replaceWith = ReplaceWith("me.rosuh.filepicker.config.FilePickerConfig.registerFileType()"),
        level = DeprecationLevel.WARNING
    )
    var customDetector: AbstractFileDetector? = null

    val defaultFileDetector: DefaultFileDetector by lazy { DefaultFileDetector().also { it.registerDefaultTypes() } }

    /**
     * 点击操作接口，采用默认实现
     */
    @Deprecated("Check the itemClickListener")
    var fileItemOnClickListener: FileItemOnClickListener? = null
        private set

    /**
     * 点击操作接口
     */
    var itemClickListener: ItemClickListener? = null
        private set

    /**
     * 主题
     */
    var themeId: Int = R.style.FilePickerThemeRail
        private set

    /**
     * 全选文字，取消全选文字，返回文字，已选择文字，确认按钮，选择限制提示语，空列表提示
     */
    var selectAllText: String = contextRes.getString(R.string.file_picker_tv_select_all)
        private set
    var deSelectAllText: String = contextRes.getString(R.string.file_picker_tv_deselect_all)
        private set

    @StringRes
    var hadSelectedText: Int = R.string.file_picker_selected_count
        private set
    var confirmText: String = contextRes.getString(R.string.file_picker_tv_select_done)
        private set

    @StringRes
    var maxSelectCountTips: Int = R.string.max_select_count_tips
        private set

    var emptyListTips: String = contextRes.getString(R.string.empty_list_tips_file_picker)
        private set

    /**
     * 允许使用你项目中的线程池
     * Allow the use of thread pools in your project
     */
    internal  var threadPool: ExecutorService? = null

    /**
     * 自定义线程池不会默认关闭，如果你需要在结束文件选择时关闭，请传 true
     * The custom thread pool will not be closed by default,
     * if you need to close when the file selection is finished, please pass true
     */
    internal var threadPoolAutoShutDown: Boolean = false

    /**
     * 如果您的 Glide 版本低于 4.9, 请使用自定义的 [ImageEngine]
     */

    /**
     * 如果您的 Glide 版本低于 4.9, 请使用自定义的 [ImageEngine]
     */
    var customImageEngine: ImageEngine? = null
        private set

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
    fun storageType(
        volumeName: String = "",
        @StorageMediaType storageMediaType: String
    ): FilePickerConfig {
        mediaStorageName = volumeName
        mediaStorageType = storageMediaType
        return this
    }

    fun setCustomRootPath(path: String): FilePickerConfig {
        customRootPath = path
        path.takeIf {
            it.isNotBlank()
        }?.let {
            File(it)
        }?.takeIf {
            it.exists()
        }?.let {
            customRootPathFile = it
        }
        return this
    }

    fun filter(fileFilter: AbstractFileFilter): FilePickerConfig {
        selfFilter = fileFilter
        return this
    }

    /**
     * @author rosuh@qq.com
     * @date 2020/9/15
     * custom file type had upgrade to [registerFileType], which can simplify your usage.
     * 实现 [AbstractFileDetector] 以自定义您自己的文件类型检测器
     * Custom your file detector by implementing [AbstractFileDetector]
     */
    @Deprecated(
        "Use 'register' function instead.",
        ReplaceWith("registerFileType(types)"),
        level = DeprecationLevel.WARNING
    )
    fun customDetector(detector: AbstractFileDetector): FilePickerConfig {
        this.customDetector = detector
        return this
    }

    /**
     * This method would be removed in 0.8.0.
     * Try to using [ItemClickListener] which the below one.
     * @author hi@rosuh.me
     */
    @Deprecated(
        "It's not flexible enough.",
        replaceWith = ReplaceWith("me.rosuh.filepicker.config.FilePickerConfig.setItemClickListener")
    )
    fun setItemClickListener(fileItemOnClickListener: FileItemOnClickListener): FilePickerConfig {
        this.fileItemOnClickListener = fileItemOnClickListener
        return this
    }

    /**
     * Setting item click listener which can intercept click event.
     * @author hi@rosuh.me
     * @since 0.7.2
     */
    fun setItemClickListener(
        itemClickListener: ItemClickListener
    ): FilePickerConfig {
        this.itemClickListener = itemClickListener
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

    fun imageEngine(ie: ImageEngine): FilePickerConfig {
        this.customImageEngine = ie
        return this
    }

    /**
     * @author rosuh@qq.com
     * @date 2020/9/15
     * 用于注册你自定义的文件类型。
     * 库将自动调用你的自定义类型里的[FileType.verify]来识别文件。如果识别成功，就会自动填充到 [me.rosuh.filepicker.bean.FileItemBeanImpl.fileType] 中
     * 如果[autoFilter]为 true，那么库将自动过滤掉不符合你自定义类型的文件。不会在结果中显示出来。
     * 如果为 false，那么就只是检测类型。不会对结果列表做修改
     * 你不需要再调用[fileType]方法，否则将默认使用[fileType]
     * ---
     * Pass your custom [FileType] instances list and all done! This lib would auto detect file type
     * by using [FileType.verify].
     * If [autoFilter] is true, this lib will filter result by using your custom file types.
     * If [autoFilter] is true, the library will automatically filter out files that do not meet your custom type.
     * Will not show up in the results. * If it is false, then only the detection type. No changes to the result list
     * You don't need to call [fileType] again !
     */
    fun registerFileType(types: List<FileType>, autoFilter: Boolean = true): FilePickerConfig {
        this.customFileTypes.addAll(types)
        this.defaultFileDetector.registerCustomTypes(customFileTypes)
        this.isAutoFilter = autoFilter
        return this
    }

    /**
     * 允许使用你项目中的线程池, 自定义线程池不会默认关闭，如果你需要在结束文件选择时关闭，请传 true
     * Allow the use of thread pools in your project
     * The custom thread pool will not be closed by default,
     * if you need to close when the file selection is finished, please pass true
     */
    fun threadPool(threadPool: ExecutorService, autoShutdown: Boolean): FilePickerConfig {
        this.threadPool = threadPool
        this.threadPoolAutoShutDown = autoShutdown
        return this
    }

    fun forResult(requestCode: Int) {
        val activity = pickerManager.contextRef?.get()
        val fragment = pickerManager.fragmentRef?.get()

        val intent = Intent(activity, FilePickerActivity::class.java)
        if (fragment == null) {
            activity?.startActivityForResult(intent, requestCode)
        } else {
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    fun resetCustomFile() {
        this.customRootPathFile = null
    }

    fun clear() {
        this.customFileTypes.clear()
        this.customImageEngine = null
        this.fileItemOnClickListener = null
        this.selfFilter = null
        this.customDetector = null
        this.defaultFileDetector.clear()
        resetCustomFile()
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