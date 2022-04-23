package com.example.mailinglist.view.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mailinglist.Constants
import com.example.mailinglist.R
import com.example.mailinglist.model.MailListItem
import com.example.mailinglist.utils.MailUtil.Companion.buildAnswerEmail
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

        holder.subjectView.text = mailListItem.subject.replace(
            Regex("\\[\\w+]\\s+"),
            ""
        ) // remove mailList name, e.g. [abelana]
        holder.contentView.text = mailListItem.content
        if (mailListItem.senderName != null) holder.senderView.text =
            mailListItem.senderName else holder.senderView.visibility = View.GONE
        holder.dateView.text =
            TimeUtil.calculateElapsedTime(holder.itemView.context, mailListItem.sentDate)

        holder.expandCollapseButton.setOnClickListener {
            val expanded = mailListItem.isExpanded
            mailListItem.isExpanded = !expanded
            notifyItemChanged(position)
            if (expanded) listView?.smoothScrollToPosition(position)
        }

        holder.answerButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(mailListItem.replyToAddress))
                putExtra(Intent.EXTRA_SUBJECT, "Re: " + mailListItem.subject)
                putExtra(Intent.EXTRA_TEXT, buildAnswerEmail(mailListItem))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            holder.itemView.context.startActivity(intent)
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
        val answerButton: Button = itemView.findViewById(R.id.answerButton)
    }
}
