package me.rosuh.filepicker.config

interface ILog {
    fun d(tag: String, msg: String)
    fun e(tag: String, msg: String, tr: Throwable? = null)
    fun i(tag: String, msg: String)
    fun v(tag: String, msg: String)
    fun w(tag: String, msg: String)
    fun wtf(tag: String, msg: String)
}