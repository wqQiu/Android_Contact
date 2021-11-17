package com.demo.myapplication.contact

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.myapplication.R
import com.demo.myapplication.contact.adapter.ContactAdapter
import com.demo.myapplication.contact.adapter.LetterIndexAdapter
import kotlinx.android.synthetic.main.activity_contact.*

/**
 * created by wangwq on 2021/11/14
 */
class ContactActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private var mContactAdapter: ContactAdapter? = null

    private var mLetterAdapter: LetterIndexAdapter? = null

    companion object {
        fun startActivity(activity: Activity) {
            val intent = Intent(activity, ContactActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        initView()
    }

    private fun initView() {
        mRvContact.apply {
            layoutManager = LinearLayoutManager(this@ContactActivity)
            val decoration = ContactItemDecoration(
                this@ContactActivity,
                object : ContactItemDecoration.OnFirstLetterListener {
                    override fun getFirstLetter(position: Int): String? {
                        return mContactAdapter?.getNameFromPosition(position)
                    }
                })
            addItemDecoration(decoration)
            mContactAdapter = ContactAdapter()
            adapter = mContactAdapter
        }

        mRvAlphabet.apply {
            layoutManager = LinearLayoutManager(this@ContactActivity)
            mLetterAdapter = LetterIndexAdapter()
            adapter = mLetterAdapter

            mLetterAdapter?.setData(alphabetArray)

            onMoveClickListener = { position, letter ->
                if (position != -1) {
                    (mRvContact.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        position,
                        0
                    )
                }
                mTvTip.visibility = if (!TextUtils.isEmpty(letter)) View.VISIBLE else View.GONE
                mTvTip.text = letter
            }
        }

        mClSearch.setOnClickListener {
            SearchContactActivity.startActivity(this)
        }

        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        println("===onCreateLoader===")
        val uri = ContactsContract.Contacts.CONTENT_URI
        return CursorLoader(this, uri, null, null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        println("===onLoadFinished===")
        if (data == null || data.count == 0) {
            return
        }
        mContactAdapter?.setData(data)
        mRvAlphabet.setCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        println("===onLoaderReset===")
    }
}