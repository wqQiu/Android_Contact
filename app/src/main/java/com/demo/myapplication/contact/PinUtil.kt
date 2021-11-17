package com.demo.myapplication.contact

import android.text.TextUtils
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

/**
 * created by wangwq on 2021/11/14
 */
object PinUtil {

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    fun converterToFirstSpell(chines: String): String {
        var chines = chines
        if (TextUtils.isEmpty(chines)) {
            return ""
        }
        chines = trimSpecialCharacter(chines)
        val pinyinName = StringBuilder()
        val nameChar = chines.toCharArray()
        val defaultFormat = HanyuPinyinOutputFormat()
        defaultFormat.caseType = HanyuPinyinCaseType.UPPERCASE
        defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE
        for (aNameChar in nameChar) {
            if (aNameChar.toInt() > 128) {
                try {
                    val pinyinArray =
                        PinyinHelper.toHanyuPinyinStringArray(aNameChar, defaultFormat)
                    if (pinyinArray != null && pinyinArray.isNotEmpty()) {
                        pinyinName.append(pinyinArray[0][0])
                    }
                } catch (e: BadHanyuPinyinOutputFormatCombination) {
                    e.printStackTrace()
                }

            } else {
                pinyinName.append(aNameChar)
            }
        }
        return pinyinName.toString()
    }

    private fun trimSpecialCharacter(str: String): String {
        return str.replace(
            "[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“。，、？_\\-～]".toRegex(),
            ""
        ).trim { it <= ' ' }
    }
}