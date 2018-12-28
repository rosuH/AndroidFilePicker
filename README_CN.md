![Banner](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/AndroidFilePicker_Banner_Dr_Sugiyama.png)

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
FilePickerManager
        .from(this@SampleActivity)
        .forResult(FilePickerManager.REQUEST_CODE)
```

现在你已经起飞了🛩️...（真的只有两行）

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

