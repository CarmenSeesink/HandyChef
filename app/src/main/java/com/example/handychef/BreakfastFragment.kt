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

class BreakfastFragment : Fragment() {

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
            val action = BreakfastFragmentDirections.actionBreakfastFragmentToRecipeDetailFragment(recipeItem.recipeItem.id)
            findNavController().navigate(action)
        }

        db.collectionGroup("recipes").whereEqualTo("body", "Breakfast").get()
            .addOnSuccessListener {
                Log.d("Success","${it}")
                for (recipe in it) {
                    val resultRecipeItem = recipe.toObject<RecipePost>()
                    resultRecipeItem.id = recipe.id
                    Log.d("RecipeItem", "${resultRecipeItem}")
                    adapter.add(RecipeItem(resultRecipeItem))
                }
            }
            .addOnFailureListener{
                Log.d("Home","${it}")
            }

        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                Log.d("Authentication", "This user is not logged in")
                findNavController().navigate(R.id.loginFragment)
            }
        }

        binding.addRecipe.setOnClickListener {
            findNavController().navigate(R.id.action_breakfastFragment_to_addRecipeFragment)
        }

        binding.lunch.setOnClickListener {
            findNavController().navigate(R.id.action_breakfastFragment_to_lunchFragment)
        }

        binding.dinner.setOnClickListener {
            findNavController().navigate(R.id.action_breakfastFragment_to_dinnerFragment)
        }

        binding.dessert.setOnClickListener {
            findNavController().navigate(R.id.action_breakfastFragment_to_dessertFragment)
        }

        binding.breakfast.setOnClickListener {
            findNavController().navigate(R.id.action_breakfastFragment_to_homeFragment)
        }

        return binding.root
    }
}
