# AndroidFilePicker
[![](https://jitpack.io/v/rosuH/AndroidFilePicker.svg)](https://jitpack.io/#rosuH/AndroidFilePicker)

:bookmark: FilePicker 是一个小巧快速的文件选择器框架，以快速集成、高自定义化和可配置化为目标不断前进~

# I 简介

## 功能
1. 独立的 Activity 浏览视图
    - 通过`Intent`启动`Activity`，通过`onActivityResult()`获取返回结果
2. 文件筛选显示
    - 通过自定义文件类型来配置的文件筛选/过滤，如果你只想显示压缩文件，一行代码搞定
    - 自定义过滤器：暴露接口让你自定义过滤器
    - 可选择是否显示隐藏的文件/文件夹
    - 可选择是否选中文件夹
3. 文件多选/全选
4. 文件夹导航栏

# II 使用

*`sample`模块是使用示例*

1. `clone` 本仓库，将`filepickerlib`模块复制到你的项目文件夹中

2. 在你的项目中添加依赖

```java
    implementation project(':filepickerlib')
```

3. 开始使用

*启动*：在你的`Activity`中启动文件选择器

```java
val intent = Intent(this, FilePickerActivity::class.java)
startActivityForResult(intent, FilePickerManager.REQUEST_CODE)
```

*获取结果*：`onActivityResult`获取返回的结果，结果是所选取文件的路径列表(`ArrayList<String>()`)

```java
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FilePickerManager.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val bundle = data!!.extras
                    val list = bundle!!.getStringArrayList(FilePickerManager.RESULT_KEY)
                    // do your work...
                } else {
                    Toast.makeText(this@SampleActivity, "没有选择图片", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
```



### 细节



1. `FilePickerManager`类是配置类，你可以配置『是否显示隐藏文件』、『是否忽略选中文件夹』等选项
2. `FileFilter.selfFilter()`接口是用以让您自定义过滤器的
   - 传入的是文件列表的数据集
   - 数据集经过您的处理之后，再生出`Adapter`，绑定视图之后展示出来

# TODO

- 解耦视图和控制逻辑，为后续自定义布局铺路
- 列表项可打开，可配置打开方式
- 记住父文件夹浏览位置
- 更优雅的方式获取返回结果，`onActivityResult()`只发送通知消息，从另一容器拿到结果
- 链式调用
- 默认视图美观度提升
