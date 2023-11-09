package ru.netology.social_network.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.social_network.R
import ru.netology.social_network.databinding.CardJobBinding
import ru.netology.social_network.dto.Job
import ru.netology.social_network.untils.AndroidUtils

interface OnJobInteractionListener {
    fun onRemoveJob(job: Job)
    fun onEditJob(job: Job)
}

class JobAdapter(
    private val onJobInteractionListener: OnJobInteractionListener,
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobViewHolder(parent.context, binding, onJobInteractionListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class JobViewHolder(
    private val context: Context,
    private val binding: CardJobBinding,
    private val onJobInteractionListener: OnJobInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {

        binding.apply {
            textViewNameCardJob.text = job.name
            textViewPositionCardJob.text = job.position
            textViewStartCardJob.text = AndroidUtils.convertDate(job.start).substring(0, 10)
            textViewFinishCardJob.text =
                if (job.finish == null) context.getString(R.string.text_job_now)
                else AndroidUtils.convertDate(job.finish).substring(0, 10)
            textViewLinkCardJob.visibility =
                if (job.link == null) GONE else VISIBLE
            textViewLinkCardJob.text = job.link

            buttonMenuCardJob.isVisible = job.ownedByMe
            buttonMenuCardJob.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    menu.setGroupVisible(R.id.owned, job.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onJobInteractionListener.onRemoveJob(job)
                                true
                            }
                            R.id.edit -> {
                                onJobInteractionListener.onEditJob(job)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}