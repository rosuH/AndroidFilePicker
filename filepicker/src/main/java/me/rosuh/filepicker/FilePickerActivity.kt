package me.rosuh.filepicker

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.tbruyelle.rxpermissions2.RxPermissions
import me.rosuh.filepicker.bean.FileTypeEnum.COMPRESSED
import me.rosuh.filepicker.bean.FileTypeEnum.DIR
import me.rosuh.filepicker.bean.FileTypeEnum.IMAGE
import me.rosuh.filepicker.bean.FileTypeEnum.OCTET_STREAM
import me.rosuh.filepicker.bean.FileTypeEnum.UNKNOWN
import me.rosuh.filepicker.bean.FileTypeEnum.VIDEO
import me.rosuh.filepicker.adapter.FileListAdapter
import me.rosuh.filepicker.adapter.FileNavAdapter
import me.rosuh.filepicker.bean.FileItemBean
import me.rosuh.filepicker.bean.FileNavBean
import me.rosuh.filepicker.bean.IFileBean
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.config.FilePickerManager.RESULT_KEY
import me.rosuh.filepicker.utils.FileUtils
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class FilePickerActivity : AppCompatActivity(), BaseQuickAdapter.OnItemClickListener, View.OnClickListener,
    BaseQuickAdapter.OnItemChildClickListener {

    /**
     * 文件列表
     */
    var mRvList: RecyclerView? = null
    /**
     * 导航栏列表
     */
    var mNavList: RecyclerView? = null
    /**
     * 文件列表适配器
     */
    var mListAdapter: BaseQuickAdapter<FileItemBean, BaseViewHolder>? = null
    /**
     * 导航栏列表适配器
     */
    var mNavAdapter: BaseQuickAdapter<FileNavBean, BaseViewHolder>? = null
    /**
     * 导航栏数据集
     */
    var mNavDataSource = ArrayList<FileNavBean>()
    /**
     * 文件夹为空时展示的空视图
     */
    var mEmptyView: View? = null

    var mBtnSelectedAll: AppCompatButton? = null
    var mBtnConfirm: AppCompatButton? = null
    var mBtnGoBack: AppCompatImageButton? = null
    val mFilesIsChecked: AtomicBoolean? = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.FilePickerTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 获取权限
        val rxPermissions = RxPermissions(this)
        val dispose = rxPermissions
            .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe {
                if (!it) {
                    Toast.makeText(this@FilePickerActivity, "未授予存储权限", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@FilePickerActivity, "已授予存储权限", Toast.LENGTH_SHORT).show()
                    if (Environment.getExternalStorageState() != MEDIA_MOUNTED) {
                        throw Throwable(IllegalStateException("外部存储不可用"))
                    }

                    // 根目录文件对象
                    val rootFile = File(Environment.getExternalStorageDirectory().absoluteFile.toURI())

                    // 文件列表数据集
                    val listData = FileUtils.produceListDataSource(rootFile)
                    mNavDataSource =
                            FileUtils.produceNavDataSource(
                                mNavDataSource,
                                Environment.getExternalStorageDirectory().absolutePath
                            )

                    initView(listData, mNavDataSource)
                }
            }
    }

    fun initView(fileListData: ArrayList<FileItemBean>, fileNavData: ArrayList<FileNavBean>) {
        mRvList = findViewById(R.id.rv_list)
        mNavList = findViewById(R.id.rv_nav)
        mBtnSelectedAll = findViewById(R.id.btn_selected_all)
        mBtnConfirm = findViewById(R.id.btn_confirm)
        mBtnGoBack = findViewById(R.id.btn_go_back)
        mBtnGoBack!!.setOnClickListener(this)
        mBtnSelectedAll!!.setOnClickListener(this)
        mBtnConfirm!!.setOnClickListener(this)

        // 空视图
        mEmptyView = layoutInflater.inflate(R.layout.item_empty_view, mRvList!!.parent as ViewGroup, false)
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
        val fileListAdapter = FileListAdapter(R.layout.item_list, dataSource)
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
        val adapter = FileNavAdapter(R.layout.item_nav, dataSource)
        adapter.onItemClickListener = this
        adapter.onItemChildClickListener = this
        return adapter
    }

    /**
     * 根据被点击项的类型，触发不同的操作
     * @param adapter BaseQuickAdapter<*, *>?
     * @param view View?
     * @param position Int
     */
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        // 如果不是点击列表，则返回
        if (view!!.id != R.id.item_list) return
        val item = adapter!!.getItem(position) as FileItemBean

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        try {
            when (item.fileType) {
                DIR -> {
                    // 文件夹则进入
                    enterDirAndUpdateUI(item)
                }
                IMAGE -> {
                    intent.type = "image/*"
                    intent.data = Uri.parse(item.mFilePath)
                    startActivity(intent)
                }
                VIDEO -> {
                    intent.type = "video/*"
                    intent.data = Uri.parse(item.filePath)
                    startActivity(intent)
                }
                COMPRESSED -> {
                    val sub = item.mFileName.substring(item.mFileName.lastIndexOf("."))
                    intent.type = "application/" + sub
                    intent.data = Uri.parse(item.filePath)
                    startActivity(intent)
                }
                UNKNOWN -> {
                    Toast.makeText(this@FilePickerActivity, "我们不知道如何打开该文件", Toast.LENGTH_SHORT).show()
                }
                OCTET_STREAM -> {
                    Toast.makeText(this@FilePickerActivity, "我们不知道如何打开该文件", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (ae: ActivityNotFoundException) {
            Toast.makeText(this@FilePickerActivity, "没有应用可以打开该文件", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 通过传入的 item 配置新的列表适配器，然后更新数据集，接着更新列表
     * 从列表中时，需要获取目标文件夹在 nav 列表中的位置，如果没有则传入 -1
     * TODO 进入下一个文件夹之前，需要先清空当前的选中状态？貌似不需要
     */
    fun enterDirAndUpdateUI(iFileBean: IFileBean) {
        var pos = -1

        for (data in mNavAdapter!!.data) {
            if (data.dirPath.equals(iFileBean.filePath)) {
                pos = mNavAdapter!!.data.indexOf(data)
            }
        }

        enterDirAndUpdateUI(iFileBean, pos)
    }

    /**
     * 从导航栏中调用本方法，需要传入 pos，以便生产新的 nav adapter
     */
    fun enterDirAndUpdateUI(iFileBean: IFileBean, position: Int) {
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
            R.id.btn_nav -> {
                val item = adapter!!.getItem(position) as FileNavBean
                enterDirAndUpdateUI(item, position)
            }
            R.id.cb_list -> {
                val item = adapter!!.getItem(position) as FileItemBean
                val checkBox = view.findViewById<CheckBox>(R.id.cb_list)
                item.isChecked = checkBox.isChecked
                Toast.makeText(this@FilePickerActivity, "选中", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            // 全选
            R.id.btn_selected_all -> {
                if (mFilesIsChecked!!.get()) {
                    for (data in mListAdapter!!.data) {
                        if (FilePickerManager.isSkipDir && data.fileType == DIR) {
                            continue
                        }
                        data.isChecked = false
                    }
                    mBtnSelectedAll!!.text = "图片全选"
                } else {
                    for (data in mListAdapter!!.data) {
                        if (FilePickerManager.isSkipDir && data.fileType == DIR) {
                            continue
                        }
                        data.isChecked = true
                    }
                    mBtnSelectedAll!!.text = "取消选中"
                }

                mListAdapter!!.notifyDataSetChanged()
                mFilesIsChecked.set(!(mFilesIsChecked.get()))
            }
            R.id.btn_confirm -> {
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

                intent.putExtra(RESULT_KEY, list)
                this@FilePickerActivity.setResult(Activity.RESULT_OK, intent)
                finish()
            }
            R.id.btn_go_back -> {
                finish()
            }
        }
    }
}
