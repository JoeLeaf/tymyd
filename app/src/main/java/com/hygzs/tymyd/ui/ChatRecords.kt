package com.hygzs.tymyd.ui


import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemSwipe
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hss01248.dialog.StyledDialog
import com.hss01248.dialog.adapter.SuperLvHolder
import com.hss01248.dialog.interfaces.MyDialogListener
import com.hygzs.tymyd.BaseActivity
import com.hygzs.tymyd.Data
import com.hygzs.tymyd.R
import com.hygzs.tymyd.util.ReadWriteData
import com.hygzs.tymyd.util.SQLite3Helper
import kotlin.math.log

class ChatRecords : BaseActivity() {
    private lateinit var chatRecords: RecyclerView
    private lateinit var readWriteData: ReadWriteData
    private var itemTouchHelper1: ItemTouchHelper? = null
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
        getAccountList()
    }

    private fun getFriends(name: String) {
        val friendsBytes = readWriteData.read(Data.PathName, name)
        FileIOUtils.writeFileFromBytesByStream(
            PathUtils.getInternalAppFilesPath() + "/${name.replace("txt", "db")}",
            friendsBytes
        )
        val dbPath = PathUtils.getInternalAppFilesPath() + "/${name.replace("txt", "db")}"
        val db = SQLite3Helper(this, dbPath)
        val friendsList = db.getAllTableNames()
        //清理chatRecords数据和事件
        itemTouchHelper1?.attachToRecyclerView(null)

        chatRecords.linear().setup {
            addType<String>(R.layout.crony_list)
            onCreate {
            }
            onBind {
                val cronyId = findView<TextView>(R.id.cronyId)
                val textView = findView<TextView>(R.id.textView)
                val notes = findView<TextView>(R.id.notes)
                cronyId.text = (models?.get(modelPosition) ?: "").toString()
                if (SPUtils.getInstance("notes").getString("${models?.get(modelPosition)}").isNotEmpty()) {
                    notes.text = SPUtils.getInstance("notes").getString("${models?.get(modelPosition)}")
                }
                when (cronyId.text.substring(0, 1)) {
                    "F" -> {
                        textView.text = "好友"
                    }

                    "P" -> {
                        textView.text = "NPC"
                    }

                    "S" -> {
                        textView.text = "陌生人"
                    }

                    "Z" -> {
                        textView.text = "阿暖"
                    }
                }
            }
        }.models = friendsList
    }

    private fun getAccountList() {
        val fileList = readWriteData.getList(Data.PathName)
        val accountList = mutableListOf<String>()
        itemTouchHelper1?.attachToRecyclerView(null)
        if (fileList.isEmpty()) {
            ToastUtils.showLong("没有查询到任何一个账号哦~")
            return
        }
        for (file in fileList) {
            //C2328362106862204481.txt
            //判断是不是C开头.txt结尾
            if (file.startsWith("C") && file.endsWith(".txt")) {
                //截取字符串
                val account = file.substring(1, file.length - 4)
                accountList.add(account)
            }
        }
        chatRecords.linear().setup {
            addType<String>(R.layout.role_list)
            onBind {
                val roleId = findView<TextView>(R.id.roleId)
                val notes = findView<TextView>(R.id.notes)
                roleId.text = (models?.get(modelPosition) ?: "").toString()
                if (SPUtils.getInstance("notes").getString("${models?.get(modelPosition)}").isNotEmpty()) {
                    notes.text = SPUtils.getInstance("notes").getString("${models?.get(modelPosition)}")
                }
            }
            //点击进入聊天记录
            R.id.role_list_item.onClick {
                getFriends("C${models?.get(modelPosition)}.txt")
            }
            //长按复制
            R.id.role_list_item.onLongClick {
                StyledDialog.buildIosAlert(
                    "小叶子的提示",
                    "是否复制这个账号？",
                    object : MyDialogListener() {
                        override fun onFirst() {
                            ClipboardUtils.copyText("${models?.get(modelPosition)}")
                            ToastUtils.showLong("复制成功")
                        }

                        override fun onSecond() {
                        }
                    }).setBtnText("确定", "取消").show()
            }
            val itemTouchHelperCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.layoutPosition
                    ToastUtils.showLong("删除了${viewHolder.layoutPosition}")
                    if (direction == ItemTouchHelper.LEFT) {
                        StyledDialog.buildIosAlert("小叶子的提示",
                            "是否确定要删除这个账号的所有聊天记录？",
                            object : MyDialogListener() {
                                override fun onFirst() {
                                    readWriteData.delete(
                                        Data.PathName, "C${models?.get(position)}.txt"
                                    )
                                    accountList.removeAt(position)
                                    notifyItemRemoved(position)
                                }
                                override fun onSecond() {
                                    notifyItemChanged(position)
                                }
                            }).setBtnText("确定", "取消").show()
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        notifyItemChanged(position)
                        //不引用layout文件，直接在代码中写
                        val editText = EditText(this@ChatRecords)
                        AlertDialog.Builder(this@ChatRecords).setTitle("给爷输入备注")
                            .setView(editText).setPositiveButton("确定") { _, _ ->
                                val notes = editText.text.toString()
                                if (notes.isNotEmpty()) {
                                    SPUtils.getInstance("notes")
                                        .put("${models?.get(position)}", notes)
                                    //刷新界面
                                    notifyItemChanged(position)
                                } else {
                                    SPUtils.getInstance("notes").put(
                                        "${models?.get(position)}",
                                        "向右滑动修改备注，向左滑动删除，长按复制或者其他功能"
                                    )
                                    //刷新界面
                                    notifyItemChanged(position)
                                }
                            }.setNegativeButton("取消") { _, _ ->
                            }.create().show()
                    }
                }
            }
            itemTouchHelper1 = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper1?.attachToRecyclerView(chatRecords)
        }.models = accountList
    }
    //双击退出软件
    private var exitTime: Long = 0
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //如果在好友列表界面则返回账号列表界面，否则双击退出软件
        if (chatRecords.adapter?.getItemViewType(0) == R.layout.crony_list) {
            getAccountList()
        }else{
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.showLong("再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
        }
    }
}