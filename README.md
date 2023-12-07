![Banner](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/AndroidFilePicker_Banner_Dr_Sugiyama.png)

# Android File Picker🛩️

[![](https://jitpack.io/v/me.rosuh/AndroidFilePicker.svg)](https://jitpack.io/#me.rosuh/AndroidFilePicker)

[中文简体](./README_CN.md)

If you're using `1.x` version, checkout the [README_1.x](./README_1.x.md) file.

Well, it doesn't have a name like Rocky, Cosmos or Fish. Android File Picker, like its name, is a local file selector framework. Some of his characteristics are described below:

- Launcher in Activity or Fragment
  - Start with a single line of code
- Browse and select all files in local storage
  - Custom Root path to start
  - Built-in default file type and file discriminator
  - Or you can implement the file type yourself
- Built in Single Choice mode and Multiple Choice mode.
- Custom list filter
  - Just want to show pictures(Or videos, audio...)?  No problem!
  - Of course, you can just display the folder
- Custom item click event: only need to implement the listener
- Apply different themes, including four built-in themes and custom themes
- More to find out yourself

|                    Rail                     |                         Reply                          |                         Crane                          |                         Shrine                         |
| :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/default_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/reply_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/crane_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/shrine_theme.png) |

## Version Compatibility
It depends on your targetAPI.

- `targetAPI > 33`, may be you are finding [photo picker](https://developer.android.com/about/versions/14/changes/partial-photo-video-access?hl=zh-cn#media-reselection)
- `targetAPI == 33`
  - Handle [media permissions](https://developer.android.com/training/data-storage/shared/media#access-other-apps-files) by your onw 
  - This lib will only show media files which your app has permission to access
- `targetAPI <= 33`
  - Set `android:requestLegacyExternalStorage="true"` in your `AndroidManifest.xml` file
  - Handler `android.permission.READ_EXTERNAL_STORAGE` permission by your own
  - This lib will show all files in your storage

## Download

[Gradle](https://docs.jitpack.io/android/#installing):

In your project `build.gradle`:

```xml
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

In your module `build.gradle`:

```xml
dependencies {
    implementation 'me.rosuh:AndroidFilePicker:$latest_version'
}
```
This lib now support AndroidX, check the version below.

Check out [releases page](https://github.com/rosuH/AndroidFilePicker/releases) to see more versions.

## Usage 📑 

### Permission

You should request permission by yourself, this lib will not request permission for you.
See [Version Compatibility](#version-compatibility) for more details.

### Launch 🚀 

```kotlin
FilePickerManager
        .from(context)
        .forResult(FilePickerManager.REQUEST_CODE)
```



### Receive Result

In `onActivityResult()` callback of the starting `Activity` or `Fragment`:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
        FilePickerManager.instance.REQUEST_CODE -> {
            if (resultCode == Activity.RESULT_OK) {
                val list = FilePickerManager.instance.obtainData()
                // do your work
            } else {
                Toast.makeText(this@SampleActivity, "You didn't choose anything~", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

The result is a path list of the selected file (`ArrayList<String>()`).


## Docs

- [Source Code Explanation](https://github.com/rosuH/AndroidFilePicker/wiki/4.-%E7%A4%BA%E4%BE%8B%E5%8F%8A%E8%A7%A3%E9%87%8A).

- [Change Log](https://github.com/rosuH/AndroidFilePicker/wiki/Change-Log)

- [TODO](https://github.com/rosuH/AndroidFilePicker/wiki/TODO)



---

## Special Thanks To:

- [whichName](https://github.com/whichname)
- [Matisse](https://github.com/zhihu/Matisse)
- [Default Icon Author Shulk](http://iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.d9df05512&cid=11271)
- [Theme Color](https://material.io/design/material-studies/about-our-material-studies.html)
- [Empty icon](https://github.com/rosuH/AndroidFilePicker/blob/master/filepicker/src/main/res/drawable/ic_empty_file_list_file_picker.xml) made by [freepik](https://www.freepik.com/) from www.flaticon.com
