package com.hygzs.tymyd.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hygzs.tymyd.BaseActivity
import com.hygzs.tymyd.Data
import com.hygzs.tymyd.R
import com.hygzs.tymyd.adapter.CRAdapter
import com.hygzs.tymyd.util.ReadWriteData

class ChatRecords : BaseActivity() {
    private lateinit var chatRecords: RecyclerView
    private lateinit var readWriteData: ReadWriteData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_records)
        readWriteData = ReadWriteData(this, 11, Data.app)
        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.more).setOnClickListener {
            ToastUtils.showLong("更多！多不了一点！")
        }
        chatRecords = findViewById(R.id.chat_records)
        getChatRecordData()
    }

    private fun getChatRecordData() {
        val fileList = readWriteData.getList(Data.PathName)
        val accountList = mutableListOf<String>()
        for (file in fileList) {
            //C2328362106862204481.txt
            //判断是不是C开头.txt结尾
            if (file.startsWith("C") && file.endsWith(".txt")) {
                //截取字符串
                val account = file.substring(1, file.length - 4)
                accountList.add(account)
                Log.e("小叶子 : ", account)
            }
        }
        chatRecords.linear().setup {
            addType<String>(R.layout.role_list)
            onBind {
                val roleId = findView<TextView>(R.id.roleId)
                val notes = findView<TextView>(R.id.notes)
                roleId.text = (models?.get(position) ?: "").toString()
            }
            models = accountList
        }
//        chatRecords.layoutManager = LinearLayoutManager(this)
//        chatRecords.adapter = CRAdapter(accountList)

    }
}