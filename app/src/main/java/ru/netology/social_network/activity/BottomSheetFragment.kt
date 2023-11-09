package ru.netology.social_network.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.netology.social_network.R
import ru.netology.social_network.adapter.OnUserInteractionListener
import ru.netology.social_network.adapter.UserAdapter
import ru.netology.social_network.databinding.FragmentBottomSheetBinding
import ru.netology.social_network.dto.User
import ru.netology.social_network.viewmodel.UserViewModel

class BottomSheetFragment : BottomSheetDialogFragment() {

    private val userViewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_users)

        val binding = FragmentBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = UserAdapter(object : OnUserInteractionListener {
            override fun openProfile(user: User) {
                userViewModel.getUserById(user.id)
                val bundle = Bundle().apply {
                    putLong("id", user.id)
                    putString("name", user.name)
                    putString("avatar", user.avatar)
                }
                findNavController().apply {
                    this.navigate(R.id.nav_profile, bundle)
                }
            }

        })

        binding.recyclerViewContainerFragmentBottomSheet.adapter = adapter

        userViewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it.filter { user ->
                userViewModel.userIds.value?.contains(user.id)!!
            })
        }

        return binding.root
    }
}