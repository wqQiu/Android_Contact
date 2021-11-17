package com.demo.myapplication.contact

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.demo.myapplication.DisplayUtil
import com.demo.myapplication.R
import java.util.*

/**
 * 通讯录分割线
 * created by wangwq on 2021/11/5
 */
class ContactItemDecoration(context: Context, val listener: OnFirstLetterListener) : RecyclerView.ItemDecoration() {

  /**
   * 悬浮栏画笔
   */
  private var mPaint: Paint? = null

  /**
   * 文字画笔
   */
  private var mTextPaint: Paint? = null

  /**
   * 吸顶高度
   */
  private val topHeight = 70

  private var fontMetrics: Paint.FontMetrics? = null

  private var textRect: Rect = Rect()

  init {
    mPaint = Paint()
    mPaint?.color = ContextCompat.getColor(context, R.color.color_eee_gray)

    mTextPaint = TextPaint()
    mTextPaint?.isAntiAlias = true
    mTextPaint?.textSize = DisplayUtil.dp2px(16f).toFloat()
    mTextPaint?.color = Color.BLACK
    mTextPaint?.isFakeBoldText = true
    mTextPaint?.textAlign = Paint.Align.LEFT

    fontMetrics = Paint.FontMetrics()
  }

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    super.getItemOffsets(outRect, view, parent, state)
    val position = parent.getChildAdapterPosition(view)
    if (position == 0 || isFirstInGroup(position)) {
      outRect.top = topHeight
    } else {
      outRect.top = 0
    }
  }

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDraw(c, parent, state)
    val left = parent.paddingLeft
    val right = parent.width - parent.paddingRight
    val childCount = parent.childCount
    for (index in 0..childCount) {
      val view = parent.getChildAt(index)
      view?.apply {
        val position = parent.getChildAdapterPosition(this)
        val textLetter = getFirstLetter(position)
        if (TextUtils.isEmpty(textLetter)) {
          val top = this.top
          mPaint?.let { p ->
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), top.toFloat(), p)
          }
        } else {
          if (isFirstInGroup(position)) {
            val top = this.top - topHeight
            val bottom = this.top
            mPaint?.let { p ->
              c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), p)
            }
            mTextPaint?.let { tp ->
              tp.getTextBounds(textLetter, 0, textLetter.length, textRect)
              c.drawText(textLetter, left.toFloat() + DisplayUtil.dp2px(15f), (bottom - topHeight / 2 + textRect.height() / 2).toFloat(), tp)
            }
          }
        }
      }
    }
  }

  override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDrawOver(c, parent, state)
    val left = parent.paddingLeft
    val right = parent.width - parent.paddingRight
    val childCount = parent.childCount
    val itemCount = state.itemCount

    var firstLetter = ""
    var lastFirstLetter = ""
    for (index in 0..childCount) {
      val view = parent.getChildAt(index)
      view?.apply {
        val position = parent.getChildAdapterPosition(this)

        lastFirstLetter = firstLetter
        firstLetter = getFirstLetter(position)
        if (!TextUtils.isEmpty(firstLetter) && firstLetter != lastFirstLetter) {
          val textLetter = firstLetter
          val bottom = this.bottom
          var textY = Math.max(topHeight, this.top)
          if (position + 1 < itemCount) {
            val nextFirstLetter = getFirstLetter(position + 1)
            if (nextFirstLetter != firstLetter && bottom < textY) {
              textY = bottom
            }
          }
          mPaint?.let { p ->
            c.drawRect(left.toFloat(), (textY - topHeight).toFloat(), right.toFloat(), textY.toFloat(), p)
          }

          mTextPaint?.let { tp ->
            tp.getTextBounds(textLetter, 0, textLetter.length, textRect)
            c.drawText(textLetter, (left + DisplayUtil.dp2px(15f)).toFloat(), (textY - topHeight / 2 + textRect.height() / 2).toFloat(), tp)
          }
        }
      }
    }
  }

  /**
   * 是否是本组第一个
   */
  private fun isFirstInGroup(position: Int): Boolean {
    if (position == RecyclerView.NO_POSITION) return false
    if (position == 0) return true
    val lastFirst = getFirstLetter(position - 1)
    val nowFirst = getFirstLetter(position)
    if (lastFirst != nowFirst) return true
    return false
  }

  /**
   * 获取首字母
   */
  private fun getFirstLetter(position: Int): String {
    if (position == RecyclerView.NO_POSITION) return ""
    val name = listener.getFirstLetter(position)
    val first = PinUtil.converterToFirstSpell(name ?: "")
    if (first.isNotEmpty()) {
      val upper = first.substring(0, 1).toUpperCase(Locale.CHINA)
      return if (upper.matches(Regex("[A-Z]"))) {
        upper
      } else {
        "#"
      }
    }
    return first
  }

  interface OnFirstLetterListener {
    fun getFirstLetter(position: Int): String?
  }
}