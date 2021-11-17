package com.demo.myapplication.contact

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.AlphabetIndexer
import androidx.recyclerview.widget.RecyclerView
import com.demo.myapplication.DisplayUtil
import java.util.*

/**
 * 字母索引列表
 * created by wangwq on 2021/11/2
 */
class LetterIndexRecyclerView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

  val alphabetArray: Array<String> = arrayOf(
    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
    "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
  )

  /**
   * 每个字母所占高度
   */
  private var itemHeight = DisplayUtil.dp2px(17f)

  /**
   * 字母表索引器
   */
  private var alphabetIndexer: AlphabetIndexer? = null

  var onMoveClickListener: ((position: Int, letter: String) -> Unit)? = null

  /**
   * 记录上一次手势字母表索引
   */
  private var lastAlphabetIndex = -1

  private var mCursor: Cursor? = null

  override fun onTouchEvent(e: MotionEvent?): Boolean {
    when (e?.action) {
      MotionEvent.ACTION_DOWN -> {
        val getY = e.y
        val alphabetIndex = getNowAlphabetIndex(getY)
        if (lastAlphabetIndex != alphabetIndex) {
          lastAlphabetIndex = alphabetIndex
          val position = calculateAlphabetPosition(alphabetIndex)
          onMoveClickListener?.invoke(position, alphabetArray[alphabetIndex])
        }
      }
      MotionEvent.ACTION_MOVE -> {
        val getY = e.y
        val alphabetIndex = getNowAlphabetIndex(getY)
        if (lastAlphabetIndex != alphabetIndex) {
          lastAlphabetIndex = alphabetIndex
          val position = calculateAlphabetPosition(alphabetIndex)
          onMoveClickListener?.invoke(position, alphabetArray[alphabetIndex])
        }
      }
      MotionEvent.ACTION_UP -> {
        lastAlphabetIndex = -1
        onMoveClickListener?.invoke(-1, "")
      }
    }
    return super.onTouchEvent(e)
  }

  fun setCursor(cursor: Cursor) {
    this.mCursor = cursor
    alphabetIndexer = AlphabetIndexer(
      IndexCursor(cursor),
      cursor.getColumnIndex(ContactsContract.Contacts.SORT_KEY_PRIMARY),
      "ABCDEFGHIJKLMNOPQRSTUVWXYZZ"
    )
  }

  /**
   * 返回当前手势所在字母表索引
   */
  private fun getNowAlphabetIndex(rawY: Float): Int {
    //倍数
    val multiple: Int = (rawY / itemHeight).toInt()
    return if (multiple <= 0) 0 else if (multiple >= alphabetArray.size - 1) alphabetArray.size - 1 else multiple
  }

  /**
   * 计算字母表位置对应首条数据位置
   */
  private fun calculateAlphabetPosition(alphabetIndex: Int): Int {
    val position = alphabetIndexer?.getPositionForSection(alphabetIndex) ?: -1
    if (position == -1) return -1
    val letter = alphabetArray[alphabetIndex]
    println("===检查合理性===$position $letter")
    if (letter != "#") {
      if (position == mCursor?.count) return -1
      val sectionForPosition = alphabetIndexer?.getSectionForPosition(position) ?: -1
      if (sectionForPosition != -1) {
        val sectionLetter = alphabetArray[sectionForPosition]
        if (sectionLetter == letter) return position
      }
      return -1
    } else {
      //判断position是否返回count，如果返回说明没有#
      val all = mCursor?.count ?: position
      if (position >= all) {
        //不存在#
        return -1
      }

      //position不是count，判断最后一条是否是Z,是的话说明没有#
      val endLetter = getFirstLetter(all - 1)
      if (endLetter.matches(Regex("[A-Z]"))) {
        return -1
      }

      //二分法查找
      return search(position, all)
    }
  }

  /**
   * 查找#号首次出现的索引
   */
  private fun search(left: Int, right: Int): Int {
    if (left == right) {
      return if (!getFirstLetter(left).matches(Regex("[A-Z]"))) left else -1
    }

    val mid = (right + left) / 2
    return if (!getFirstLetter(mid).matches(Regex("[A-Z]"))) {
      search(left, mid)
    } else {
      search(mid + 1, right)
    }
  }

  /**
   * 获取首字母
   */
  private fun getFirstLetter(position: Int): String {
    mCursor?.let {
      it.moveToPosition(position)
      val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
      return PinUtil.converterToFirstSpell(name).substring(0, 1).toUpperCase(Locale.CHINA)
    }
    return ""
  }
}