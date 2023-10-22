package ru.netology.social_network.adapter

import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.social_network.BuildConfig.BASE_URL
import ru.netology.social_network.R
import ru.netology.social_network.databinding.ActivityCardPostFragmentBinding
import ru.netology.social_network.dto.Post
import ru.netology.social_network.until.ConvertNumber

class PostViewHolder(
    private val binding : ActivityCardPostFragmentBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            publisher.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"
            share.text = ConvertNumber.counterDecimal(post.shares)
            view.text = "${post.views}"
            video.isVisible = !post.videoUrl.isNullOrBlank()
            videoImage.isVisible = !post.videoUrl.isNullOrBlank()
            attachment.visibility = View.GONE
            val url = "${BASE_URL}/avatars/${post.authorAvatar}"
            Glide.with(itemView)
                .load(url)
                .placeholder(R.drawable.ic_loading_24dp)
                .error(R.drawable.ic_baseline_error_outline_24dp)
                .timeout(10_000)
                .circleCrop()
                .into(Avatar)

            val urlAttachment = "${BASE_URL}/media/${post.attachment?.url}"
            if (post.attachment != null) {
                attachment.visibility = View.VISIBLE
                Glide.with(itemView)
                    .load(urlAttachment)
                    .placeholder(R.drawable.ic_loading_24dp)
                    .error(R.drawable.ic_baseline_error_outline_24dp)
                    .timeout(10_000)
                    .into(attachment)
            }  else {
                attachment.visibility = View.GONE
            }


            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }

                    }

                }.show()

            }
            like.setOnClickListener {
                onInteractionListener.onLike(post)

            }
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            view.setOnClickListener {
                onInteractionListener.onViewPost(post)

            }
            video.setOnClickListener {

                onInteractionListener.onVideo(post)

            }
            attachment.setOnClickListener {
                post.attachment?.let { attach ->
                    onInteractionListener.onImage(attach.url)
                }
            }

        }


    }
}







