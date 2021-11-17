package com.demo.myapplication.contact

import android.content.ContentResolver
import android.database.CharArrayBuffer
import android.database.ContentObserver
import android.database.Cursor
import android.database.DataSetObserver
import android.net.Uri
import android.os.Bundle
import java.util.*

/**
 * created by wangwq on 2021/11/1
 */
class IndexCursor(val cursor:Cursor) :Cursor {

  private var position = 0

  override fun getCount(): Int {
    return cursor.count
  }

  override fun getString(columnIndex: Int): String {
    val name = cursor.getString(columnIndex)
    val upperLetter = PinUtil.converterToFirstSpell(name).substring(0,1).toUpperCase(Locale.CHINA)
    return if (upperLetter.matches(Regex("[A-Z]"))){
      upperLetter
    }else{
      "Z"
    }
  }

  override fun moveToPosition(position: Int): Boolean {
    this.position = position
    cursor.moveToPosition(position)
    return true
  }

  override fun close() {
    cursor.close()
  }

  override fun getPosition(): Int {
    return position
  }

  override fun move(offset: Int): Boolean {
    return cursor.move(offset)
  }

  override fun moveToFirst(): Boolean {
    return cursor.moveToFirst()
  }

  override fun moveToLast(): Boolean {
    return cursor.moveToLast()
  }

  override fun moveToNext(): Boolean {
    return cursor.moveToNext()
  }

  override fun moveToPrevious(): Boolean {
    return false
  }

  override fun isFirst(): Boolean {
    return false
  }

  override fun isLast(): Boolean {
    return false
  }

  override fun isBeforeFirst(): Boolean {
    return false
  }

  override fun isAfterLast(): Boolean {
    return false
  }

  override fun getColumnIndex(columnName: String?): Int {
    return 0
  }

  override fun getColumnIndexOrThrow(columnName: String?): Int {
    return 0
  }

  override fun getColumnName(columnIndex: Int): String {
    return ""
  }

  override fun getColumnNames(): Array<String> {
    return arrayOf()
  }

  override fun getColumnCount(): Int {
    return 0
  }

  override fun getBlob(columnIndex: Int): ByteArray {
    return ByteArray(0)
  }

  override fun copyStringToBuffer(columnIndex: Int, buffer: CharArrayBuffer?) {

  }

  override fun getShort(columnIndex: Int): Short {
   return 0
  }

  override fun getInt(columnIndex: Int): Int {
    return 0
  }

  override fun getLong(columnIndex: Int): Long {
    return 0
  }

  override fun getFloat(columnIndex: Int): Float {
    return 0f
  }

  override fun getDouble(columnIndex: Int): Double {
    return 0.0
  }

  override fun getType(columnIndex: Int): Int {
    return 0
  }

  override fun isNull(columnIndex: Int): Boolean {
    return false
  }

  override fun deactivate() {

  }

  override fun requery(): Boolean {
    return false
  }

  override fun isClosed(): Boolean {
    return false
  }

  override fun registerContentObserver(observer: ContentObserver?) {

  }

  override fun unregisterContentObserver(observer: ContentObserver?) {

  }

  override fun registerDataSetObserver(observer: DataSetObserver?) {

  }

  override fun unregisterDataSetObserver(observer: DataSetObserver?) {

  }

  override fun setNotificationUri(cr: ContentResolver?, uri: Uri?) {

  }

  override fun getNotificationUri(): Uri {
    return Uri.parse("")
  }

  override fun getWantsAllOnMoveCalls(): Boolean {
    return false
  }

  override fun setExtras(extras: Bundle?) {

  }

  override fun getExtras(): Bundle {
    return Bundle()
  }

  override fun respond(extras: Bundle?): Bundle {
    return Bundle()
  }
}