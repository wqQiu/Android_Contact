package com.demo.myapplication.contact.adapter

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
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
class SearchContactAdapter : BaseQuickAdapter<SelectBean, BaseViewHolder>(R.layout.item_contact),LoadMoreModule {

  init {
    loadMoreModule.isEnableLoadMore = true
    loadMoreModule.preLoadNumber = 10
  }

  override fun convert(holder: BaseViewHolder, item: SelectBean) {
    val itemView = holder.itemView
    itemView.mTvName.text = item.name
    itemView.mTvPhone.text = ""
    queryPhoneFromId(itemView.context, item.id) {
      itemView.mTvPhone.text = it
    }
  }

  /**
   * 根据联系人表id查询电话号
   */
  private fun queryPhoneFromId(context: Context, id: String?, callBack: ((phone: String) -> Unit)? = null) {
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
}