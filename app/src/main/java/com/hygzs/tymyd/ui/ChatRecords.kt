package com.hygzs.tymyd.ui


import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.drake.brv.annotaion.AnimationType
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hss01248.dialog.StyledDialog
import com.hss01248.dialog.interfaces.MyDialogListener
import com.hygzs.tymyd.BaseActivity
import com.hygzs.tymyd.Data
import com.hygzs.tymyd.R
import com.hygzs.tymyd.util.ReadWriteData
import com.hygzs.tymyd.util.SQLite3Helper
import okhttp3.OkHttpClient
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import kotlin.concurrent.thread

class ChatRecords : BaseActivity() {
    private lateinit var chatRecords: RecyclerView
    private lateinit var readWriteData: ReadWriteData
    private var itemTouchHelper1: ItemTouchHelper? = null
    private lateinit var notesMap: Map<String, *>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_records)
        readWriteData = ReadWriteData(this, 11, Data.app)
        notesMap = getSPList("notes")
        upCheck()
        initView()
    }

    private fun initView() {
        chatRecords = findViewById(R.id.chat_records)
        findViewById<TextView>(R.id.textView5).setOnClickListener {
            ToastUtils.showLong("你点啥！有这个打算！但是还没写！")
        }
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
        var friendsList = db.getAllTableNames()
        //转为 mutableListOf<String>()
        friendsList = GsonUtils.fromJson<List<String>>(
            friendsList.toString(),
            List::class.java
        ) as MutableList<String>

        //清理chatRecords数据和事件
        itemTouchHelper1?.attachToRecyclerView(null)

        val adapter = chatRecords.linear().setup {
            addType<String>(R.layout.crony_list)
            setAnimation(AnimationType.SCALE)
            onCreate {
            }
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
                val i = Intent(this@ChatRecords, ChatInterface::class.java)
                startActivity(
                    i,
                    ActivityOptions.makeSceneTransitionAnimation(this@ChatRecords).toBundle()
                )
            }
            //长按复制
            R.id.crony_list_item.onLongClick {
                StyledDialog.buildIosAlert(
                    "小叶子的提示",
                    "是否复制对方ID？",
                    object : MyDialogListener() {
                        override fun onFirst() {
                            //删除第一位然后复制
                            ClipboardUtils.copyText("${models?.get(modelPosition)}".substring(1))
                            ToastUtils.showLong("复制成功")
                        }

                        override fun onSecond() {
                        }
                    }).setBtnText("确定", "取消").show()
            }
            //拖动事件
            val itemTouchHelperCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    //不需要拖动
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.layoutPosition
                    if (direction == ItemTouchHelper.LEFT) {
                        notifyItemChanged(position)
                        StyledDialog.buildIosAlert("小叶子的提示",
                            "是否确定要删除这个好友的所有聊天记录？",
                            object : MyDialogListener() {
                                override fun onFirst() {
                                    db.dropTable("${models?.get(position)}")
                                    updateDbFile(dbPath)
                                    friendsList.removeAt(position)
                                    ToastUtils.showLong("删除成功~")
                                    notifyItemRemoved(position)
                                }

                                override fun onSecond() {
//                                    notifyItemChanged(position)
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
                                    notesMap = getSPList("notes")
                                    //刷新界面
                                    notifyItemChanged(position)
                                } else {
                                    SPUtils.getInstance("notes").put(
                                        "${models?.get(position)}",
                                        getString(R.string.notes_tip)
                                    )
                                    notesMap = getSPList("notes")
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
        }
        adapter.models = friendsList
        adapter.animationRepeat = true
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
                if (notesMap["${models?.get(modelPosition)}"].toString()
                        .isNotEmpty() && notesMap["${models?.get(modelPosition)}"].toString() != "null"
                ) {
                    notes.text =
                        notesMap["${models?.get(modelPosition)}"].toString()
                } else {
                    notes.text = getString(R.string.notes_tip)
                }
            }
            //点击进入聊天记录
            R.id.role_list_item.onClick {
                val targetAccount = "${models?.get(modelPosition)}"
                Log.e("小叶子 : ", targetAccount)
                Data.TargetAccount = targetAccount
                getFriends("C$targetAccount.txt")
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
                    if (direction == ItemTouchHelper.LEFT) {
                        notifyItemChanged(position)
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
                        val editText = EditText(this@ChatRecords)
                        AlertDialog.Builder(this@ChatRecords).setTitle("给爷输入备注")
                            .setView(editText).setPositiveButton("确定") { _, _ ->
                                val notes = editText.text.toString()
                                if (notes.isNotEmpty()) {
                                    SPUtils.getInstance("notes")
                                        .put("${models?.get(position)}", notes)
                                    notesMap = getSPList("notes")
                                    //刷新界面
                                    notifyItemChanged(position)
                                } else {
                                    SPUtils.getInstance("notes").put(
                                        "${models?.get(position)}",
                                        getString(R.string.notes_tip)
                                    )
                                    notesMap = getSPList("notes")
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


    //更新检测
    private fun upCheck() {
        thread {
            try {
                val okHttpClient = OkHttpClient()
                val request = okhttp3.Request.Builder()
                    .url("https://hygzs.xyz/td/version.json")
                    .build()
                val response = okHttpClient.newCall(request).execute()
                val body = response.body?.string()
                val jsonObject = JSONObject(body)
                val version = jsonObject.getString("a")
                val updateLog = jsonObject.getString("b")
                val url = jsonObject.getString("c")
                val versionCode = version.replace(".", "").toInt()
                val selfVersionCode = AppUtils.getAppVersionName().replace(".", "").toInt()
                if (versionCode > selfVersionCode) {
                    runOnUiThread {
                        StyledDialog.buildIosAlert("小叶子的提示",
                            "发现新版本：$version\n\n更新日志：$updateLog",
                            object : MyDialogListener() {
                                override fun onFirst() {
                                    //启动浏览器下载
                                    val intent = Intent()
                                    intent.action = "android.intent.action.VIEW"
                                    intent.data = Uri.parse(url)
                                    startActivity(intent)
                                }

                                override fun onSecond() {
                                    finishAffinity()
                                }
                            }).setBtnText("更新", "取消").show()
                    }
                }
            } catch (e: SecurityException) {
                ToastUtils.showLong("!!!我网络权限呢！！")
                finishAffinity()
            } catch (e: JSONException) {
                ToastUtils.showLong("json解析错误")
            } catch (e: IOException) {
                ToastUtils.showLong("网络错误~或者服务器炸了！")
            } catch (e: Exception) {
                ToastUtils.showLong("未知错误")
            }
        }
    }

    //写一个方法用来把修改后的db文件写入到手机内部存储
    private fun updateDbFile(dbPath: String) {
        val dbBytes = FileIOUtils.readFile2BytesByStream(dbPath)
        val fileName = dbPath.split("/").last().replace("db", "txt")
        readWriteData.write(Data.PathName, fileName, null, dbBytes)
    }

    private fun getSPList(string: String): Map<String, *> {
        return SPUtils.getInstance(string).all
    }

    //双击退出软件
    private var exitTime: Long = 0

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //如果在好友列表界面则返回账号列表界面，否则双击退出软件
        if (chatRecords.adapter?.getItemViewType(0) == R.layout.crony_list) {
            getAccountList()
        } else {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.showLong("再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
        }
    }
}