package me.rosuh.sample

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.Toast
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.config.AbstractFileFilter
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.filetype.RasterImageFileType


class SampleActivity : AppCompatActivity() {
    private var rv: RecyclerView? = null

    /**
     * 自定义文件过滤器
     */
    private val fileFilter = object : AbstractFileFilter() {
        override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
            val iterator = listData.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                // 如果是文件夹则略过
                if (item.isDir) continue
                // 判断文件类型是否是图片
                if (item.fileType !is RasterImageFileType) {
                    iterator.remove()
                }
            }

            return listData
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity_main)

        rv = findViewById(R.id.rv_main)

        val defaultBtn = findViewById<Button>(R.id.btn_default)
        val singleBtn = findViewById<Button>(R.id.btn_single)
        val onlyDirBtn = findViewById<Button>(R.id.btn_only_dir)
        val onlyImgBtn = findViewById<Button>(R.id.btn_only_image)
        val displayHiddenBtn = findViewById<Button>(R.id.btn_display_hidden)
        val singleFileBtn = findViewById<Button>(R.id.btn_single_file)
        val singleDirBtn = findViewById<Button>(R.id.btn_single_dir)
        val multiFilesBtn = findViewById<Button>(R.id.btn_multi_file)
        val multiDirsBtn = findViewById<Button>(R.id.btn_multi_dir)

        // 下面随机切换主题

        // 默认状态
        defaultBtn.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 单选
        singleBtn.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .setTheme(R.style.FilePickerThemeRail)
                .maxSelectable(1)
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 只展示文件夹
        onlyDirBtn.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .setTheme(R.style.FilePickerThemeShrine)
                .filter(object : AbstractFileFilter() {
                    override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                        return ArrayList(listData.filter { item ->
                            item.isDir
                        })
                    }
                })
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 只展示图片
        onlyImgBtn.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .filter(object : AbstractFileFilter() {
                    override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                        return ArrayList(listData.filter { item ->
                            ((item.isDir) || (item.fileType is RasterImageFileType))
                        })
                    }
                })
                .forResult(FilePickerManager.REQUEST_CODE)
        }

        // 显示隐藏文件，. 符号开头的
        displayHiddenBtn.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .showHiddenFiles(true)
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 单选文件
        singleFileBtn.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .maxSelectable(1)
                .showCheckBox(false)
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 单选文件夹
        singleDirBtn.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .maxSelectable(1)
                .filter(object : AbstractFileFilter() {
                    override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                        return ArrayList(listData.filter { item ->
                            item.isDir
                        })
                    }
                })
                .skipDirWhenSelect(false)
                .setTheme(R.style.FilePickerThemeReply)
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 多选文件
        multiFilesBtn.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .setTheme(R.style.FilePickerThemeCrane)
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 多选文件夹
        multiDirsBtn.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .filter(object : AbstractFileFilter() {
                    override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                        return ArrayList(listData.filter { item ->
                            item.isDir
                        })
                    }
                })
                .skipDirWhenSelect(false)
                .setTheme(R.style.FilePickerThemeShrine)
                .forResult(FilePickerManager.REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FilePickerManager.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = FilePickerManager.obtainData()
                    rv!!.adapter = SampleAdapter(layoutInflater, ArrayList(list))
                    rv!!.layoutManager = LinearLayoutManager(this@SampleActivity)
                } else {
                    Toast.makeText(this@SampleActivity, "没有选择图片", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
