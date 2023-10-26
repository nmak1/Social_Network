package ru.netology.social_network.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.social_network.R
import ru.netology.social_network.adapter.UserAdapter
import ru.netology.social_network.adapter.OnUserInteractionListener
import ru.netology.social_network.databinding.FragmentUsersBinding
import ru.netology.social_network.dto.User
import ru.netology.social_network.viewmodel.EventViewModel
import ru.netology.social_network.viewmodel.PostViewModel
import ru.netology.social_network.viewmodel.UserViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UsersFragment : Fragment() {

    private val userViewModel by viewModels<UserViewModel>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val eventViewModel by activityViewModels<EventViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentUsersBinding.inflate(
            inflater,
            container,
            false
        )

        val open = arguments?.getString("open")

        val adapter = UserAdapter(object : OnUserInteractionListener {
            override fun openProfile(user: User) {
                when (open) {
                    "mention" -> {
                        postViewModel.changeMentionIds(user.id)
                        postViewModel.save()
                        findNavController().navigateUp()
                    }
                    "speaker" -> {
                        eventViewModel.setSpeaker(user.id)
                        findNavController().navigateUp()
                    }
                    else -> {
                        userViewModel.getUserById(user.id)

                        val bundle = Bundle().apply {
                            putLong("id", user.id)
                            putString("avatar", user.avatar)
                            putString("name", user.name)
                        }
                        findNavController().apply {
                            this.popBackStack(R.id.nav_users, true)
                            this.navigate(R.id.nav_profile, bundle)
                        }
                    }
                }
            }
        })

        binding.fragmentListUsers.adapter = adapter

        userViewModel.data.observe(viewLifecycleOwner)
        {
            adapter.submitList(it)
        }

        userViewModel.dataState.observe(viewLifecycleOwner)
        {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.progressBarFragmentUsers.isVisible = it.loading
        }

        return binding.root
    }
}