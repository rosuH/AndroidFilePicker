package me.rosuh.filepicker.config

/**
 *
 * @author rosu
 * @date 2018/11/22
 */
enum class StorageMediaTypeEnum {

    /**
     * 存储类型：
     * 1. 手机内部的外置存储，也就是内置 SD 卡
     * 2. 可拔插的 SD 卡
     * 3. 可拔插 U 盘
     *
     * 本来不需要传入什么媒体类型的，实际上这个参数只是用来显示在导航栏，比较直观而已
     * 在 FilePickerManager 中的另一个参数是用来自定义名字的，传入那个也是可以的
     */
    EXTERNAL_STORAGE, UUID_SD_CARD, UUID_USB_DRIVE
}