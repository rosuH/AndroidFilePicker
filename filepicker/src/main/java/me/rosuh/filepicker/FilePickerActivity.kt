package me.rosuh.filepicker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.os.Environment.MEDIA_MOUNTED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.collection.ArrayMap
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import me.rosuh.filepicker.R.string
import me.rosuh.filepicker.adapter.BaseAdapter
import me.rosuh.filepicker.adapter.FileListAdapter
import me.rosuh.filepicker.adapter.FileNavAdapter
import me.rosuh.filepicker.adapter.RecyclerViewListener
import me.rosuh.filepicker.bean.FileBean
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.bean.FileNavBeanImpl
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.utils.FileUtils
import me.rosuh.filepicker.utils.dp
import me.rosuh.filepicker.widget.PosLinearLayoutManager
import me.rosuh.filepicker.widget.RecyclerViewFilePicker
import java.io.File
import java.util.concurrent.*

@SuppressLint("ShowToast")
class FilePickerActivity : AppCompatActivity(), View.OnClickListener,
    RecyclerViewListener.OnItemClickListener {

    private var rvList: RecyclerViewFilePicker? = null
    private var rvNav: RecyclerView? = null
    private var srl: SwipeRefreshLayout? = null
    private var tvToolbarTitle: TextView? = null
    private var btnConfirm: Button? = null
    private var btnSelectedAll: Button? = null
    private var btnGoBack: ImageView? = null
    private var mainHandler = Handler(Looper.getMainLooper())

    // Creates a thread pool manager
    private val loadingThreadPool: ExecutorService =
        FilePickerManager.config.threadPool ?: ThreadPoolExecutor(
            1,       // Initial pool size
            1,       // Max pool size
            KEEP_ALIVE_TIME,
            TimeUnit.MINUTES,
            LinkedBlockingDeque()
        )

    private val loadFileRunnable: Runnable by lazy {
        Runnable {
            val customRootPathFile = pickerConfig.customRootPathFile
            val rootFile = when {
                customRootPathFile?.exists() == true -> {
                    // move to custom root dir
                    navDataSource.clear()
                    val root = FileUtils.getRootFile()
                    var curPath = customRootPathFile.absolutePath
                    while (curPath != root.parent && !curPath.isNullOrBlank()) {
                        Log.i("loadFileRunnable", "curPath = $curPath")
                        val f = File(curPath)
                        val fileNavBeanImpl = FileNavBeanImpl(
                            FileUtils.getDirAlias(f),
                            f.absolutePath
                        )
                        navDataSource.add(0, fileNavBeanImpl)
                        curPath = f.parent
                    }
                    pickerConfig.resetCustomFile()
                    customRootPathFile
                }
                navDataSource.isEmpty() && pickerConfig.isSkipDir -> {
                    FileUtils.getRootFile()
                }
                navDataSource.isEmpty() && !pickerConfig.isSkipDir -> {
                    // 如果是文件夹作为可选项时，需要让根目录也作为 item 被点击，故而取根目录上级作为 rootFiles
                    FileUtils.getRootFile().parentFile
                }
                else -> {
                    File(navDataSource.last().dirPath)
                }
            }

            val listData = FileUtils.produceListDataSource(rootFile)

            // 导航栏数据集
            navDataSource = FileUtils.produceNavDataSource(
                navDataSource,
                if (navDataSource.isEmpty()) {
                    rootFile.path
                } else {
                    navDataSource.last().dirPath
                },
                this@FilePickerActivity
            )
            mainHandler.post {
                initRv(listData, navDataSource)
                setLoadingFinish()
            }
        }
    }

    /**
     * 文件列表适配器
     */
    private val listAdapter: FileListAdapter by lazy {
        FileListAdapter(
            this@FilePickerActivity,
            FilePickerManager.config.singleChoice
        ).apply {
            addListener {
                onCheckSizeChanged {
                    updateItemUI()
                }
            }
        }
    }

    /**
     * 导航栏列表适配器
     */
    private val navAdapter: FileNavAdapter by lazy {
        FileNavAdapter(this@FilePickerActivity)
    }

    /**
     * 导航栏数据集
     */
    private var navDataSource = ArrayList<FileNavBeanImpl>()

    /**
     * 文件夹为空时展示的空视图
     */
    private val selectedCount
        get() = listAdapter.checkedCount
    private val maxSelectable = FilePickerManager.config.maxSelectable
    private val pickerConfig by lazy { FilePickerManager.config }
    private var fileListListener: RecyclerViewListener? = null
        get() {
            if (field == null) {
                field = getListener(rvList)
            }
            return field
        }
    private var navListener: RecyclerViewListener? = null
        get() {
            if (field == null) {
                field = getListener(rvNav)
            }
            return field
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(pickerConfig.themeId)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_for_file_picker)
        initView()
        // 核验权限
        // checking permission
        if (isPermissionGrated()) {
            loadList()
        } else {
            requestPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        val shouldShutDownThreadPool = pickerConfig.threadPool != loadingThreadPool
                || pickerConfig.threadPoolAutoShutDown

        if (!loadingThreadPool.isShutdown && shouldShutDownThreadPool) {
            Log.i(TAG, "shutdown thread pool")
            loadingThreadPool.shutdown()
        }
        currOffsetMap.clear()
        currPosMap.clear()
    }

    private fun isPermissionGrated() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    /**
     * 申请权限
     */
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@FilePickerActivity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            FILE_PICKER_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FILE_PICKER_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@FilePickerActivity.applicationContext,
                        getString(string.file_picker_request_permission_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                    setLoadingFinish()
                } else {
                    loadList()
                }
            }
        }
    }

    private fun initView() {
        btnGoBack = findViewById(R.id.btn_go_back_file_picker)
        btnGoBack?.setOnClickListener(this@FilePickerActivity)
        btnSelectedAll = findViewById(R.id.btn_selected_all_file_picker)
        btnSelectedAll?.apply {
            // 单选模式时隐藏并且不初始化
            if (pickerConfig.singleChoice) {
                visibility = View.GONE
                return@apply
            }
            setOnClickListener(this@FilePickerActivity)
            FilePickerManager.config.selectAllText.let {
                text = it
            }
        }
        btnConfirm = findViewById(R.id.btn_confirm_file_picker)
        btnConfirm?.apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                // 小于 4.4 的样式兼容
                // compatible with 4.4 api
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                    setMargins(0, 0, 16.dp, 0)
                }
            }
            setOnClickListener(this@FilePickerActivity)
            FilePickerManager.config.confirmText.let {
                text = it
            }
        }
        tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title_file_picker)
        tvToolbarTitle?.visibility = if (pickerConfig.singleChoice) {
            View.GONE
        } else {
            View.VISIBLE
        }
        srl = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        srl?.apply {
            setOnRefreshListener {
                resetViewState()
                loadList()
            }
            isRefreshing = true
            setColorSchemeColors(
                *resources.getIntArray(
                    when (pickerConfig.themeId) {
                        R.style.FilePickerThemeCrane -> {
                            R.array.crane_swl_colors
                        }
                        R.style.FilePickerThemeReply -> {
                            R.array.reply_swl_colors
                        }
                        R.style.FilePickerThemeShrine -> {
                            R.array.shrine_swl_colors
                        }
                        else -> {
                            R.array.rail_swl_colors
                        }
                    }
                )
            )
        }

        rvNav = findViewById<RecyclerView>(R.id.rv_nav_file_picker).apply {
            layoutManager =
                LinearLayoutManager(
                    this@FilePickerActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            adapter = navAdapter
        }
        rvList = findViewById<RecyclerViewFilePicker>(R.id.rv_list_file_picker).apply {
            setHasFixedSize(true)
            adapter = listAdapter
            layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_anim_file_picker)
            layoutManager = PosLinearLayoutManager(this@FilePickerActivity)
            if (!hasEmptyView()) {
                emptyView = LayoutInflater.from(context)
                    .inflate(R.layout.empty_file_list_file_picker, this, false).apply {
                        this.findViewById<TextView>(R.id.tv_empty_list).text =
                            pickerConfig.emptyListTips
                    }
            }
        }
    }

    private fun loadList() {
        if (!isPermissionGrated()) {
            requestPermission()
            return
        }
        if (Environment.getExternalStorageState() != MEDIA_MOUNTED) {
            Log.e(
                TAG, "External storage is not available ====>>> "
                        + "Environment.getExternalStorageState() != MEDIA_MOUNTED"
            )
            return
        }
        try {
            Log.i(TAG, "loadList in ${Thread.currentThread()} in $loadingThreadPool")
            loadingThreadPool.submit(loadFileRunnable)
        } catch (e: RejectedExecutionException) {
            Log.e(TAG, "submit job failed")
        }
    }

    private fun initRv(
        listData: ArrayList<FileItemBeanImpl>,
        navDataList: ArrayList<FileNavBeanImpl>
    ) {
        switchButton(true)
        // 导航栏适配器
        navAdapter.setNewData(navDataList)
        rvNav?.apply {
            navListener?.let { removeOnItemTouchListener(it) }
            navListener?.let { addOnItemTouchListener(it) }
        }
        // 列表适配器
        listAdapter.apply {
            isSingleChoice = FilePickerManager.config.singleChoice
            setNewData(listData)
        }
        rvList?.apply {
            fileListListener?.let { removeOnItemTouchListener(it) }
            fileListListener?.let { addOnItemTouchListener(it) }
        }
    }

    private fun setLoadingFinish() {
        srl?.isRefreshing = false
    }

    /**
     * 获取两个列表的监听器
     */
    private fun getListener(recyclerView: RecyclerView?): RecyclerViewListener? {
        if (recyclerView == null) {
            return null
        }
        return RecyclerViewListener(recyclerView, this@FilePickerActivity)
    }

    private val currPosMap: ArrayMap<String, Int> by lazy {
        ArrayMap(4)
    }
    private val currOffsetMap: ArrayMap<String, Int> by lazy {
        ArrayMap(4)
    }

    /**
     * 保存当前文件夹被点击项，下次进入时将滑动到此
     */
    private fun saveCurrPos(item: FileNavBeanImpl?, position: Int) {
        item?.run {
            currPosMap[filePath] = position
            (rvList?.layoutManager as? LinearLayoutManager)?.let {
                currOffsetMap.put(filePath, it.findViewByPosition(position)?.top ?: 0)
            }
        }
    }

    /*--------------------------Item click listener begin------------------------------*/

    /**
     * 传递 item 点击事件给调用者
     */
    override fun onItemClick(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        view: View,
        position: Int
    ) {
        val item = (adapter as BaseAdapter).getItem(position)
        item ?: return
        val file = File(item.filePath)
        if (!file.exists()) {
            return
        }
        when (view.id) {
            R.id.item_list_file_picker -> {
                // Check the lib users whether if intercept the click event.
                val hookItemClick = FilePickerManager.config.itemClickListener?.onItemClick(
                    adapter as FileListAdapter,
                    view,
                    position
                ) == true
                if (hookItemClick) {
                    return
                }
                if (file.isDirectory) {
                    (rvNav?.adapter as? FileNavAdapter)?.let {
                        saveCurrPos(it.dataList.last(), position)
                    }
                    // 如果是文件夹，则进入
                    enterDirAndUpdateUI(item)
                } else {
                    FilePickerManager.config.fileItemOnClickListener?.onItemClick(
                        adapter as FileListAdapter,
                        view,
                        position
                    )
                }
            }
            R.id.item_nav_file_picker -> {
                if (file.isDirectory) {
                    (rvNav?.adapter as? FileNavAdapter)?.let {
                        saveCurrPos(it.dataList.last(), position)
                    }
                    // 如果是文件夹，则进入
                    enterDirAndUpdateUI(item)
                }
            }
        }
    }

    /**
     * 子控件被点击
     */
    override fun onItemChildClick(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        view: View,
        position: Int
    ) {
        when (view.id) {
            R.id.tv_btn_nav_file_picker -> {
                val item = (adapter as FileNavAdapter).getItem(position)
                item ?: return
                enterDirAndUpdateUI(item)
            }
            else -> {
                val item = (adapter as FileListAdapter).getItem(position) ?: return
                // Check the lib users whether if intercept the click event.
                val hookItemClick = FilePickerManager.config.itemClickListener?.onItemChildClick(
                    adapter,
                    view,
                    position
                ) == true
                if (hookItemClick) {
                    return
                }
                // 文件夹直接进入
                // if it's Dir, enter directly
                if (item.isDir && pickerConfig.isSkipDir) {
                    enterDirAndUpdateUI(item)
                    return
                }
                if (pickerConfig.singleChoice) {
                    listAdapter.singleCheck(position)
                } else {
                    listAdapter.multipleCheckOrNo(item, position, ::isCanSelect) {
                        Toast.makeText(
                            this@FilePickerActivity.applicationContext,
                            getString(pickerConfig.maxSelectCountTips, maxSelectable),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    /**
     * 条目被长按
     */
    override fun onItemLongClick(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        view: View,
        position: Int
    ) {
        if (view.id != R.id.item_list_file_picker) return
        val item = (adapter as FileListAdapter).getItem(position) ?: return
        // Check the lib users whether if intercept the click event.
        val hookItemClick = FilePickerManager.config.itemClickListener?.onItemLongClick(
            adapter,
            view,
            position
        ) == true
        if (hookItemClick) {
            return
        }
        val file = File(item.filePath)
        val isSkipDir = FilePickerManager.config.isSkipDir
        // current item is directory and should skip directory, because long click would make the item been selected.
        if (file.exists() && file.isDirectory && isSkipDir) return
        // same action like child click
        if (item.isDir && pickerConfig.isSkipDir) {
            enterDirAndUpdateUI(item)
            return
        }
        if (pickerConfig.singleChoice) {
            listAdapter.singleCheck(position)
        } else {
            listAdapter.multipleCheckOrNo(item, position, ::isCanSelect) {
                Toast.makeText(
                    this@FilePickerActivity.applicationContext,
                    getString(pickerConfig.maxSelectCountTips, maxSelectable),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // notify listener
        FilePickerManager.config.fileItemOnClickListener?.onItemLongClick(adapter, view, position)
    }

    /*--------------------------Item click listener end------------------------------*/


    /**
     * 从导航栏中调用本方法，需要传入 pos，以便生产新的 nav adapter
     */
    private fun enterDirAndUpdateUI(fileBean: FileBean) {
        // 清除当前选中状态
        resetViewState()
        // 获取文件夹文件
        val nextFiles = File(fileBean.filePath)
        // 更新列表数据集
        listAdapter.setNewData(FileUtils.produceListDataSource(nextFiles))
        // 更新导航栏的数据集
        navDataSource = FileUtils.produceNavDataSource(
            ArrayList(navAdapter.dataList),
            fileBean.filePath,
            this@FilePickerActivity
        )
        navAdapter.setNewData(navDataSource)
        rvNav?.adapter?.itemCount?.let {
            rvNav?.smoothScrollToPosition(
                if (it == 0) {
                    0
                } else {
                    it - 1
                }
            )
        }
        notifyDataChangedForList(fileBean)
    }

    private fun notifyDataChangedForList(fileBean: FileBean) {
        rvList?.apply {
            (layoutManager as? PosLinearLayoutManager)?.setTargetPos(
                currPosMap[fileBean.filePath] ?: 0,
                currOffsetMap[fileBean.filePath] ?: 0
            )
            scheduleLayoutAnimation()
        }
    }

    private fun switchButton(isEnable: Boolean) {
        btnConfirm?.isEnabled = isEnable
        btnSelectedAll?.isEnabled = isEnable
    }

    private fun resetViewState() {
        listAdapter.resetCheck()
        updateItemUI()
    }

    private fun updateItemUI() {
        if (pickerConfig.singleChoice) {
            return
        }
        // 取消选中，并且选中数为 0
        if (selectedCount == 0) {
            btnSelectedAll?.text = pickerConfig.selectAllText
            tvToolbarTitle?.text = ""
            return
        }
        btnSelectedAll?.text = pickerConfig.deSelectAllText
        tvToolbarTitle?.text =
            resources.getString(pickerConfig.hadSelectedText, selectedCount)
    }

    override fun onBackPressed() {
        if ((rvNav?.adapter as? FileNavAdapter)?.itemCount ?: 0 <= 1) {
            super.onBackPressed()
        } else {
            // 即将进入的 item 的索引
            (rvNav?.adapter as? FileNavAdapter)?.run {
                enterDirAndUpdateUI(getItem(this.itemCount - 2)!!)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            // 全选
            R.id.btn_selected_all_file_picker -> {
                // 只要当前选中项数量大于 0，那么本按钮则为取消全选按钮
                if (selectedCount > 0) {
                    listAdapter.disCheckAll()
                } else if (isCanSelect()) {
                    // 当前选中数少于最大选中数，则即将执行选中
                    listAdapter.checkAll()
                }
            }
            // 确认按钮
            R.id.btn_confirm_file_picker -> {
                if (listAdapter.dataList.isNullOrEmpty()) {
                    return
                }
                val list = ArrayList<String>()
                val intent = Intent()

                for (data in listAdapter.dataList) {
                    if (data.isChecked()) {
                        list.add(data.filePath)
                    }
                }

                if (list.isEmpty()) {
                    this@FilePickerActivity.setResult(Activity.RESULT_CANCELED, intent)
                    finish()
                }

                FilePickerManager.saveData(list)
                this@FilePickerActivity.setResult(Activity.RESULT_OK, intent)
                finish()
            }
            R.id.btn_go_back_file_picker -> {
                onBackPressed()
            }
        }
    }

    private fun isCanSelect() = selectedCount < maxSelectable

    companion object {
        private const val TAG = "FilePickerActivity"
        private const val FILE_PICKER_PERMISSION_REQUEST_CODE = 10201

        // Sets the amount of time an idle thread waits before terminating
        private const val KEEP_ALIVE_TIME = 10L
    }
}
