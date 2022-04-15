package com.example.mailinglist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mailinglist.model.Mail

class MailListFragment : Fragment(R.layout.fragment_mail_list) {
    private val mails = arrayOf(
        Mail("First Mail", "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."),
        Mail("ergsr Mail", "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."),
        Mail("Third Mail", "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."),
        Mail("Whatsup Mail", "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."),
        Mail("Hey ho", "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."),
        Mail("gfrdgvd", "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."),
        Mail("House", "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."),
        Mail("Search", "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno."),
        Mail("Test", "Hello Peter, mfoifweoam noiewomwe ojinfowem oifejwomfew jiofwemopfew okno.")
    )

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

        val adapter = CustomListAdapter(mails)

        listView.adapter = adapter
    }
}