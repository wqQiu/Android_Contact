package com.demo.myapplication.contact.adapter

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.myapplication.R
import com.fooww.module.sdk.ui.contact.SelectBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_contact.view.*
import java.lang.StringBuilder

/**
 * created by wangwq on 2021/11/5
 */
class ContactAdapter : RecyclerView.Adapter<ContactAdapter.VH>() {

  class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

  var mCursor: Cursor? = null

  //选中按钮监听
  var onCheckListener: ((bean: SelectBean, isCheck: Boolean) -> Unit)? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false))
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    val itemView = holder.itemView
    mCursor?.let { it ->
      it.moveToPosition(position)
      //姓名
      val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
      itemView.mTvName.text = name
      //电话
      itemView.mTvPhone.text = ""
      val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
      val hasPhone = it.getString(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
      if (hasPhone == "1") {
        //异步查询电话
        queryPhoneFromId(itemView.context, id) { p ->
          itemView.mTvPhone.text = p
        }
      } else {
        itemView.mTvPhone.text = ""
      }
    }
  }

  override fun getItemCount(): Int {
    return mCursor?.count ?: 0
  }

  /**
   * 根据联系人表id查询电话号
   */
  private fun queryPhoneFromId(context: Context, id: String, callBack: ((phone: String) -> Unit)? = null) {
    Observable.just(id)
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .map {
        val phone = StringBuilder()
        val phoneCursor: Cursor? = context.contentResolver.query(
          ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
          null,
          ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + it, null, null
        )
        if (phoneCursor != null) {
          while (phoneCursor.moveToNext()) {
            val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            if (!TextUtils.isEmpty(phoneNumber)) {
              phone.append("$phoneNumber,")
            }
          }
          phoneCursor.close()
        }
        return@map phone
      }
      .observeOn(AndroidSchedulers.mainThread())
      .doOnNext {
        if (it.isNotEmpty()) {
          callBack?.invoke(it.substring(0, it.length - 1).replace(" ", ""))
        } else {
          callBack?.invoke("")
        }
      }
      .doOnError {
        it.printStackTrace()
      }
      .subscribe()
  }

  /**
   * 获取当前position的名字
   */
  fun getNameFromPosition(position: Int):String{
    var name = ""
    mCursor?.let {
      it.moveToPosition(position)
      name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
    }
    return name
  }

  fun setData(cursor :Cursor){
    this.mCursor = cursor
    notifyDataSetChanged()
  }
}