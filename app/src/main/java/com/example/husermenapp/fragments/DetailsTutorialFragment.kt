package com.example.husermenapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.husermenapp.R
import com.example.husermenapp.databinding.FragmentTutorialDetailsBinding
import com.example.husermenapp.dataclasses.Tutorial
import com.example.husermenapp.utils.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.utils.FragmentUtils.replaceFragment
import com.example.husermenapp.fragments.basefragments.BaseItemDetailsFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class DetailsTutorialFragment : BaseItemDetailsFragment<FragmentTutorialDetailsBinding, Tutorial>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTutorialDetailsBinding {
        return FragmentTutorialDetailsBinding.inflate(inflater, container, false)
    }

    override fun setupTextViews() {
        tutorial?.let {
            binding.tvName.text = applyTextViewFormat(it.name.toString())
            binding.tvDescriptionValue.text = it.description
            binding.tvTopicValue.text = applyTextViewFormat(it.topic.toString())
            binding.tvTypeValue.text = applyTextViewFormat(it.type.toString())

            Log.d("Tutorial", "${it.type.toString()}")

            when (it.type.toString()) {
                "video" -> {
                    binding.viewYoutubePlayer.visibility = View.VISIBLE
                    binding.tvWalkthrough.visibility = View.GONE
                    playYoutubeVideo(it.videoUrl.toString().takeLast(11))
                }
                "guía" -> {
                    binding.tvWalkthrough.visibility = View.VISIBLE
                    binding.viewYoutubePlayer.visibility = View.GONE
                    binding.tvWalkthrough.text = it.content
                }
            }
        }
    }

    override fun EditItemFragment(): Fragment = EditTutorialFragment()

    override var modelRef: String = "tutorials"

    private var tutorial: Tutorial? = null
    private val tutorialsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(modelRef)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tutorial = it.getSerializable("selectedTutorial") as? Tutorial
            isCreatingNewItem = it.getBoolean("isCreatingNewTutorial", false)

            if (isCreatingNewItem) handleClickBtnEditTutorial()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextViews()
        setupYotubePlayer()

        binding.btnEditTutorial.setOnClickListener { handleClickBtnEditTutorial() }
    }

    override fun onResume() {
        super.onResume()

        tutorial?.key?.let { it1 ->
            tutorialsRef.child(it1).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        tutorial = dataSnapshot.getValue(Tutorial::class.java)
                        tutorial?.key = dataSnapshot.key

                        setupTextViews()
                    } else {
                        Log.d("Firebase", "No existe el producto con key: $it1")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error al obtener el producto: ${e.message}")
                }
        }
    }

    private fun handleClickBtnEditTutorial() {
        val editTutorialFragment = setupEditItemFragment(tutorial)
        replaceFragment(
            requireActivity().supportFragmentManager,
            R.id.tutorialFragmentsContainer,
            editTutorialFragment
        )
    }

    private fun setupYotubePlayer() {
        lifecycle.addObserver(binding.viewYoutubePlayer)
    }

    private fun playYoutubeVideo(codigoVideo: String) {
        binding.viewYoutubePlayer.visibility = View.VISIBLE

        binding.viewYoutubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(codigoVideo, 0f)
                youTubePlayer.play()
            }
        })
    }
}