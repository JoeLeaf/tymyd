package com.hygzs.tymyd.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SPUtils
import com.drake.brv.annotaion.AnimationType
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hygzs.tymyd.BaseActivity
import com.hygzs.tymyd.Data
import com.hygzs.tymyd.R
import kotlin.concurrent.thread

class SearchAccount : BaseActivity() {
    private lateinit var editText: EditText
    private lateinit var chatRecords: RecyclerView
    private lateinit var notesMap: Map<String, *>
    private lateinit var searchRecords: String
    private lateinit var searchRecordsList: MutableList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_account)
        ViewCompat.setTransitionName(
            findViewById<EditText>(R.id.editText), "search"
        )
        initView()
    }

    private fun initView() {
        searchRecords = SPUtils.getInstance("config").getString("searchRecords").toString()
        notesMap = getSPList("notes")
        chatRecords = findViewById(R.id.chatRecords)
        editText = findViewById(R.id.editText)
        findViewById<ImageView>(R.id.imageView2).setOnClickListener {
            finishAfterTransition()
        }
        thread {
            Thread.sleep(500)
            runOnUiThread {
                editText.requestFocus()
                editText.isFocusable = true
                editText.isFocusableInTouchMode = true
                KeyboardUtils.showSoftInput(editText)
                //如果有搜索记录用逗号分隔
                searchRecordsList = if (searchRecords.isNotEmpty()) {
                    searchRecords.split(",").toMutableList()
                } else {
                    Data.friendsList
                }
                refreshSearchRecords()
            }
        }
        findViewById<ImageView>(R.id.imageView).setOnClickListener {
            editText.setText("")
        }
        //editText输入监听去匹配好友
        editText.addTextChangedListener { it ->
            //如果为空就显示搜索记录
            if (it.toString().isEmpty()) {
                searchRecordsList = if (searchRecords.isNotEmpty()) {
                    searchRecords.split(",").toMutableList()
                } else {
                    Data.friendsList
                }
                refreshSearchRecords()
                return@addTextChangedListener
            }
            //改变后的文字
            val text = it.toString()
            //搜索notesMap和Data.friendsList是否有匹配的
            var searchList = mutableListOf<String>()
            //notesMap匹配键值和值
            notesMap.forEach { (key, value) ->
                if (value.toString().contains(text)) {
                    searchList.add(key)
                }
            }
            Data.friendsList.forEach {
                if (it.contains(text)) {
                    searchList.add(it)
                }
            }
            //去重
            searchList = searchList.distinct().toMutableList()
            //刷新搜索记录
            searchRecordsList = searchList
            refreshSearchRecords()
        }
    }

    //刷新搜索记录
    private fun refreshSearchRecords() {
        Log.e("小叶子 : ", searchRecordsList.toString())
        val adapter = chatRecords.linear().setup {
            addType<String>(R.layout.crony_list)
            setAnimation(AnimationType.SCALE)
            onBind {
                val cronyId = findView<TextView>(R.id.cronyId)
                val textView = findView<TextView>(R.id.textView)
                val notes = findView<TextView>(R.id.notes)
                cronyId.text = (models?.get(modelPosition) ?: "").toString()
                if (notesMap["${models?.get(modelPosition)}"].toString().isNotEmpty()
                    && notesMap["${models?.get(modelPosition)}"].toString() != "null"
                ) {
                    notes.text = notesMap["${models?.get(modelPosition)}"].toString()
                } else {
                    notes.text = getString(R.string.notes_tip)
                }
                when (cronyId.text.substring(0, 1)) {
                    "F" -> {
                        textView.text = "好友"
                    }

                    "P" -> {
                        textView.text = "NPC"
                    }

                    "S" -> {
                        textView.text = "路人"
                    }

                    "Z" -> {
                        textView.text = "阿暖"
                    }
                }
            }
            //点击打开界面
            R.id.crony_list_item.onClick {
                val targetFriend = "${models?.get(modelPosition)}"
                Data.TargetFriend = targetFriend
                Data.clickFriend = targetFriend
                finishAfterTransition()
            }
        }
        adapter.models = searchRecordsList
        adapter.animationRepeat = true
    }

    private fun getSPList(string: String): Map<String, *> {
        return SPUtils.getInstance(string).all
    }
}