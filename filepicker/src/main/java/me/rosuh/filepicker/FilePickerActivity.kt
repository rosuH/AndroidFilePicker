package me.rosuh.filepicker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import me.rosuh.filepicker.adapter.FileListAdapter
import me.rosuh.filepicker.adapter.FileNavAdapter
import me.rosuh.filepicker.bean.FileItemBean
import me.rosuh.filepicker.bean.FileNavBean
import me.rosuh.filepicker.bean.IFileBean
import me.rosuh.filepicker.config.FilePickerConfig
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.utils.FileUtils
import me.rosuh.filepicker.utils.PercentTextView
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class FilePickerActivity : AppCompatActivity(), BaseQuickAdapter.OnItemClickListener, View.OnClickListener,
    BaseQuickAdapter.OnItemChildClickListener {

    /**
     * 文件列表
     */
    private var mRvList: RecyclerView? = null
    /**
     * 导航栏列表
     */
    private var mNavList: RecyclerView? = null
    /**
     * 文件列表适配器
     */
    private var mListAdapter: BaseQuickAdapter<FileItemBean, BaseViewHolder>? = null
    /**
     * 导航栏列表适配器
     */
    private var mNavAdapter: BaseQuickAdapter<FileNavBean, BaseViewHolder>? = null
    /**
     * 导航栏数据集
     */
    private var mNavDataSource = ArrayList<FileNavBean>()
    /**
     * 文件夹为空时展示的空视图
     */
    private var mEmptyView: View? = null

    private var mBtnSelectedAll: AppCompatButton? = null
    private var mBtnConfirm: AppCompatButton? = null
    private var mBtnGoBack: AppCompatImageButton? = null
    private val mFilesIsChecked: AtomicBoolean? = AtomicBoolean(false)
    private var mTvSelected: PercentTextView? = null
    private val pickerConfig by lazy { FilePickerConfig.getInstance(FilePickerManager.instance) }

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
                    Toast.makeText(this@FilePickerActivity, "未授予存储权限", Toast.LENGTH_SHORT).show()
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
            throw Throwable(cause = IllegalStateException("外部存储不可用"))
        }

        // 根目录文件对象
        val rootFile = FileUtils.getRootFile()

        // 文件列表数据集
        val listData = FileUtils.produceListDataSource(rootFile)
        mNavDataSource =
                FileUtils.produceNavDataSource(
                    mNavDataSource,
                    Environment.getExternalStorageDirectory().absolutePath
                )

        initView(listData, mNavDataSource)
    }

    private fun initView(fileListData: ArrayList<FileItemBean>, fileNavData: ArrayList<FileNavBean>) {
        mRvList = findViewById(R.id.rv_list_file_picker)
        mNavList = findViewById(R.id.rv_nav_file_picker)
        mBtnSelectedAll = findViewById(R.id.btn_selected_all_file_picker)
        mBtnConfirm = findViewById(R.id.btn_confirm_file_picker)
        mBtnGoBack = findViewById(R.id.btn_go_back_file_picker)
        mTvSelected = findViewById(R.id.tv_toolbar_title_file_picker)
        mBtnGoBack!!.setOnClickListener(this)
        mBtnSelectedAll!!.setOnClickListener(this)
        mBtnConfirm!!.setOnClickListener(this)

        // 空视图
        mEmptyView =
                layoutInflater.inflate(R.layout.item_empty_view_file_picker, mRvList!!.parent as ViewGroup, false)
        // 列表适配器
        mListAdapter = produceListAdapter(fileListData)
        // 导航栏适配器
        mNavAdapter = produceNavAdapter(fileNavData)
        mRvList!!.adapter = mListAdapter
        mNavList!!.adapter = mNavAdapter
        mRvList!!.layoutManager = LinearLayoutManager(this@FilePickerActivity)
        val linearLayoutManager = LinearLayoutManager(this@FilePickerActivity, LinearLayoutManager.HORIZONTAL, false)
        mNavList!!.layoutManager = linearLayoutManager
    }

    /**
     * 生产列表的适配器
     * @param dataSource 列表数据集
     * @return 列表适配器
     */
    private fun produceListAdapter(dataSource: ArrayList<FileItemBean>): BaseQuickAdapter<FileItemBean, BaseViewHolder> {
        val fileListAdapter = FileListAdapter(R.layout.item_list_file_picker, dataSource)
        // 避免频繁添加空视图导致 view 存在 parent 视图，所以需要先判断
        if (mEmptyView!!.parent != null) {
            val viewGroup = mEmptyView!!.parent as ViewGroup
            viewGroup.removeView(mEmptyView)
        }
        fileListAdapter.emptyView = mEmptyView
        fileListAdapter.onItemClickListener = this
        fileListAdapter.onItemChildClickListener = this
        return fileListAdapter
    }

    /**
     * 生产导航栏适配器
     * @param dataSource 导航栏数据集
     * @return 导航栏适配器
     */
    private fun produceNavAdapter(dataSource: ArrayList<FileNavBean>): BaseQuickAdapter<FileNavBean, BaseViewHolder> {
        val adapter = FileNavAdapter(R.layout.item_nav_file_picker, dataSource)
        adapter.onItemClickListener = this
        adapter.onItemChildClickListener = this
        return adapter
    }

    /**
     * 传递条目点击事件给调用者
     * @param adapter BaseQuickAdapter<*, *>?
     * @param view View?
     * @param position Int
     */
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        // 如果不是点击列表，则返回
        if (view!!.id != R.id.item_list_file_picker) return
        val item = adapter!!.getItem(position) as FileItemBean
        val file = File(item.filePath)

        if (file.exists() && file.isDirectory) {
            // 如果是文件夹，则进入
            enterDirAndUpdateUI(item)
        } else {
            FilePickerConfig.getInstance(FilePickerManager.instance).fileIFileItemOnClickListener?.onItemClick(item, position)
        }
    }

    /**
     * 通过传入的 item 配置新的列表适配器，然后更新数据集，接着更新列表
     * 从列表中时，需要获取目标文件夹在 nav 列表中的位置，如果没有则传入 -1
     * TODO 进入下一个文件夹之前，需要先清空当前的选中状态？貌似不需要
     */
    private fun enterDirAndUpdateUI(iFileBean: IFileBean) {
        var pos = -1

        for (data in mNavAdapter!!.data) {
            if (data.dirPath == iFileBean.filePath) {
                pos = mNavAdapter!!.data.indexOf(data)
            }
        }

        enterDirAndUpdateUI(iFileBean, pos)
    }

    /**
     * 从导航栏中调用本方法，需要传入 pos，以便生产新的 nav adapter
     * @param iFileBean IFileBean
     * @param position Int 用来定位导航栏的当前 item，如果是后退按钮，则传入倒数第二个 position
     */
    private fun enterDirAndUpdateUI(iFileBean: IFileBean, position: Int) {
        // 获取文件夹文件
        val nextFiles = File(iFileBean.filePath)
        // 获取列表的数据集
        val listDataSource = FileUtils.produceListDataSource(nextFiles)
        // 获取导航栏的数据集
        mNavDataSource = FileUtils.produceNavDataSource(ArrayList(mNavAdapter!!.data), iFileBean.filePath)

        mListAdapter = produceListAdapter(listDataSource)
        mNavAdapter = produceNavAdapter(mNavDataSource)

        mRvList!!.adapter = mListAdapter
        mNavList!!.adapter = mNavAdapter
        mListAdapter!!.notifyDataSetChanged()
        mNavAdapter!!.notifyDataSetChanged()

        if (position == -1) return

        mNavList!!.scrollToPosition(position)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view!!.id) {
            R.id.btn_nav_file_picker -> {
                val item = adapter!!.getItem(position) as FileNavBean
                enterDirAndUpdateUI(item, position)
            }
            R.id.cb_list_file_picker -> {
                val item = adapter!!.getItem(position) as FileItemBean
                val checkBox = view.findViewById<CheckBox>(R.id.cb_list_file_picker)
                item.isChecked = checkBox.isChecked
                Toast.makeText(this@FilePickerActivity, "选中", Toast.LENGTH_SHORT).show()
            }
        }
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
                if (mFilesIsChecked!!.get()) {
                    for (data in mListAdapter!!.data) {
                        val file = File(data.filePath)
                        if (pickerConfig.isSkipDir && file.exists() && file.isDirectory) {
                            continue
                        }
                        data.isChecked = false
                    }
                    mBtnSelectedAll!!.text = pickerConfig.selectAllText
                    mTvSelected!!.text = pickerConfig.goBackText
                } else {
                    var checkedCount = 0
                    for (data in mListAdapter!!.data) {
                        val file = File(data.filePath)
                        if (pickerConfig.isSkipDir && file.exists() && file.isDirectory) {
                            continue
                        }
                        data.isChecked = true
                        checkedCount++
                    }
                    mBtnSelectedAll!!.text = pickerConfig.unSelectAllText
                    mTvSelected!!.text = pickerConfig.hadSelectedText + checkedCount
                }

                mListAdapter!!.notifyDataSetChanged()
                mFilesIsChecked.set(!(mFilesIsChecked.get()))
            }
            R.id.btn_confirm_file_picker -> {
                val list = ArrayList<String>()
                val intent = Intent()

                for (data in mListAdapter!!.data) {
                    if (data.isChecked) {
                        list.add(data.filePath)
                    }
                }

                if (list.isEmpty()) {
                    this@FilePickerActivity.setResult(Activity.RESULT_CANCELED, intent)
                    finish()
                }

                FilePickerManager.instance.saveData(list)
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
