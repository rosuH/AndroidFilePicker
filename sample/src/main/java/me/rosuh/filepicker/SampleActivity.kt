package me.rosuh.filepicker

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.Toast
import me.rosuh.filepicker.bean.FileItemBean
import me.rosuh.filepicker.bean.FileTypeEnum.DIR
import me.rosuh.filepicker.bean.FileTypeEnum.IMAGE
import me.rosuh.filepicker.config.FileFilter
import me.rosuh.filepicker.config.FilePickerManager

class SampleActivity : AppCompatActivity(), FileFilter {
    var rv: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity_main)

        // 主题设置
        FilePickerManager.themeId = R.style.FilePickerThemeReply
        FilePickerManager.selfFilter = this

        val btn = findViewById<Button>(R.id.btn_jump)
        rv = findViewById(R.id.rv_main)

        btn.setOnClickListener {
            val intent = Intent(this, FilePickerActivity::class.java)
            startActivityForResult(intent, FilePickerManager.REQUEST_CODE)
        }
    }

    override fun selfFilter(listData: ArrayList<FileItemBean>): ArrayList<FileItemBean> {
        return ArrayList(listData.filter {
            (it.fileType == DIR) || (it.fileType == IMAGE)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FilePickerManager.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val bundle = data!!.extras
                    val list = bundle!!.getStringArrayList(FilePickerManager.RESULT_KEY)
                    rv!!.adapter = SampleAdapter(R.layout.demo_item, list)
                    rv!!.layoutManager = LinearLayoutManager(this@SampleActivity)
                } else {
                    Toast.makeText(this@SampleActivity, "没有选择图片", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
