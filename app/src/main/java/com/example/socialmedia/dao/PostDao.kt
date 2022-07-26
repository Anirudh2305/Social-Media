package com.example.socialmedia.dao

import com.example.socialmedia.model.Post
import com.example.socialmedia.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.okhttp.internal.DiskLruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Document

class PostDao {

    private val db= FirebaseFirestore.getInstance()
    val postCollections=db.collection("posts")
    private val auth = FirebaseAuth.getInstance()


     fun addPost(text:String){
         GlobalScope.launch {
             val currentUserId = auth.currentUser!!.uid
             val user = UserDao().getUserById(currentUserId).await().toObject(User::class.java)!!
             val currentTime = System.currentTimeMillis()
             val post = Post(text, user, currentTime)
             postCollections.document().set(post)
         }
    }

    fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postCollections.document(postId).get()

    }

    fun updateLikes(postId:String){
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(Post::class.java)!!

            val isLiked = post.likedBy.contains(currentUserId)

            if(isLiked)
                post.likedBy.remove(currentUserId)
            else
                post.likedBy.add(currentUserId)

            postCollections.document(postId).set(post)
        }
    }
}