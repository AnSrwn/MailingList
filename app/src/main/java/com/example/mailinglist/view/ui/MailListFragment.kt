package com.example.mailinglist.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mailinglist.R
import com.example.mailinglist.model.MailListItem
import com.example.mailinglist.view.adapter.MailListAdapter
import com.example.mailinglist.viewmodel.MailListViewModel

class MailListFragment : Fragment(R.layout.fragment_mail_list) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mail_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<RecyclerView>(R.id.mailListView)
        listView.layoutManager = LinearLayoutManager(activity)
        listView.itemAnimator = null

        val model: MailListViewModel by viewModels()
        model.getMails().observe(viewLifecycleOwner) { mails ->
            val mailListItems = mails.map { mail ->
                MailListItem(mail)
            }

            val adapter = MailListAdapter(mailListItems)
            listView.adapter = adapter
        }
    }
}
