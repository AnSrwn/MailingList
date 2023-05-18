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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mailinglist.R
import com.example.mailinglist.shared.utils.TimeUtil
import com.example.mailinglist.ui.model.MailListItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.util.*

private const val COLLAPSE_MAX_CHAR_COUNT: Int = 200

class MailListAdapter(private val mailListItems: MutableList<MailListItem>) :
    RecyclerView.Adapter<MailListAdapter.ViewHolder>() {
    private var mailListView: RecyclerView? = null

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val subjectView: TextView = itemView.findViewById(R.id.subjectView)
        val imageViewPager: ViewPager2 = itemView.findViewById(R.id.imageViewPager)
        val imageTabLayout: TabLayout = itemView.findViewById(R.id.imageTabLayout)
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

        bindExpandCollapseButton(
            holder.expandCollapseButton,
            mailListItem,
            position
        )

        bindSubject(holder.subjectView, mailListItem.subject)
        bindSenderName(holder.senderView, mailListItem.senderName)
        bindContent(
            holder.contentView,
            holder.contentLayout,
            mailListItem.content,
            mailListItem.isExpanded
        )
        bindImages(holder.imageViewPager, holder.imageTabLayout, mailListItem.images)
        bindDate(holder.dateView, mailListItem.receivedDate)

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
        contentView.text = if (isExpanded) content else content.getChunkByCharCount(
            COLLAPSE_MAX_CHAR_COUNT,
            " [...]"
        )

        contentView.doOnPreDraw {
            // not optimal, because one-lines are getting removed
            // but so far there is no other way to remove empty content
            if (contentView.lineCount <= 1) {
                contentLayout.visibility = View.GONE
            }
        }
    }

    private fun bindImages(
        imageViewPager: ViewPager2,
        imageTabLayout: TabLayout,
        images: List<Bitmap>
    ) {
        if (images.isNotEmpty()) {
            imageViewPager.visibility = View.VISIBLE
            imageTabLayout.visibility = View.VISIBLE

            setupViewPager(images, imageViewPager)
            setupTabLayout(images, imageTabLayout)
            connectTabLayoutWithViewPager(imageTabLayout, imageViewPager)
        } else {
            imageViewPager.visibility = View.GONE
            imageTabLayout.visibility = View.GONE
        }
    }

    private fun setupTabLayout(
        images: List<Bitmap>,
        imageTabLayout: TabLayout
    ) {
        for (i in 1..images.size) {
            val tab = imageTabLayout.newTab()
            imageTabLayout.addTab(tab)
        }
    }

    private fun setupViewPager(
        images: List<Bitmap>,
        imageViewPager: ViewPager2
    ) {
        val adapter = ImageGalleryAdapter(images)
        imageViewPager.adapter = adapter
    }

    private fun connectTabLayoutWithViewPager(
        imageTabLayout: TabLayout,
        imageViewPager: ViewPager2
    ) {
        imageTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                imageViewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        imageViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                imageTabLayout.selectTab(imageTabLayout.getTabAt(position))
            }
        })
    }

    private fun bindDate(dateView: TextView, sentDate: Date) {
        dateView.text = TimeUtil.calculateElapsedTime(dateView.context, sentDate)
    }

    private fun bindExpandCollapseButton(
        expandCollapseButton: Button,
        mailListItem: MailListItem,
        position: Int
    ) {
        if (mailListItem.content.length > COLLAPSE_MAX_CHAR_COUNT) {
            expandCollapseButton.visibility = View.VISIBLE
        } else {
            expandCollapseButton.visibility = View.GONE
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

    private fun String.getChunkByCharCount(charCount: Int, appendString: String = ""): String {
        val chunks = this.chunked(charCount)

        if (chunks.size > 1) {
            val restOfWord = chunks[1].split(" ")[0]
            return chunks[0] + restOfWord + appendString
        }

        return chunks[0]
    }
}
