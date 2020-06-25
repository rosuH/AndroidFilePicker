package me.rosuh.filepicker.config

import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.filetype.FileType

/**
 *
 * @author rosu
 * @date 2020/06/25
 * 这个类用于注册你自己的文件类型检测方法。您需要遵循下列步骤：
 * 1. 实现您自己的文件类型[FileType]，也就是其中的[FileType.verify]方法
 * 2. 构建此类的一个子类，并在[AbstractFileDetector.fillFileType] 中，检测文件类型，并赋值给[FileItemBeanImpl.fileType]属性
 * ===================================================================================================================
 * This class is used to register your own file type detection methods. You need to follow the following steps:
 * 1. implement your own file type [FileType], which is the [FileType.verify] method of the [FileType].
 * 2. Construct a subclass of this class and, in [AbstractFileDetector.fillFileType], detect the file type and assign it to [FileItemBeanImpl.fileType] property.
 *
 */
abstract class AbstractFileDetector {
    /**
     * 自定义文件类型识别方法，传入 @param itemBeanImpl 条目数据对象，
     * 由实现者来实现文件类型的甄别，返回填充了 fileType 的方法
     */
    abstract fun fillFileType(itemBeanImpl: FileItemBeanImpl): FileItemBeanImpl
}