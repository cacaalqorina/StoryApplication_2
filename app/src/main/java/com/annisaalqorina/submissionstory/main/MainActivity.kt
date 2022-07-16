package com.annisaalqorina.submissionstory.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.annisaalqorina.submissionstory.DataPreferences
import com.annisaalqorina.submissionstory.R
import com.annisaalqorina.submissionstory.adapter.PagingAdapter
import com.annisaalqorina.submissionstory.adapter.StoryAdapter
import com.annisaalqorina.submissionstory.databinding.ActivityMainBinding
import com.annisaalqorina.submissionstory.response.ListStoryItem
import com.annisaalqorina.submissionstory.viewmodel.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private val userViewModel by viewModels<UserViewModel>()
    private val storyViewModel by viewModels<StoryViewModel>()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setupAdapter()
        setClick()
        checkUserStatus()

        storyViewModel.isLoading.observe(this){
            setProgressDialog(it)
        }
    }

    private fun checkUserStatus() {
        userViewModel.getDataSession().observe(this) {
            if (it.token.trim() == "") {
                val intent = Intent(this@MainActivity, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                setupData()
            }
        }
    }

    private fun setupData() {
        storyViewModel.allStory.observe(this) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
            setProgressDialog(false)
        }
    }


    private fun logoutUser() {
        userViewModel.clearDataSession()
        checkUserStatus()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.menu_logout -> {
                logoutUser()
            }
            R.id.menu_maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
        }
        return true
    }

    private fun setClick() {
        activityMainBinding.createAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupAdapter() {

        storyAdapter = StoryAdapter()
        activityMainBinding.snapStory.layoutManager = LinearLayoutManager(this@MainActivity)
        activityMainBinding.snapStory.adapter = storyAdapter.withLoadStateFooter(
            footer = PagingAdapter {
                storyAdapter.retry()
            }
        )
    }

    private fun setProgressDialog(isLoading : Boolean) {
        activityMainBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}