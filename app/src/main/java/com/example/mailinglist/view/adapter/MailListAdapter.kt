package com.example.mailinglist.view.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mailinglist.Constants
import com.example.mailinglist.R
import com.example.mailinglist.model.MailListItem
import com.example.mailinglist.utils.TimeUtil

class MailListAdapter(private val mails: List<MailListItem>) :
    RecyclerView.Adapter<MailListAdapter.ViewHolder>() {
    private var listView: RecyclerView? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mail_list_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        listView = recyclerView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mailListItem = mails[position]

        setContentViewMaxLines(holder, mailListItem)
        setExpandCollapseButtonText(holder, mailListItem)

        holder.subjectView.text = mailListItem.subject
        holder.contentView.text =
            if (mailListItem.isHtml) Html.fromHtml(mailListItem.content) else mailListItem.content
        holder.senderView.text =
            if (mailListItem.sender.personal != null) mailListItem.sender.personal else ""
        holder.dateView.text =
            TimeUtil.calculateElapsedTime(holder.itemView.context, mailListItem.sentDate)

        holder.expandCollapseButton.setOnClickListener {
            val expanded = mailListItem.isExpanded
            mailListItem.isExpanded = !expanded
            notifyItemChanged(position)
            listView?.smoothScrollToPosition(position)
        }
    }

    private fun setContentViewMaxLines(
        holder: ViewHolder,
        mailListItem: MailListItem
    ) {
        holder.contentView.maxLines =
            if (mailListItem.isExpanded) Int.MAX_VALUE else Constants.CARD_MAX_LINES
    }

    private fun setExpandCollapseButtonText(
        holder: ViewHolder,
        mailListItem: MailListItem
    ) {
        holder.expandCollapseButton.text =
            if (mailListItem.isExpanded) holder.itemView.context.resources.getString(
                R.string.collapse
            ) else holder.itemView.context.resources.getString(
                R.string.expand
            )
    }

    override fun getItemCount(): Int {
        return mails.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val subjectView: TextView = itemView.findViewById(R.id.subjectView)
        val contentView: TextView = itemView.findViewById(R.id.contentView)
        val senderView: TextView = itemView.findViewById(R.id.senderView)
        val dateView: TextView = itemView.findViewById(R.id.dateView)
        val expandCollapseButton: Button = itemView.findViewById(R.id.expandCollapseButton)
    }
}
