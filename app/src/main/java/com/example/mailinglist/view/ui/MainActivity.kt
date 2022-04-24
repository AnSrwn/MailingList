package com.example.mailinglist.view.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.mailinglist.CacheManager
import com.example.mailinglist.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MailListFragment>(R.id.fragment_container_view)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val cacheManager = CacheManager()
        cacheManager.cleanDir(this)
    }
}
