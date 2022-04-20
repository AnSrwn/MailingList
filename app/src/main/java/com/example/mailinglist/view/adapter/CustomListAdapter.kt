package com.example.mailinglist.view.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mailinglist.R
import com.example.mailinglist.model.Mail

class CustomListAdapter(private val mails: List<Mail>) :
    RecyclerView.Adapter<CustomListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_list_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mail = mails[position]

        holder.subjectView.text = mail.subject
        holder.contentView.text = if (mail.isHtml) Html.fromHtml(mail.content) else mail.content
        holder.senderView.text = if (mail.sender.personal != null) mail.sender.personal else ""
        holder.dateView.text = mail.sentDate.toString()
    }

    override fun getItemCount(): Int {
        return mails.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val subjectView: TextView = itemView.findViewById(R.id.subjectView)
        val contentView: TextView = itemView.findViewById(R.id.contentView)
        val senderView: TextView = itemView.findViewById(R.id.senderView)
        val dateView: TextView = itemView.findViewById(R.id.dateView)
    }
}
