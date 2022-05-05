package com.example.mailinglist.ui.mailList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mailinglist.R
import com.example.mailinglist.ui.shared.PaginationScrollListener
import com.example.mailinglist.ui.shared.adapter.MailListAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MailListFragment : Fragment(R.layout.fragment_mail_list) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mail_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressIndicatorTop =
            view.findViewById<CircularProgressIndicator>(R.id.progressIndicatorTop)

        val model: MailListViewModel by viewModels()
        val listView = getPaginatedListView(view, model)

        model.isLoading = true
        progressIndicatorTop.visibility = View.VISIBLE
        model.getInitialData().observe(viewLifecycleOwner) { mailListItems ->
            model.isLoading = false
            progressIndicatorTop.visibility = View.INVISIBLE

            val adapter = MailListAdapter(mailListItems)
            listView.adapter = adapter
        }

    }

    private fun getPaginatedListView(
        view: View,
        model: MailListViewModel
    ): RecyclerView {
        val progressIndicatorBottom =
            view.findViewById<CircularProgressIndicator>(R.id.progressIndicatorBottom)
        val listView = view.findViewById<RecyclerView>(R.id.mailListView)

        val layoutManager = LinearLayoutManager(activity)
        listView.layoutManager = layoutManager
        listView.itemAnimator = null

        listView.addOnScrollListener((object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return model.isLastPage
            }

            override fun isLoading(): Boolean {
                return model.isLoading
            }

            override fun loadMoreItems() {
                model.isLoading = true
                progressIndicatorBottom.visibility = View.VISIBLE

                model.getNextPage().observe(viewLifecycleOwner) { mailListItems ->
                    model.isLoading = false
                    progressIndicatorBottom.visibility = View.INVISIBLE

                    (listView.adapter as MailListAdapter).addItems(mailListItems)
                }
            }
        }))

        return listView
    }
}
