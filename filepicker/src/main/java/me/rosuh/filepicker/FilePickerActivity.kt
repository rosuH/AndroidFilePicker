package me.rosuh.filepicker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.os.Environment.MEDIA_MOUNTED
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.empty_file_list_file_picker.view.*
import kotlinx.android.synthetic.main.main_activity_for_file_picker.*
import me.rosuh.filepicker.R.string
import me.rosuh.filepicker.adapter.BaseAdapter
import me.rosuh.filepicker.adapter.FileListAdapter
import me.rosuh.filepicker.adapter.FileNavAdapter
import me.rosuh.filepicker.adapter.RecyclerViewListener
import me.rosuh.filepicker.bean.BeanSubscriber
import me.rosuh.filepicker.bean.FileBean
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.bean.FileNavBeanImpl
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.utils.FileUtils
import me.rosuh.filepicker.utils.ScreenUtils
import me.rosuh.filepicker.widget.PosLinearLayoutManager
import java.io.File

@SuppressLint("ShowToast")
class FilePickerActivity : AppCompatActivity(), View.OnClickListener,
    RecyclerViewListener.OnItemClickListener,
    BeanSubscriber {

    private var mainHandler = Handler(Looper.getMainLooper())

    private var loadFileThread: Thread? = null

    private val loadFileRunnable: Runnable by lazy {
        Runnable {
            val rootFile = if (navDataSource.isEmpty()) {
                FileUtils.getRootFile()
            } else {
                File(navDataSource.last().dirPath)
            }
            val listData = FileUtils.produceListDataSource(rootFile, this@FilePickerActivity)
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
    private var listAdapter: FileListAdapter? = null

    /**
     * 导航栏列表适配器
     */
    private var navAdapter: FileNavAdapter? = null

    /**
     * 导航栏数据集
     */
    private var navDataSource = ArrayList<FileNavBeanImpl>()

    /**
     * 文件夹为空时展示的空视图
     */
    private var selectedCount: Int = 0
    private val maxSelectable = FilePickerManager.config.maxSelectable
    private val pickerConfig by lazy { FilePickerManager.config }
    private val fileListListener: RecyclerViewListener by lazy { getListener(rv_list_file_picker) }
    private val navListener: RecyclerViewListener by lazy { getListener(rv_nav_file_picker) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(pickerConfig.themeId)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_for_file_picker)
        // 核验权限
        // checking permission
        if (isPermissionGrated()) {
            prepareLauncher()
        } else {
            requestPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loadFileThread?.isAlive == true) {
            loadFileThread?.interrupt()
        }
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
        when (requestCode) {
            FILE_PICKER_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@FilePickerActivity.applicationContext,
                        getString(string.file_picker_request_permission_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    prepareLauncher()
                }
            }
        }
    }

    /**
     * 在做完权限申请之后开始的真正的工作
     */
    private fun prepareLauncher() {
        if (Environment.getExternalStorageState() != MEDIA_MOUNTED) {
            throw Throwable(cause = IllegalStateException("External storage is not available ====>>> Environment.getExternalStorageState() != MEDIA_MOUNTED"))
        }
        initView()
        // 加载中布局
        initLoadingView()
        loadList()
    }

    private fun initView() {
        btn_go_back_file_picker.setOnClickListener(this@FilePickerActivity)

        btn_selected_all_file_picker.apply {
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
        btn_confirm_file_picker.apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                // 小于 4.4 的样式兼容
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                    setMargins(0, 0, ScreenUtils.dipToPx(this@FilePickerActivity, 16f), 0)
                }
            }
            setOnClickListener(this@FilePickerActivity)
            FilePickerManager.config.confirmText.let {
                text = it
            }
        }

        tv_toolbar_title_file_picker.visibility = if (pickerConfig.singleChoice) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun loadList() {
        if (loadFileThread?.isAlive == true) {
            loadFileThread?.interrupt()
        }
        loadFileThread = Thread(loadFileRunnable)
        loadFileThread?.start()
    }

    private fun setLoadingFinish() {
        swipe_refresh_layout?.isRefreshing = false
    }

    private fun initLoadingView() {
        swipe_refresh_layout?.apply {
            setOnRefreshListener {
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
    }

    private fun initRv(
        listData: ArrayList<FileItemBeanImpl>?,
        navDataList: ArrayList<FileNavBeanImpl>
    ) {
        listData?.let { switchButton(true) }
        // 导航栏适配器
        rv_nav_file_picker?.apply {
            navAdapter = produceNavAdapter(navDataList)
            adapter = navAdapter
            layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(
                    this@FilePickerActivity,
                    androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                    false
                )
            removeOnItemTouchListener(navListener)
            addOnItemTouchListener(navListener)
        }

        // 列表适配器
        listAdapter = produceListAdapter(listData)
        rv_list_file_picker?.apply {
            emptyView = LayoutInflater.from(context)
                .inflate(R.layout.empty_file_list_file_picker, null).apply {
                    tv_empty_list.text = pickerConfig.emptyListTips
                }
            setHasFixedSize(true)
            adapter = listAdapter
            layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_anim_file_picker)
            layoutManager = PosLinearLayoutManager(this@FilePickerActivity)
            removeOnItemTouchListener(fileListListener)
            addOnItemTouchListener(fileListListener)
        }
    }

    /**
     * 获取两个列表的监听器
     */
    private fun getListener(recyclerView: androidx.recyclerview.widget.RecyclerView): RecyclerViewListener {
        return RecyclerViewListener(this@FilePickerActivity, recyclerView, this@FilePickerActivity)
    }

    /**
     * 构造列表的适配器
     */
    private fun produceListAdapter(dataSource: ArrayList<FileItemBeanImpl>?): FileListAdapter {
        return FileListAdapter(
            this@FilePickerActivity,
            dataSource,
            FilePickerManager.config.singleChoice
        )
    }

    /**
     * 构造导航栏适配器
     */
    private fun produceNavAdapter(dataSource: ArrayList<FileNavBeanImpl>): FileNavAdapter {
        return FileNavAdapter(this@FilePickerActivity, dataSource)
    }

    private val currPosMap: HashMap<String, Int> by lazy {
        HashMap<String, Int>(4)
    }
    private val currOffsetMap: HashMap<String, Int> by lazy {
        HashMap<String, Int>(4)
    }

    /**
     * 保存当前文件夹被点击项，下次进入时将滑动到此
     */
    private fun saveCurrPos(item: FileNavBeanImpl?, position: Int) {
        item?.run {
            currPosMap[filePath] = position
            (rv_list_file_picker?.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager)?.let {
                currOffsetMap.put(filePath, it.findViewByPosition(position)?.top ?: 0)
            }
        }
    }

    /*--------------------------Item click listener begin------------------------------*/

    /**
     * 传递 item 点击事件给调用者
     */
    override fun onItemClick(
        adapter: androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>,
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
                if (file.isDirectory) {
                    (rv_nav_file_picker?.adapter as? FileNavAdapter)?.let {
                        saveCurrPos(it.data.last(), position)
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
                    (rv_nav_file_picker?.adapter as? FileNavAdapter)?.let {
                        saveCurrPos(it.data.last(), position)
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
        adapter: androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>,
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
                // 文件夹直接进入
                // if it's Dir, enter directly
                if (item.isDir && pickerConfig.isSkipDir) {
                    enterDirAndUpdateUI(item)
                    return
                }
                if (pickerConfig.singleChoice) {
                    listAdapter?.singleCheck(position)
                } else {
                    listAdapter?.multipleCheckOrNo(item, position, ::isCanSelect) {
                        Toast.makeText(
                            this@FilePickerActivity.applicationContext,
                            "最多只能选择 $maxSelectable 项",
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
        adapter: androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>,
        view: View,
        position: Int
    ) {
        if (view.id != R.id.item_list_file_picker) return
        val item = (adapter as FileListAdapter).getItem(position)
        item ?: return
        val file = File(item.filePath)
        val isSkipDir = FilePickerManager.config.isSkipDir
        // 如果是文件夹并且没有略过文件夹
        if (file.exists() && file.isDirectory && isSkipDir) return
        // same action like child click
        onItemChildClick(adapter, view, position)
        // notify listener
        FilePickerManager.config.fileItemOnClickListener?.onItemLongClick(adapter, view, position)
    }

    /*--------------------------Item click listener end------------------------------*/


    /**
     * 从导航栏中调用本方法，需要传入 pos，以便生产新的 nav adapter
     */
    private fun enterDirAndUpdateUI(fileBean: FileBean) {
        // 清除当前选中状态
        cleanStatus()

        // 获取文件夹文件
        val nextFiles = File(fileBean.filePath)

        // 更新列表数据集
        listAdapter?.dataList =
            FileUtils.produceListDataSource(nextFiles, this@FilePickerActivity)

        // 更新导航栏的数据集
        navDataSource = FileUtils.produceNavDataSource(
            ArrayList(navAdapter!!.data),
            fileBean.filePath,
            this@FilePickerActivity
        )
        navAdapter?.data = navDataSource

        navAdapter!!.notifyDataSetChanged()
        notifyDataChangedForList(fileBean)

        rv_nav_file_picker?.adapter?.itemCount?.let {
            rv_nav_file_picker?.smoothScrollToPosition(
                if (it == 0) {
                    0
                } else {
                    it - 1
                }
            )
        }
    }

    private fun notifyDataChangedForList(fileBean: FileBean) {
        rv_list_file_picker?.apply {
            (layoutManager as? PosLinearLayoutManager)?.setTargetPos(
                currPosMap[fileBean.filePath] ?: 0,
                currOffsetMap[fileBean.filePath] ?: 0
            )
            layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_anim_file_picker)
            adapter?.notifyDataSetChanged()
            scheduleLayoutAnimation()
        }
    }

    private fun switchButton(isEnable: Boolean) {
        btn_confirm_file_picker?.isEnabled = isEnable
        btn_selected_all_file_picker?.isEnabled = isEnable
    }

    private fun cleanStatus() {
        selectedCount = 1
        updateItemUI(false)
    }

    override fun updateItemUI(isCheck: Boolean) {
        if (isCheck) {
            selectedCount++
        } else {
            selectedCount--
        }
        if (pickerConfig.singleChoice) {
            return
        }
        // 取消选中，并且选中数为 0
        if (selectedCount == 0) {
            btn_selected_all_file_picker.text = pickerConfig.selectAllText
            tv_toolbar_title_file_picker.text = ""
            return
        }
        btn_selected_all_file_picker.text = pickerConfig.deSelectAllText
        tv_toolbar_title_file_picker.text =
            resources.getString(pickerConfig.hadSelectedText, selectedCount)
    }

    override fun onBackPressed() {
        if ((rv_nav_file_picker?.adapter as? FileNavAdapter)?.itemCount ?: 0 <= 1) {
            super.onBackPressed()
        } else {
            // 即将进入的 item 的索引
            (rv_nav_file_picker?.adapter as? FileNavAdapter)?.run {
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
                    listAdapter?.disCheckAll()
                } else if (isCanSelect()) {
                    // 当前选中数少于最大选中数，则即将执行选中
                    listAdapter?.checkAll(selectedCount)
                }
            }
            // 确认按钮
            R.id.btn_confirm_file_picker -> {
                val list = ArrayList<String>()
                val intent = Intent()

                for (data in listAdapter!!.dataList!!) {
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
                finish()
            }
        }
    }

    private fun getAvailableCount(): Long {
        var count: Long = 0
        for (item in listAdapter!!.dataList!!) {
            val file = File(item.filePath)
            if (pickerConfig.isSkipDir && file.exists() && file.isDirectory) {
                continue
            }
            count++
        }
        return count
    }

    private fun isCanSelect() = selectedCount < maxSelectable

    companion object {
        private const val FILE_PICKER_PERMISSION_REQUEST_CODE = 10201
    }
}
