![Banner](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/AndroidFilePicker_Banner_Dr_Sugiyama.png)

# Android File Picker🛩️

[![](https://jitpack.io/v/me.rosuh/AndroidFilePicker.svg)](https://jitpack.io/#me.rosuh/AndroidFilePicker)

[中文简体](https://github.com/rosuH/AndroidFilePicker/blob/master/README_CN.md)

Well, it doesn't have a name like Rocky, Cosmos or Fish. Android File Picker, like its name, is a local file selector framework. Some of his characteristics are described below:

- Launcher in Activity or Fragment
  - Start with a single line of code
- Browse and select all files in local storage
  - Built-in default file type and file discriminator
  - Or you can implement the file type yourself
- Custom list filter
  - Just want to show pictures(Or videos, audio...)?  No problem!
  - Of course, you can just display the folder
- Custom item click event: only need to implement the listener
- Apply different themes, including four built-in themes and custom themes
- More to find out yourself

|                    Rail Style（default）                     |                         Reply Style                          |                         Crane Style                          |                         Shrine Style                         |
| :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/default_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/reply_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/crane_theme.png) | ![](https://raw.githubusercontent.com/rosuH/AndroidFilePicker/master/images/shrine_theme.png) |

# Download

Gradle:

In your project `build.gradle`:

```xml
allprojects {
    repositories {
	    ...
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

Check out [releases page](https://github.com/rosuH/AndroidFilePicker/releases) to see more versions.

# Usage 📑 

## Permission

The library requires two permissions:

- `android.permission.READ_EXTERNAL_STORAGE`
- `android.permission.WRITE_EXTERNAL_STORAGE`

If you do not have permission to apply, this framework will check and apply at startup.

## Launch 🚀 (Kotlin)

```java
FilePickerManager
        .from(this@SampleActivity)
        .forResult(FilePickerManager.REQUEST_CODE)
```

Now that you have taken off 🛩️ ... ( there are really only two lines )

You only need to add `.INSTANCE` to use it:

```java
FilePickerManager.INSTANCE
                .from(this)
                .forResult(FilePickerManager.REQUEST_CODE);
```



## Receive Result

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



## More examples

[Look here](https://github.com/rosuH/AndroidFilePicker/wiki/4.-%E7%A4%BA%E4%BE%8B%E5%8F%8A%E8%A7%A3%E9%87%8A).

# Log

[Update Log](https://github.com/rosuH/AndroidFilePicker/wiki/%E6%9B%B4%E6%96%B0%E6%97%A5%E5%BF%97)

# TODO

[TODO](https://github.com/rosuH/AndroidFilePicker/wiki/TODO)



---

# Special Thanks To:

- [*1 @whichName](https://github.com/whichname)
- [BRVAH](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
- [Matisse](https://github.com/zhihu/Matisse)
- [Default Icon Author Shulk](http://iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.d9df05512&cid=11271)
- [Theme Color](https://material.io/design/material-studies/about-our-material-studies.html)
- [Empty icon](https://github.com/rosuH/AndroidFilePicker/blob/master/filepicker/src/main/res/drawable/ic_empty_file_list_file_picker.xml) made by [freepik](https://www.freepik.com/) from www.flaticon.com
