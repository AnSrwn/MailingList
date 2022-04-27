package com.example.mailinglist.ui.shared.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.mailinglist.R
import com.example.mailinglist.domain.model.MailListItem
import com.example.mailinglist.shared.Constants
import com.example.mailinglist.shared.utils.TimeUtil
import com.example.mailinglist.ui.shared.SnapToPositionHelper
import java.util.*


class MailListAdapter(private val mailListItems: MutableList<MailListItem>) :
    RecyclerView.Adapter<MailListAdapter.ViewHolder>() {
    private var mailListView: RecyclerView? = null

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val subjectView: TextView = itemView.findViewById(R.id.subjectView)
        val imageGalleryView: RecyclerView = itemView.findViewById(R.id.imageGallery)
        val contentView: TextView = itemView.findViewById(R.id.contentView)
        val senderView: TextView = itemView.findViewById(R.id.senderView)
        val dateView: TextView = itemView.findViewById(R.id.dateView)
        val expandCollapseButton: Button = itemView.findViewById(R.id.expandCollapseButton)
        val answerButton: Button = itemView.findViewById(R.id.answerButton)
        val contentLayout: LinearLayout = itemView.findViewById(R.id.contentLayout)
    }

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
        mailListView = recyclerView
    }

    override fun getItemCount(): Int {
        return mailListItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mailListItem = mailListItems[position]

        bindSubject(holder.subjectView, mailListItem.subject)
        bindSenderName(holder.senderView, mailListItem.senderName)
        bindContent(
            holder.contentView,
            holder.contentLayout,
            mailListItem.content,
            mailListItem.isExpanded
        )
        bindImages(holder.imageGalleryView, mailListItem.images)
        bindDate(holder.dateView, mailListItem.receivedDate)

        bindExpandCollapseButton(
            holder.expandCollapseButton,
            mailListItem,
            position,
            holder.contentView
        )
        bindAnswerButton(holder.answerButton, mailListItem)
    }

    fun addItems(items: List<MailListItem>) {
        val size = mailListItems.size
        mailListItems.addAll(items)
        val sizeNew = mailListItems.size
        notifyItemChanged(size, sizeNew)
    }

    private fun bindSubject(subjectView: TextView, subject: String) {
        subjectView.text = subject
    }

    private fun bindSenderName(senderView: TextView, senderName: String?) {
        if (senderName != null) senderView.text =
            senderName else senderView.visibility = View.GONE
    }

    private fun bindContent(
        contentView: TextView,
        contentLayout: LinearLayout,
        content: String,
        isExpanded: Boolean
    ) {
        contentView.maxLines =
            if (isExpanded) Int.MAX_VALUE else Constants.CARD_MAX_LINES

        contentView.text = content

        contentView.doOnPreDraw {
            // not optimal, because one-lines are getting removed
            // but so far there is no other way to remove empty content
            if (contentView.lineCount <= 1) {
                contentLayout.visibility = View.GONE
            }
        }
    }

    private fun bindImages(imageGalleryView: RecyclerView, images: List<Bitmap>) {
        if (images.isNotEmpty()) {
            imageGalleryView.visibility = View.VISIBLE
            prepareImageGalleryView(imageGalleryView)

            val adapter = ImageGalleryAdapter(images)
            imageGalleryView.adapter = adapter
        } else {
            imageGalleryView.visibility = View.GONE
        }
    }

    private fun prepareImageGalleryView(imageGalleryView: RecyclerView) {
        val layoutManager =
            LinearLayoutManager(imageGalleryView.context, LinearLayoutManager.HORIZONTAL, false)

        imageGalleryView.layoutManager = layoutManager
        imageGalleryView.itemAnimator = null

        if (imageGalleryView.onFlingListener == null) {
            val helper: SnapHelper = SnapToPositionHelper()
            helper.attachToRecyclerView(imageGalleryView)

        }
    }

    private fun bindDate(dateView: TextView, sentDate: Date) {
        dateView.text = TimeUtil.calculateElapsedTime(dateView.context, sentDate)
    }

    private fun bindExpandCollapseButton(
        expandCollapseButton: Button,
        mailListItem: MailListItem,
        position: Int,
        contentView: TextView
    ) {
        contentView.doOnPreDraw {
            if (contentView.lineCount <= Constants.CARD_MAX_LINES) {
                expandCollapseButton.visibility = View.GONE
            }
        }

        expandCollapseButton.text =
            if (mailListItem.isExpanded) expandCollapseButton.context.resources.getString(
                R.string.collapse
            ) else expandCollapseButton.context.resources.getString(
                R.string.expand
            )

        expandCollapseButton.setOnClickListener {
            val expanded = mailListItem.isExpanded
            mailListItem.isExpanded = !expanded
            notifyItemChanged(position)
            if (expanded) mailListView?.smoothScrollToPosition(position)
        }
    }

    private fun bindAnswerButton(
        answerButton: Button,
        mailListItem: MailListItem
    ) {
        answerButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(mailListItem.replyToAddress))
                putExtra(Intent.EXTRA_SUBJECT, "Re: " + mailListItem.subject)
                putExtra(
                    Intent.EXTRA_TEXT,
                    answerButton.context.resources.getString(
                        R.string.answerEmail,
                        mailListItem.senderName ?: ""
                    )
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            answerButton.context.startActivity(intent)
        }
    }
}
