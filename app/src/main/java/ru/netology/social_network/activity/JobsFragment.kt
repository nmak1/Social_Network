package ru.netology.social_network.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.social_network.R
import ru.netology.social_network.adapter.JobAdapter
import ru.netology.social_network.adapter.OnJobInteractionListener
import ru.netology.social_network.databinding.FragmentJobsBinding
import ru.netology.social_network.dto.Job
import ru.netology.social_network.viewmodel.JobViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class JobsFragment : Fragment() {

    private val jobViewModel by activityViewModels<JobViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentJobsBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = JobAdapter(object : OnJobInteractionListener {

            override fun onRemoveJob(job: Job) {
                jobViewModel.removeById(job.id)
            }

            override fun onEditJob(job: Job) {
                jobViewModel.edit(job)
                val bundle = Bundle().apply {
                    putString("name", job.name)
                    putString("position", job.position)
                    putString("start", job.start)
                    job.finish?.let {
                        putString("finish", it)
                    }
                    job.link?.let {
                        putString("link", it)
                    }
                }
                findNavController()
                    .navigate(R.id.nav_new_job_fragment, bundle)
            }
        })

        val id = parentFragment?.arguments?.getLong("id")

        binding.recyclerViewContainerFragmentJobs.adapter = adapter

        lifecycleScope.launchWhenCreated {
            if (id != null) {
                jobViewModel.setId(id)
                jobViewModel.loadJobs(id)
            }
            jobViewModel.data.collectLatest {
                adapter.submitList(it)
                binding.textViewEmptyTextFragmentJobs.isVisible =
                    it.isEmpty()
            }
        }

        jobViewModel.dataState.observe(viewLifecycleOwner) {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.progressBarFragmentJobs.isVisible = it.loading
        }

        return binding.root
    }
}