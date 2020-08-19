package com.example.handychef

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.handychef.data.RecipePost
import com.example.handychef.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.fragment_all_recipe_item.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        auth = Firebase.auth
        db = Firebase.firestore
        var adapter = GroupAdapter<GroupieViewHolder>()

        binding.allRecipeRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val recipeItem = item as RecipeItem
            Log.d("ItemClicked", "Item ID: ${recipeItem.recipeItem.id}")
            val action = HomeFragmentDirections.actionHomeFragmentToRecipeDetailFragment(recipeItem.recipeItem.id)
            findNavController().navigate(action)
        }

        db.collection("recipes").get()
            .addOnSuccessListener {
                for (recipe in it){
                    val resultRecipeItem = recipe.toObject<RecipePost>()
                    resultRecipeItem.id = recipe.id
                    Log.d("RecipeItem", "${resultRecipeItem}")
                    adapter.add(RecipeItem(resultRecipeItem))
                }
            }
//
//        db.collection("recipes").whereEqualTo("body", "breakfast")
//            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, e ->
//                if (e != null) {
//                    Log.w(TAG, "Listen error", e)
//                    return@addSnapshotListener
//                }
//                for (recipe in querySnapshot!!.documentChanges) {
//                    if (recipe.type == DocumentChange.Type.ADDED) {
//                        val resultRecipeItem = recipe.toObject<RecipePost>()
//                        resultRecipeItem.id = recipe.id
//                        Log.d("RecipeItem", "${resultRecipeItem}")
//                        adapter.add(RecipeItem(resultRecipeItem))
//                    }
//                    val source = if (querySnapshot.metadata.isFromCache)
//                        "local cache"
//                    else
//                        "server"
//                }
//            }

//        db.collectionGroup("recipes").whereEqualTo("body", "Breakfast").get()
//            .addOnSuccessListener {
//                Log.d("Success","${it}")
//                for (recipe in it) {
//                    val resultRecipeItem = recipe.toObject<RecipePost>()
//                    resultRecipeItem.id = recipe.id
//                    Log.d("RecipeItem", "${resultRecipeItem}")
//                    adapter.add(RecipeItem(resultRecipeItem))
//                }
//            }
//            .addOnFailureListener{
//                Log.d("Home","${it}")
//            }

        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                Log.d("Authentication", "This user is not logged in")
                findNavController().navigate(R.id.loginFragment)
            }
        }

        binding.addRecipe.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addRecipeFragment)
        }

        binding.breakfast.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_breakfastFragment)
        }

        binding.lunch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_lunchFragment)
        }

        binding.dinner.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dinnerFragment)
        }

        binding.dessert.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dessertFragment)
        }
////            db.collectionGroup("recipes").whereEqualTo("body", "Breakfast").get()
////                .addOnSuccessListener {
////                    for (recipe in it) {
////                        val resultRecipeItem = recipe.toObject<RecipePost>()
////                        resultRecipeItem.id = recipe.id
////                        Log.d("RecipeItem", "${resultRecipeItem}")
////                        adapter.add(RecipeItem(resultRecipeItem))
////                    }
////                }
//            db.collection("recipes")
//                .orderBy("body")
//                .startAt("Breakfast")
//        }

//        binding.lunch.setOnClickListener {
////            db.collectionGroup("recipes").whereEqualTo("body", "Lunch").get()
////                .addOnSuccessListener {
////                    for (recipe in it){
////                        val resultRecipeItem = recipe.toObject<RecipePost>()
////                        resultRecipeItem.id = recipe.id
////                        Log.d("Lunch", "${it}")
////                        adapter.add(RecipeItem(resultRecipeItem))
////                    }
////                }
//
//            db.collection("recipes")
//                .orderBy("body")
//                .startAt("Lunch")
//        }

        return binding.root

    }

}

class RecipeItem(val recipeItem: RecipePost) : Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.title.text = recipeItem.title
        viewHolder.body.text = recipeItem.body
        viewHolder.timeStamp.text = recipeItem.timestamp.toDate().toString()
        if(recipeItem.headerImageUrl != "" && recipeItem.headerImageUrl.isNotEmpty()){
            Picasso.get().load(recipeItem.headerImageUrl).fit().centerCrop().into(viewHolder.imageView)
        }
    }

    override fun getLayout(): Int = R.layout.fragment_all_recipe_item
}


