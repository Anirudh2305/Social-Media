package com.example.socialmedia

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmedia.dao.PostDao
import com.example.socialmedia.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.android.gms.common.api.GoogleApiClient

import com.google.android.gms.auth.api.signin.GoogleSignInOptions




class MainActivity : AppCompatActivity(), ClickHandler {

    private lateinit var madapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener{
            val intent = Intent(this,CreatePostActivity::class.java)
            startActivity(intent)

        }
        setupRecyclerView()
    }

    private fun setupRecyclerView(){

            val postCollections = PostDao().postCollections
            val query = postCollections.orderBy("createdAt",Query.Direction.DESCENDING)
            val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()

            madapter= PostAdapter(recyclerViewOptions,this)

            with(post_lists){
                layoutManager=LinearLayoutManager(this@MainActivity)
                adapter=madapter
            }

    }

       override fun onStart() {
           super.onStart()
           madapter.startListening()
       }


       override fun onStop() {
           super.onStop()
           madapter.stopListening()
       }

    override fun onLikeClicked(postId: String) {
        PostDao().updateLikes(postId)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.logout_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logoutButton)
        {
            val auth = FirebaseAuth.getInstance()
            if(auth.currentUser!=null)
            {
                auth.signOut()
                //GoogleSignInClient.sig
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(this, gso)
                googleSignInClient.signOut().addOnCompleteListener{
                    val intent = Intent(this,SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener{
                    Toast.makeText(this,"Unable to Log Out",Toast.LENGTH_LONG).show()
                }
            }
            else
                Toast.makeText(this,"Nobody Logged In",Toast.LENGTH_LONG).show()
        }
        return true

    }

}