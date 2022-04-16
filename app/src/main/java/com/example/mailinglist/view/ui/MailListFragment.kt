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
import com.example.mailinglist.view.adapter.CustomListAdapter
import com.example.mailinglist.viewmodel.MailListViewModel

class MailListFragment : Fragment(R.layout.fragment_mail_list) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mail_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<RecyclerView>(R.id.mailListView)
        listView.layoutManager = LinearLayoutManager(activity)

        val model: MailListViewModel by viewModels()
        model.getMails().observe(viewLifecycleOwner) { mails ->
            val adapter = CustomListAdapter(mails)
            listView.adapter = adapter
        }
    }
}
