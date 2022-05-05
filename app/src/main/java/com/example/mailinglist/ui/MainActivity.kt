package com.example.mailinglist.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.mailinglist.R
import com.example.mailinglist.shared.Constants
import com.example.mailinglist.ui.mailList.MailListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.customToolbar))

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MailListFragment>(R.id.fragment_container_view)
            }
        }

        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.ABELANA_MAIL_ADDRESS))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.emailToList))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            addButton.context.startActivity(intent)
        }
    }
}
