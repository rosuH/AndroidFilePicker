![Banner](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/AndroidFilePicker_Banner_Dr_Sugiyama.png)

# Android File Picker🛩️

[![](https://jitpack.io/v/me.rosuh/AndroidFilePicker.svg)](https://jitpack.io/#me.rosuh/AndroidFilePicker)

如果你使用的是 `1.x` 版本，请查看 [README_1.x](./README_CN_1.x.md) 文件。

嗯，它没有像 Rocky、Cosmos 或 Fish 这样的名字。Android File Picker，正如其名，是一个本地文件选择框架。以下是它的一些特点：

- 在 Activity 或 Fragment 中启动
  - 一行代码启动
- 浏览和选择本地存储中的所有文件
  - 自定义根路径开始
  - 内置默认文件类型和文件区分器
  - 或者你可以自己实现文件类型
- 内置单选模式和多选模式。
- 自定义列表过滤器
  - 只想显示图片（或视频、音频...）？没问题！
  - 当然，你也可以只显示文件夹
- 自定义条目点击事件：只需实现监听器
- 应用不同的主题，包括四个内置主题和自定义主题
- 更多功能等你发现

|                    Rail Style（默认）                     |                         Reply Style                          |                         Crane Style                          |                         Shrine Style                         |
| :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/default_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/reply_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/crane_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/shrine_theme.png) |

## 版本兼容性
取决于你的 targetAPI。

- `targetAPI > 33`，也许你正在寻找 [照片选择器](https://developer.android.com/about/versions/14/changes/partial-photo-video-access?hl=zh-cn#media-reselection)
- `targetAPI == 33`
  - 处理[媒体权限](https://developer.android.com/training/data-storage/shared/media#access-other-apps-files)由你自己处理
  - 此库将仅显示你的应用有权限访问的媒体文件
- `targetAPI <= 33`
  - 在你的 `AndroidManifest.xml` 文件中设置 `android:requestLegacyExternalStorage="true"`
  - 由你自己处理 `android.permission.READ_EXTERNAL_STORAGE` 权限
  - 此库将显示存储中的所有文件

## 下载

[Gradle](https://docs.jitpack.io/android/#installing):

在项目的 `build.gradle` 文件中：

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
在模块的 build.gradle 文件中：

```gradle
dependencies {
    implementation 'me.rosuh:AndroidFilePicker:$latest_version'
}
```