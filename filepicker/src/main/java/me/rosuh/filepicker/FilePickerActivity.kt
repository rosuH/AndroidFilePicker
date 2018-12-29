package me.rosuh.filepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_file_picker.btn_confirm_file_picker
import kotlinx.android.synthetic.main.activity_file_picker.btn_go_back_file_picker
import kotlinx.android.synthetic.main.activity_file_picker.btn_selected_all_file_picker
import kotlinx.android.synthetic.main.activity_file_picker.rv_list_file_picker
import kotlinx.android.synthetic.main.activity_file_picker.rv_nav_file_picker
import kotlinx.android.synthetic.main.activity_file_picker.tv_toolbar_title_file_picker
import me.rosuh.filepicker.R.string
import me.rosuh.filepicker.adapter.FileListAdapter
import me.rosuh.filepicker.adapter.FileNavAdapter
import me.rosuh.filepicker.adapter.RecyclerViewListener
import me.rosuh.filepicker.bean.BeanSubscriber
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.bean.FileNavBeanImpl
import me.rosuh.filepicker.bean.FileBean
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.utils.FileUtils
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class FilePickerActivity : AppCompatActivity(), View.OnClickListener, RecyclerViewListener.IOnItemClickListener,
    BeanSubscriber {
    /**
     * 文件列表适配器
     */
    private var mListAdapter: FileListAdapter? = null
    /**
     * 导航栏列表适配器
     */
    private var mNavAdapter: FileNavAdapter? = null
    /**
     * 导航栏数据集
     */
    private var mNavDataSource = ArrayList<FileNavBeanImpl>()
    /**
     * 文件夹为空时展示的空视图
     */
    private var mEmptyView: View? = null
    private var selectedCount: AtomicInteger = AtomicInteger(0)
    private val maxSelectable = FilePickerManager.config.maxSelectable
    private val pickerConfig by lazy { FilePickerManager.config }
    private val fileListListener: RecyclerViewListener by lazy { getListener(rv_list_file_picker!!) }
    private val navListener: RecyclerViewListener by lazy { getListener(rv_nav_file_picker!!) }
    private val fileListener: RecyclerViewListener by lazy { getListener(rv_list_file_picker!!) }
    private val availableCount by lazy {
        var count = 0
        for (item in mListAdapter!!.data) {
            val file = File(item.filePath)
            if (pickerConfig.isSkipDir && file.exists() && file.isDirectory) {
                continue
            }
            count++
        }
        count
    }
    private val TAG = "FilePickerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(pickerConfig.themeId)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_picker)
        // 获取权限
        if (setupPermission()) {
            prepareLauncher()
        }
    }

    private fun setupPermission(): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
            return false
        }
        return true
    }

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
                    Toast.makeText(this@FilePickerActivity, getString(string.file_picker_request_permission_failed), Toast.LENGTH_SHORT).show()
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

        // 根目录文件对象
        val rootFile = FileUtils.getRootFile()
        // 文件列表数据集
        val listData = FileUtils.produceListDataSource(rootFile, this@FilePickerActivity)
        // 导航栏数据集
        mNavDataSource =
                FileUtils.produceNavDataSource(
                    mNavDataSource,
                    Environment.getExternalStorageDirectory().absolutePath
                )

        initView(listData, mNavDataSource)
    }

    private fun initView(
        listDataList: ArrayList<FileItemBeanImpl>,
        navDataList: ArrayList<FileNavBeanImpl>
    ) {
        btn_go_back_file_picker!!.setOnClickListener(this)
        btn_selected_all_file_picker!!.setOnClickListener(this)
        btn_confirm_file_picker!!.setOnClickListener(this)

        // 空视图
        mEmptyView =
                layoutInflater.inflate(
                    R.layout.item_empty_view_file_picker,
                    rv_list_file_picker!!.parent as ViewGroup,
                    false
                )
        // 列表适配器
        mListAdapter = produceListAdapter(listDataList)
        // 导航栏适配器
        mNavAdapter = produceNavAdapter(navDataList)
        rv_list_file_picker!!.adapter = mListAdapter
        rv_nav_file_picker!!.adapter = mNavAdapter
        rv_list_file_picker!!.layoutManager = LinearLayoutManager(this@FilePickerActivity)
        val linearLayoutManager = LinearLayoutManager(this@FilePickerActivity, LinearLayoutManager.HORIZONTAL, false)
        rv_nav_file_picker!!.layoutManager = linearLayoutManager
        rv_list_file_picker!!.addOnItemTouchListener(fileListListener)
        rv_nav_file_picker!!.addOnItemTouchListener(navListener)
    }

    /**
     * 获取两个列表的监听器
     */
    private fun getListener(recyclerView: RecyclerView): RecyclerViewListener {
        return RecyclerViewListener(this@FilePickerActivity, recyclerView, this@FilePickerActivity)
    }

    /**
     * 生产列表的适配器
     * @param dataSource 列表数据集
     * @return 列表适配器
     */
    private fun produceListAdapter(dataSource: ArrayList<FileItemBeanImpl>): FileListAdapter {
        val fileListAdapter = FileListAdapter(this@FilePickerActivity, dataSource)
        // 避免频繁添加空视图导致 view 存在 parent 视图，所以需要先判断
        if (mEmptyView!!.parent != null) {
            val viewGroup = mEmptyView!!.parent as ViewGroup
            viewGroup.removeView(mEmptyView)
        }
        fileListAdapter.recyclerViewListener = fileListener
        return fileListAdapter
    }

    /**
     * 生产导航栏适配器
     * @param dataSource 导航栏数据集
     * @return 导航栏适配器
     */
    private fun produceNavAdapter(dataSource: ArrayList<FileNavBeanImpl>): FileNavAdapter {
        val adapter = FileNavAdapter(this@FilePickerActivity, dataSource)
        adapter.recyclerViewListener = navListener
        return adapter
    }

    /**
     * 传递条目点击事件给调用者
     * @param recyclerAdapter RecyclerView.Adapter
     * @param view View?
     * @param position Int
     */
    override fun onItemClick(
        recyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        view: View,
        position: Int
    ) {
        when (view.id) {
            R.id.item_list_file_picker -> {
                val item = (recyclerAdapter as FileListAdapter).getItem(position)
                item ?: return
                val file = File(item.filePath)
                if (file.exists() && file.isDirectory) {
                    // 如果是文件夹，则进入
                    enterDirAndUpdateUI(item)
                } else {
                    FilePickerManager.config.fileItemOnClickListener.onItemClick(recyclerAdapter, view, position)
                }
            }
        }
    }

    /**
     * 条目被长按
     */
    override fun onItemLongClick(
        recyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        view: View,
        position: Int
    ) {
        if (view.id != R.id.item_list_file_picker) return
        val item = (recyclerAdapter as FileListAdapter).getItem(position)
        item ?: return
        val file = File(item.filePath)
        val isSkipDir = FilePickerManager.config.isSkipDir
        // 如果是文件夹并且没有略过文件夹
        if (file.exists() && file.isDirectory && isSkipDir) return
        val cb = view.findViewById<CheckBox>(R.id.cb_list_file_picker)

        when {
            cb.isChecked -> {
                // 当前被选中，现在取消选中
                FilePickerManager.config.fileItemOnClickListener.onItemLongClick(recyclerAdapter, view, position)
                selectedCount.decrementAndGet()
            }
            isCanSelect() -> {
                // 新增选中项情况
                FilePickerManager.config.fileItemOnClickListener.onItemLongClick(recyclerAdapter, view, position)
                selectedCount.incrementAndGet()
            }
            else -> {
                // 新增失败的情况
                Toast.makeText(this@FilePickerActivity, "最多只能选择 $maxSelectable 项", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 子控件被点击
     */
    override fun onItemChildClick(
        recyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        view: View,
        position: Int
    ) {
        when (view.id) {
            R.id.btn_nav_file_picker -> {
                val item = (recyclerAdapter as FileNavAdapter).getItem(position)
                item ?: return
                enterDirAndUpdateUI(item, position)
            }
            else -> {
                val item = (recyclerAdapter as FileListAdapter).getItem(position)
                item ?: return
                // 略过文件夹
                if (item.isDir && pickerConfig.isSkipDir) return

                val checkBox = view.findViewById<CheckBox>(R.id.cb_list_file_picker)
                // checkBox 的点击事件被拦截下来到此，不会继续传递下去
                when {
                    checkBox.isChecked -> {
                        // 当前被选中，说明即将取消选中
                        selectedCount.decrementAndGet()
                        item.setCheck(false)
                        checkBox.isChecked = false
                    }
                    isCanSelect() -> {
                        // 当前未被选中，并且检查合格，则即将新增选中
                        selectedCount.incrementAndGet()
                        item.setCheck(true)
                        checkBox.isChecked = true
                    }
                    else -> {
                        // 新增选中项失败的情况
                        checkBox.isChecked = false
                        item.setCheck(false)
                        Toast.makeText(this@FilePickerActivity, "最多只能选择 $maxSelectable 项", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun isCanSelect(): Boolean {
        // 可选的
        var checkedCount = 0
        for (item in mListAdapter!!.data) {
            if (item.isChecked()) checkedCount++
        }

        return ((checkedCount < maxSelectable) && (checkedCount < availableCount))
    }

    /**
     * 通过传入的 item 配置新的列表适配器，然后更新数据集，接着更新列表
     * 从列表中时，需要获取目标文件夹在 nav 列表中的位置，如果没有则传入 -1
     * TODO 进入下一个文件夹之前，需要先清空当前的选中状态
     */
    private fun enterDirAndUpdateUI(fileBean: FileBean) {
        var pos = -1

        for (data in mNavAdapter!!.data) {
            if (data.dirPath == fileBean.filePath) {
                pos = mNavAdapter!!.data.indexOf(data)
            }
        }

        enterDirAndUpdateUI(fileBean, pos)
    }

    /**
     * 从导航栏中调用本方法，需要传入 pos，以便生产新的 nav adapter
     * @param fileBean FileBean
     * @param position Int 用来定位导航栏的当前 item，如果是后退按钮，则传入倒数第二个 position
     */
    private fun enterDirAndUpdateUI(fileBean: FileBean, position: Int) {

        //清除当前选中状态
        cleanStatus()

        // 获取文件夹文件
        val nextFiles = File(fileBean.filePath)
        // 获取列表的数据集
        val listDataSource = FileUtils.produceListDataSource(nextFiles, this@FilePickerActivity)
        // 获取导航栏的数据集
        mNavDataSource = FileUtils.produceNavDataSource(ArrayList(mNavAdapter!!.data), fileBean.filePath)

        mListAdapter = produceListAdapter(listDataSource)
        mNavAdapter = produceNavAdapter(mNavDataSource)

        rv_list_file_picker!!.adapter = mListAdapter
        rv_nav_file_picker!!.adapter = mNavAdapter
        mListAdapter!!.notifyDataSetChanged()
        mNavAdapter!!.notifyDataSetChanged()

        if (position == -1) return

        rv_nav_file_picker!!.scrollToPosition(position)
    }

    private fun cleanStatus() {
        selectedCount.set(0)
        updateUI(false)
    }

    override fun updateUI(isCheck: Boolean) {
        // 取消选中，并且选中数为 0
        if (selectedCount.get() == 0) {
            btn_selected_all_file_picker!!.text = pickerConfig.selectAllText
            tv_toolbar_title_file_picker!!.text = pickerConfig.goBackText
            return
        }
        btn_selected_all_file_picker!!.text = pickerConfig.unSelectAllText
        tv_toolbar_title_file_picker!!.text = resources.getString(R.string.file_picker_selected_count, selectedCount.get())
    }

    override fun onBackPressed() {
        if (mNavDataSource.size <= 1) {
            super.onBackPressed()
        } else {
            // 即将进入的 item 的索引
            val willEnterItemPos = mNavDataSource.size - 2
            enterDirAndUpdateUI(mNavDataSource[willEnterItemPos], willEnterItemPos)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            // 全选
            R.id.btn_selected_all_file_picker -> {
                // 只要当前选中项数量大于 0，那么本按钮则为取消全选按钮
                if (selectedCount.get() > 0) {
                    selectedCount.set(0)
                    for (data in mListAdapter!!.data) {
                        val file = File(data.filePath)
                        if (pickerConfig.isSkipDir && file.exists() && file.isDirectory) {
                            continue
                        }
                        data.setCheck(false)
                    }
                } else if (isCanSelect()) {
                    // 当前选中数少于最大选中数，则即将执行选中
                    for (i in selectedCount.get()..(mListAdapter!!.data.size - 1)) {
                        val data = mListAdapter!!.data[i]
                        val file = File(data.filePath)
                        if (pickerConfig.isSkipDir && file.exists() && file.isDirectory) {
                            continue
                        }
                        selectedCount.incrementAndGet()
                        data.setCheck(true)
                        if (selectedCount.get() >= maxSelectable) {
                            break
                        }
                    }
                }
                mListAdapter!!.notifyDataSetChanged()
            }
            // 确认按钮
            R.id.btn_confirm_file_picker -> {
                val list = ArrayList<String>()
                val intent = Intent()

                for (data in mListAdapter!!.data) {
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

    companion object {
        private const val FILE_PICKER_PERMISSION_REQUEST_CODE = 10201
    }
}
