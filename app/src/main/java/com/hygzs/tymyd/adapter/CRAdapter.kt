package com.hygzs.tymyd.adapter

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.hygzs.tymyd.R


/*
* Created by xyz on 2023/10/30
* 所以你瞅啥？
*/

class CRAdapter(private val accountList: MutableList<String>) :
    RecyclerView.Adapter<CRAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roleId: TextView = view.findViewById(R.id.roleId)
        val notes: TextView = view.findViewById(R.id.notes)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CRAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.role_list, parent, false))
    }

    override fun onBindViewHolder(holder: CRAdapter.ViewHolder, position: Int) {
        val account = accountList[position]
        holder.roleId.text = account
        if (SPUtils.getInstance("notes").contains(account)) {
            holder.notes.text = SPUtils.getInstance("notes").getString(account)
        }
    }

    override fun getItemCount(): Int {
        return accountList.size
    }

}