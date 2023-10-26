package ru.netology.social_network.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.social_network.activity.JobsFragment
import ru.netology.social_network.activity.WallFragment

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val itemCount = 2

    override fun getItemCount(): Int {
        return this.itemCount
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return WallFragment()
//            1 -> return EventsFragment()
            1 -> return JobsFragment()
        }
        return Fragment()
    }
}