package com.demo.myapplication

import android.content.res.Resources

/**
 * created by wangwq on 2021/11/14
 */
object DisplayUtil {

    fun dp2px(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }
}