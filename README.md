![Banner](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/dev/Android_FilePicker_Banner.png)

# AndroidFilePicker

[![](https://jitpack.io/v/me.rosuh/AndroidFilePicker.svg)](https://jitpack.io/#me.rosuh/AndroidFilePicker)

# I 简介

🔖 FilePicker 是一个小巧快速的文件选择器框架，以快速集成、高自定义化和可配置化为目标不断前进~🚩


# II 使用

1. 在你的项目中添加依赖

```xml
allprojects {
    repositories {
	    ...
    	maven { url 'https://jitpack.io' }
    }
}
```

```xml
dependencies {
    implementation 'me.rosuh:AndroidFilePicker:latest_version'
}
```

`latest_version` 请自行替换成最新版本

2. 开始使用(`Kotlin`)

简单的链式调用示意：

```java
FilePickerManager.instance
                .from(this@SampleActivity)
                // 主题设置
                .setTheme(R.style.FilePickerThemeReply)
                // 自定义过滤器(可选)
                .filter(fileFilter)
                .forResult(FilePickerManager.instance.REQUEST_CODE)
...

/**
* 自定义文件过滤器，此处使用库默认的文件类型
*/
private val fileFilter = object : AbstractFileFilter(){
	override fun doFilter(listData: ArrayList<FileItemBean>): ArrayList<FileItemBean> {
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

...

```

*获取结果*：`onActivityResult`接受消息，然后调用`FilePickerManager.obtainData()`获取保存的数据，结果是所选取文件的路径列表(`ArrayList<String>()`)

```java
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
```



## 功能 & 特点

1. 链式调用
2. 默认选中实现
   - 点击条目(`item`)无默认实现
   - 点击`CheckBox`为选中
   - 长按条目为更改选中状态：选中/取消选中
3. 内置四种主题配色 + 可自定义配色
   - 查看主题颜色示意图，然后调用`setTheme()`传入自定义主题
4. 默认实现多种文件类型
   - 实现`IFileType`接口来实现你的文件类型
   - 实现`AbstractFileType`抽象类来实现你的文件类型甄别器
5. 公开文件过滤接口
   - 实现`AbstractFileFilter`抽象类来定制你自己的文件过滤器，这样可以控制文件列表的展示内容
6. 多种可配置选项
   1. 选中时是否忽略文件夹
   2. 是否显示隐藏文件夹（以符号`.`开头的，视为隐藏文件或隐藏文件夹）
   3. 可配置导航栏的文本，默认显示、多选文本、取消选择文本以及根目录默认名称
7. 公开条目(`item`)选择监听器，可自定义条目被点击的实现

### 部分源码说明

1. 包和文件夹

   - `adapter`包：两个列表（导航栏和文件列表）的数据适配器类

   - `bean`包：所有用到的`Model`类

     - `IFileBean`是文件对象所需要实现的接口

   - `config`：管理类、配置类所在

     - `AbstractFileFilter`：文件过滤器抽象类，用于给调用者自实现文件过滤器
     - `AbstractFileType`：文件类型抽象类，用于给调用者自实现自己的文件类型
       - 其中的抽象函数`fillFileType`为文件甄别器，如果你实现了自己的文件类型，那么最好也要实现自己的文件甄别器
     - `DefaultFileType`：默认文件类型，文件类型类的默认实现，里面实现了默认的文件甄别器

   - `filetype`：一些默认实现的文件类型

     - 实现接口`IFileType`以实现自己的文件类型

   - `utils`：一些工具类

     - `FileUtils`类包含了文件相关的大部分所需的函数
     - `PercentLayoutUtils`、`PercentTextView`是`TextView`的相对布局实现（*1）

# Log

## <0.2.0>_2018-11-27  

:recycle: :art: :rocket: :memo:

### Add

- 链式调用
- 添加文件类型抽象类
    - 公开文件类型接口
- 添加文件过滤抽象类
    - 公开文件过滤器接口
- 公开条目点击接口，可以自实现条目点击效果
- 添加界面字符串自定义功能
- 返回键返回上层目录功能
- 添加 FilePickerConfig 类保存配置
- 新增四种主题配色

### Update

- 文件类型可由调用者自己实现，也可以使用默认实现
- FileItemBean 添加图标资源变量，支持自定义类型图标
- 调用 FilePickerManager.obtainData() 获取数据，Intent 仅作消息发送功能
- 更新部分文件类型默认 icon
- README


# TODO

- [x] 列表项可打开，可配置打开方式 
- [x] 更优雅的方式获取返回结果，`onActivityResult()`只发送通知消息，从另一容器拿到结果
- [x] 默认视图美观度提升
- [x] 链式调用 
- [ ] 记住父文件夹浏览位置
- [ ] 解耦视图和控制逻辑，为后续自定义布局铺路



---

# Special Thanks To:

- [*1 @whichName](https://github.com/whichname)
- [BRVAH](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
- [Matisse](https://github.com/zhihu/Matisse)
- [默认图标作者 Shulk](http://iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.d9df05512&cid=11271)
- [主题配色](https://material.io/design/material-studies/about-our-material-studies.html)

