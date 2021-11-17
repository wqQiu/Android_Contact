package com.demo.myapplication.contact

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.myapplication.R
import com.demo.myapplication.contact.adapter.SearchContactAdapter
import com.fooww.module.sdk.ui.contact.SelectBean
import kotlinx.android.synthetic.main.activity_contact_search.*

/**
 * 搜索联系人
 * created by wangwq on 2021/11/5
 */
class SearchContactActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

  private var mAdapter: SearchContactAdapter? = null

  private var phoneSelection: String = ""

  private var keyword = ""
  private var lastKeyword = ""

  private val searchList: MutableList<SelectBean> = mutableListOf()

  val checkSet: HashSet<SelectBean> = hashSetOf()

  private var mCursor: Cursor? = null

  companion object {
    fun startActivity(activity: Activity) {
      val intent = Intent(activity, SearchContactActivity::class.java)
      activity.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_contact_search)

    initView()
  }

  private fun initView() {
    mRvSearch.apply {
      layoutManager = LinearLayoutManager(this@SearchContactActivity)
      mAdapter = SearchContactAdapter()
      adapter = mAdapter

      mAdapter?.loadMoreModule?.setOnLoadMoreListener {
        mCursor?.let {
          it.moveToPrevious()
          val lastPosition = it.position
          while (it.moveToNext() && it.position <= lastPosition + 50) {
            val id =
              it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
            val name =
              it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val bean = SelectBean(id, name)
            if (!searchList.contains(bean)) {
              searchList.add(bean)
            }
          }
          mAdapter?.setList(searchList)
          if (it.position == it.count) {
            mAdapter?.loadMoreModule?.loadMoreEnd()
          } else {
            mAdapter?.loadMoreModule?.loadMoreComplete()
          }
        }
      }
    }

    mEtSearch.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      }

      override fun afterTextChanged(s: Editable?) {
        keyword = s.toString()

        if (TextUtils.isEmpty(keyword)) return
        //搜索电话表
        phoneSelection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} like '%$keyword%' or ${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} like '%$keyword%'"
        LoaderManager.getInstance(this@SearchContactActivity).restartLoader(1, null, this@SearchContactActivity)
      }
    })

    mBtConfirm.visibility = View.VISIBLE
    mBtConfirm.setOnClickListener {
      finishSearch()
    }

    LoaderManager.getInstance(this).initLoader(0, null, this)
  }

  override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
    if (id == 1) {
      val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
      return CursorLoader(this, phoneUri, null, phoneSelection, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_ALTERNATIVE)
    }
    return CursorLoader(this, Uri.parse(""), null, null, null, null)
  }

  override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
    println("===onLoadFinished1===${loader.id} ${data?.count}")
    if (loader.id == 1 && keyword != lastKeyword) {
      lastKeyword = keyword
      searchList.clear()
      mCursor = data
      mCursor?.let {
        while (it.moveToNext() && it.position <= 50) {
          val id = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
          val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
          val bean = SelectBean(id, name)
          if (!searchList.contains(bean)) {
            searchList.add(bean)
          }
        }
        mAdapter?.setList(searchList)
      }
    }
  }

  override fun onLoaderReset(loader: Loader<Cursor>) {
    println("===onLoaderReset1===")
  }

  private fun finishSearch() {
    val intent = Intent()
    intent.putExtra("search", checkSet)
    setResult(RESULT_OK, intent)
    finish()
  }
}