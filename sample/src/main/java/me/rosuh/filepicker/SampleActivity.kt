package me.rosuh.filepicker

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
    private val fileFilter = object : AbstractFileFilter(){
        override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
            val iterator = listData.iterator()
            while (iterator.hasNext()){
                val item = iterator.next()
                // 如果是文件夹则略过
                if (item.isDir) continue
                // 判断文件类型是否是图片
                if (item.fileType !is RasterImageFileType){
                    iterator.remove()
                }
            }

            return listData
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity_main)

        val btn = findViewById<Button>(R.id.btn_jump)
        rv = findViewById(R.id.rv_main)

        btn.setOnClickListener {
            FilePickerManager.instance
                .from(this@SampleActivity)
                // 主题设置
                .setTheme(R.style.FilePickerThemeReply)
                // 自定义过滤器
                .filter(fileFilter)
                .forResult(FilePickerManager.instance.REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FilePickerManager.instance.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = FilePickerManager.instance.obtainData()
                    rv!!.adapter = SampleAdapter(R.layout.demo_item, ArrayList(list))
                    rv!!.layoutManager = LinearLayoutManager(this@SampleActivity)
                } else {
                    Toast.makeText(this@SampleActivity, "没有选择图片", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
