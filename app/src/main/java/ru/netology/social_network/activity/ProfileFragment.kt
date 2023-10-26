package ru.netology.social_network.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.social_network.R
import ru.netology.social_network.adapter.UserProfileAdapter
import ru.netology.social_network.databinding.FragmentProfileBinding
import ru.netology.social_network.dto.Event
import ru.netology.social_network.dto.Job
import ru.netology.social_network.dto.Post
import ru.netology.social_network.viewmodel.AuthViewModel
import ru.netology.social_network.viewmodel.EventViewModel
import ru.netology.social_network.viewmodel.JobViewModel
import ru.netology.social_network.viewmodel.PostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val authViewModel by activityViewModels<AuthViewModel>()

    private val postViewModel by activityViewModels<PostViewModel>()

    private val eventViewModel by activityViewModels<EventViewModel>()

    private val jobViewModel by activityViewModels<JobViewModel>()

    private val profileTitles = arrayOf(
        R.string.title_posts,
//        R.string.title_events,
        R.string.title_jobs
    )

    private var visibilityFabGroup = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )

        val viewPagerProfile = binding.viewPagerFragmentProfile
        val tabLayoutProfile = binding.tabLayoutFragmentProfile
        val id = arguments?.getLong("id")
        val avatar = arguments?.getString("avatar")
        val name = arguments?.getString("name")

        (activity as AppCompatActivity).supportActionBar?.title = name

        viewPagerProfile.adapter = UserProfileAdapter(this)

        TabLayoutMediator(tabLayoutProfile, viewPagerProfile) { tab, position ->
            tab.text = getString(profileTitles[position])
        }.attach()

        with(binding) {
            textViewUserNameFragmentProfile.text = name

            imageViewUserAvatarFragmentProfile.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("url", avatar)
                }
                findNavController().navigate(R.id.nav_image_attachment_fragment, bundle)
            }

            Glide.with(imageViewUserAvatarFragmentProfile)
                .load("$avatar")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_default_user_profile_image)
                .into(imageViewUserAvatarFragmentProfile)
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authorized && id == it.id) {
                binding.fabAdd.visibility = View.VISIBLE
            }
        }

        binding.fabAdd.setOnClickListener {
            if (!visibilityFabGroup) {
                binding.fabAdd.setImageResource(R.drawable.ic_baseline_close_24)
                binding.fabGroup.visibility = View.VISIBLE
            } else {
                binding.fabAdd.setImageResource(R.drawable.ic_baseline_add_24)
                binding.fabGroup.visibility = View.GONE
            }
            visibilityFabGroup = !visibilityFabGroup
        }

        binding.fabAddPost.setOnClickListener {
            postViewModel.edit(Post.emptyPost)
            findNavController().navigate(R.id.action_nav_profile_to_new_post_fragment)
        }

        binding.fabAddEvent.setOnClickListener {
            eventViewModel.edit(Event.emptyEvent)
            findNavController().navigate(R.id.action_nav_profile_to_new_event_fragment)
        }

        binding.fabAddJob.setOnClickListener {
            jobViewModel.edit(Job.emptyJob)
            findNavController().navigate(R.id.action_nav_profile_to_new_job_fragment)
        }

        return binding.root
    }
}