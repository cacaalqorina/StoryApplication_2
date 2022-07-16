package com.annisaalqorina.submissionstory.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.annisaalqorina.submissionstory.databinding.ActivityDetailStoryBinding
import com.annisaalqorina.submissionstory.response.ListStoryItem
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail"
        bind()
    }

    private fun bind() {
        val story = intent.getParcelableExtra<ListStoryItem>("ListStoryItem") as ListStoryItem
        binding.apply {
            Glide.with(applicationContext)
                .load(story.photoUrl)
                .into(binding.storyPhoto)
            binding.nameAccount.text = story.name
            binding.tvDescription.text = story.description
        }
    }
}