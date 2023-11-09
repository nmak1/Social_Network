package ru.netology.social_network.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.SCALE_X
import android.view.View.SCALE_Y
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.social_network.R
import ru.netology.social_network.databinding.CardPostBinding
import ru.netology.social_network.dto.Post
import ru.netology.social_network.enumeration.AttachmentType.AUDIO
import ru.netology.social_network.enumeration.AttachmentType.IMAGE
import ru.netology.social_network.enumeration.AttachmentType.VIDEO
import ru.netology.social_network.untils.formatToDate

interface OnPostInteractionListener {
    fun onOpenPost(post: Post) {}
    fun onEditPost(post: Post) {}
    fun onRemovePost(post: Post) {}
    fun onLikePost(post: Post) {}
    fun onMentionPost(post: Post) {}
    fun onSharePost(post: Post) {}
    fun onOpenLikers(post: Post) {}
    fun onOpenMentions(post: Post) {}
    fun onPlayVideo(post: Post) {}
    fun onPlayAudio(post: Post) {}
    fun onOpenImageAttachment(post: Post) {}
}

class PostsAdapter(
    private val onPostInteractionListener: OnPostInteractionListener,
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onPostInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onPostInteractionListener: OnPostInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {

        binding.apply {
            textViewAuthorCardPost.text = post.author
            textViewPublishedCardPost.text = formatToDate(post.published)
            textViewContentCardPost.text = post.content
            buttonLikeCardPost.isChecked = post.likedByMe
            checkboxLikesSumCardPost.text = post.likeOwnerIds.count().toString()
            buttonMentionCardPost.isChecked = post.mentionedMe
            checkboxMentionsSumCardPost.text = post.mentionIds.count().toString()

            imageViewAttachmentImageCardPost.visibility =
                if (post.attachment != null && post.attachment.type == IMAGE) VISIBLE else GONE

            groupAttachmentAudioCardPost.visibility =
                if (post.attachment != null && post.attachment.type == AUDIO) VISIBLE else GONE

            groupAttachmentVideoCardPost.visibility =
                if (post.attachment != null && post.attachment.type == VIDEO) VISIBLE else GONE

            Glide.with(itemView)
                .load("${post.authorAvatar}")
                .placeholder(R.drawable.ic_baseline_loading_24)
                .error(R.drawable.ic_default_user_profile_image)
                .timeout(10_000)
                .circleCrop()
                .into(imageViewAvatarCardPost)

            post.attachment?.apply {
                Glide.with(imageViewAttachmentImageCardPost)
                    .load(this.url)
                    .placeholder(R.drawable.ic_baseline_loading_24)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .timeout(10_000)
                    .into(imageViewAttachmentImageCardPost)
            }

            buttonMenuCardPost.isVisible = post.ownedByMe
            buttonMenuCardPost.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onPostInteractionListener.onRemovePost(post)
                                true
                            }
                            R.id.edit -> {
                                onPostInteractionListener.onEditPost(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            buttonLikeCardPost.setOnClickListener {
                val scaleX = PropertyValuesHolder.ofFloat(SCALE_X, 1F, 1.25F, 1F)
                val scaleY = PropertyValuesHolder.ofFloat(SCALE_Y, 1F, 1.25F, 1F)
                ObjectAnimator.ofPropertyValuesHolder(it, scaleX, scaleY).apply {
                    duration = 500
                    repeatCount = 1
                    interpolator = BounceInterpolator()
                }.start()
                onPostInteractionListener.onLikePost(post)
            }

            buttonMentionCardPost.visibility =
                if (post.ownedByMe) VISIBLE else INVISIBLE
            buttonMentionCardPost.setOnClickListener {
                onPostInteractionListener.onMentionPost(post)
            }

            buttonShareCardPost.setOnClickListener {
                onPostInteractionListener.onSharePost(post)
            }

            checkboxLikesSumCardPost.setOnClickListener {
                onPostInteractionListener.onOpenLikers(post)
            }

            checkboxMentionsSumCardPost.setOnClickListener {
                onPostInteractionListener.onOpenMentions(post)
            }

            imageButtonBackgroundVideoCardPost.setOnClickListener {
                onPostInteractionListener.onPlayVideo(post)
            }

            imageButtonPlayVideoCardPost.setOnClickListener {
                onPostInteractionListener.onPlayVideo(post)
            }

            imageButtonPlayPauseAudioCardPost.setOnClickListener {
                onPostInteractionListener.onPlayAudio(post)
            }

            imageViewAttachmentImageCardPost.setOnClickListener {
                onPostInteractionListener.onOpenImageAttachment(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Post, newItem: Post): Any = Unit
}