package com.example.socialmedia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.socialmedia.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth


class PostAdapter(options: FirestoreRecyclerOptions<Post>,val listener:ClickHandler) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
    options)
{

    inner class PostViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)

        fun bind(model:Post){

            postText.text=model.text
            userText.text=model.createdBy.displayName
            Glide.with(userImage.context).load(model.createdBy.imageUrl).circleCrop().into(userImage)
            likeCount.text=model.likedBy.size.toString()
            createdAt.text=Utils.getTimeAgo(model.createdAt)

            val auth = FirebaseAuth.getInstance()
            val currentUserId=auth.currentUser!!.uid
            val isLiked = model.likedBy.contains(currentUserId)

            if(isLiked) {
                likeButton.setImageDrawable(ContextCompat.getDrawable(likeButton.context, R.drawable.ic_baseline_favorite_24))
            } else {
                likeButton.setImageDrawable(ContextCompat.getDrawable(likeButton.context, R.drawable.ic_baseline_favorite_border_24))
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewholder = PostViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false))

        viewholder.likeButton.setOnClickListener{
            listener.onLikeClicked(snapshots.getSnapshot(viewholder.bindingAdapterPosition).id)
        }

        return viewholder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.bind(model)
    }
}

interface ClickHandler{                    // It is required because our likecount will be shown in mainAct
    fun onLikeClicked(postId:String){      // So interface callback

    }
}