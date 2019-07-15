![Banner](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/AndroidFilePicker_Banner_Dr_Sugiyama.png)

# AndroidFilePicker

[![](https://jitpack.io/v/me.rosuh/AndroidFilePicker.svg)](https://jitpack.io/#me.rosuh/AndroidFilePicker)



它没有像 Rocky，Cosmos 或是 Peppa 这样的名字。 Android File Picker 正如其名，是一个本地文件选择器框架。 他的一些特征如下所述：

 - 在 `Activity` 或 `Fragment` 中启动
    - 从一行代码开始
 - 浏览本地存储中的所有文件
    - 内置默认文件类型和文件鉴别器
    - 或者您可以自己实现文件类型
 - 自定义列表过滤器
    - 只想显示图片（或视频，音频......）？ 没问题！
    - 当然，您也可只显示文件夹
 - 自定义`item`点击事件：只需要实现监听器
 - 四个内置主题和自定义主题
 - 还有更多待您自己探索的特性（？）



|                    Rail Style（default）                     |                         Reply Style                          |                         Crane Style                          |                         Shrine Style                         |
| :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/default_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/reply_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/crane_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/shrine_theme.png) |



# 下载使用

1. 在你的项目中添加依赖

现在项目 `build.gradle` 配置文件添加仓库：

```xml
allprojects {
    repositories {
	    ...
    	maven { url 'https://jitpack.io' }
    }
}
```

然后在子模块（`app`）的配置文件添加依赖：

```xml
dependencies {
    implementation 'me.rosuh:AndroidFilePicker:latest_version'
}
```

`latest_version` 请自行替换成 [最新版本](https://github.com/rosuH/AndroidFilePicker/releases) 



# 使用

## 权限

此库需要两个权限：

- `android.permission.READ_EXTERNAL_STORAGE`
- `android.permission.WRITE_EXTERNAL_STORAGE`

如果您没有提前授予，这个库会自动申请该权限的。

## 开始使用(`Kotlin`)

简单的链式调用示意：

```java
FilePickerManager
        .from(this@SampleActivity)
        .forResult(FilePickerManager.REQUEST_CODE)
```

现在你已经起飞了🛩️...（真的只有两行）

如果使用 Java，那么仅需要加入一个`.INSTANCE` 即可使用：

```java
FilePickerManager.INSTANCE
                .from(this)
                .forResult(FilePickerManager.REQUEST_CODE);
```



## 获取结果

*获取结果*：`onActivityResult`接受消息，然后调用`FilePickerManager.obtainData()`获取保存的数据，**结果是所选取文件的路径列表(`ArrayList<String>()`)**

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
        FilePickerManager.instance.REQUEST_CODE -> {
            if (resultCode == Activity.RESULT_OK) {
                val list = FilePickerManager.instance.obtainData()
                // do your work
            } else {
                Toast.makeText(this@SampleActivity, "没有选择任何东西~", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

### 更多示例

来翻翻我写的[飞行手册](https://github.com/rosuH/AndroidFilePicker/wiki)吧？

或者想看看[主题配色](https://github.com/rosuH/AndroidFilePicker/wiki/3.-%E9%85%8D%E7%BD%AE%E9%80%89%E9%A1%B9#2-%E4%B8%BB%E9%A2%98%E5%B1%95%E7%A4%BA)？

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

[看这里](https://github.com/rosuH/AndroidFilePicker/wiki/%E9%83%A8%E5%88%86%E6%BA%90%E7%A0%81%E8%AF%B4%E6%98%8E)。

# Log

[Update Log](https://github.com/rosuH/AndroidFilePicker/wiki/%E6%9B%B4%E6%96%B0%E6%97%A5%E5%BF%97)

# TODO

[TODO](https://github.com/rosuH/AndroidFilePicker/wiki/TODO)



---

# Special Thanks To:

- [*1 @whichName](https://github.com/whichname)
- [BRVAH](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
- [Matisse](https://github.com/zhihu/Matisse)
- [默认图标作者 Shulk](http://iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.d9df05512&cid=11271)
- [主题配色](https://material.io/design/material-studies/about-our-material-studies.html)
- [Empty icon](https://github.com/rosuH/AndroidFilePicker/blob/master/filepicker/src/main/res/drawable/ic_empty_file_list_file_picker.xml) made by [freepik](https://www.freepik.com/) from www.flaticon.com
