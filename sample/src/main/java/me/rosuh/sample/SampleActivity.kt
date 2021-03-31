package me.rosuh.sample

//import com.bumptech.glide.Glide
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.demo_activity_main.*
import me.rosuh.filepicker.adapter.FileListAdapter
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.config.*
import me.rosuh.filepicker.engine.ImageEngine
import me.rosuh.filepicker.filetype.AudioFileType
import me.rosuh.filepicker.filetype.FileType
import me.rosuh.filepicker.filetype.RasterImageFileType


class SampleActivity : AppCompatActivity() {
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
        // 单选
        btn_single.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .setTheme(getRandomTheme())
                .imageEngine(object : ImageEngine {
                    override fun loadImage(
                        context: Context?,
                        imageView: ImageView?,
                        url: String?,
                        placeholder: Int
                    ) {
                        // 应该使用 loadImage 传递过来的 context，而不是您自己的 context
                        // You should use the context passed by loadImage(), not your own context
                        Glide
                            .with(context!!)
                            .load(url)
                            .into(imageView!!)
                    }
                })
                .enableSingleChoice()
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 只展示文件夹
        btn_only_dir.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .setTheme(getRandomTheme())
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
        btn_only_image.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .setTheme(getRandomTheme())
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
        btn_display_hidden.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .setTheme(getRandomTheme())
                .showHiddenFiles(true)
                .forResult(FilePickerManager.REQUEST_CODE)
        }

        // 单选文件夹
        btn_single_dir.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .enableSingleChoice()
                .setTheme(getRandomTheme())
                .filter(object : AbstractFileFilter() {
                    override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                        return ArrayList(listData.filter { item ->
                            item.isDir
                        })
                    }
                })
                .skipDirWhenSelect(false)
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 多选文件
        btn_multi_file.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .setTheme(getRandomTheme())
                .maxSelectable(2)
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 多选文件夹
        btn_multi_dir.setOnClickListener {
            FilePickerManager
                .from(this@SampleActivity)
                .setTheme(getRandomTheme())
                .maxSelectable(2)
                .filter(object : AbstractFileFilter() {
                    override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                        return ArrayList(listData.filter { item ->
                            item.isDir
                        })
                    }
                })
                .skipDirWhenSelect(false)
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 自定义根目录
        btn_custom_root_path.setOnClickListener {
            FilePickerManager.from(this@SampleActivity)
                .storageType("下载", FilePickerConfig.STORAGE_CUSTOM_ROOT_PATH)
                .setTheme(getRandomTheme())
                // 不指定名称则为导航栏将显示绝对路径
//                .storageType(FilePickerConfig.STORAGE_CUSTOM_ROOT_PATH)
                .setCustomRootPath("/storage/emulated/0/Download")
                .forResult(FilePickerManager.REQUEST_CODE)
        }
        // 自定义文件类型
        // the new api for register your custom file type
        btn_custom_file_type.setOnClickListener {
            FilePickerManager.from(this@SampleActivity)
                .setTheme(getRandomTheme())
                .registerFileType(arrayListOf(AudioFileType()))
                .forResult(FilePickerManager.REQUEST_CODE)
        }

        findViewById<Button>(R.id.btn_show_in_fragment).setOnClickListener {
            SampleFragment.show(supportFragmentManager, "SampleFragment")
        }
    }

    private fun getRandomTheme(): Int {
        return arrayListOf(
            R.style.FilePickerThemeRail,
            R.style.FilePickerThemeCrane,
            R.style.FilePickerThemeReply,
            R.style.FilePickerThemeShrine
        ).run {
            shuffle()
            first()
        }
    }

    //<editor-fold desc="Custom File type example">
    /**
     * 自定义文件类型。通过路径或文件名来判断是否符合类型
     * Customize the file type. Determine if it matches the type by path or filename.
     */
    class CustomFileType(
        override val fileType: String = "",
        override val fileIconResId: Int = R.drawable.ic_unknown_file_picker
    ) : FileType {
        override fun verify(fileName: String): Boolean {
            return fileName.endsWith("123")
        }
    }

    /**
     * 自定义文件检测器。
     * 检测是否符合您的类型，如果是，那么填充到[FileItemBeanImpl.fileType]中
     * Custom file detector.
     * detects if it matches your type, and if so, then fill it into [FileItemBeanImpl.fileType]
     */
    internal class CustomFileDetector : AbstractFileDetector() {
        private val customFileType by lazy { CustomFileType() }

        override fun fillFileType(itemBeanImpl: FileItemBeanImpl): FileItemBeanImpl {
            // detected file type by yourself and fill your type to [FileItemBeanImpl]
            if (customFileType.verify(itemBeanImpl.fileName)) {
                itemBeanImpl.fileType = customFileType
            }
            return itemBeanImpl
        }
    }
    //</editor-fold>

    class SampleFragment : DialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_sample, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            view.findViewById<Button>(R.id.btn_go).setOnClickListener {
                FilePickerManager.from(this)
                    .setText(
                        "A",
                        "B",
                        hadSelectedStrRes = R.string.demo_test_had_selected,
                        confirmText = "C",
                        emptyListTips = "E"
                    )
                    .setItemClickListener(object : SimpleItemClickListener() {
                        override fun onItemClick(
                            itemAdapter: FileListAdapter,
                            itemView: View,
                            position: Int
                        ) {
                            super.onItemClick(itemAdapter, itemView, position)
                            Toast.makeText(
                                activity,
                                "${itemAdapter.dataList[position].fileName} was clicked",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                    .forResult(1001)
            }
        }

        companion object {
            fun show(supportFragmentManager: FragmentManager?, s: String) {
                SampleFragment().show(supportFragmentManager, s)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FilePickerManager.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = FilePickerManager.obtainData(release = true)
                    rv_main.adapter = SampleAdapter(layoutInflater, ArrayList(list))
                    rv_main.layoutManager = LinearLayoutManager(this@SampleActivity)
                } else {
                    Toast.makeText(this@SampleActivity, "没有选择图片", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
