package com.example.handychef

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.handychef.databinding.FragmentAddRecipeBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddRecipeFragment : Fragment() {

    private  lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentAddRecipeBinding
    private  lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        db = Firebase.firestore
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_recipe, container, false)

        binding.cancelRecipe.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
        }

        binding.addRecipe.setOnClickListener {
            val recipe = hashMapOf (
                "userId" to auth.currentUser?.uid,
                "timestamp" to Timestamp.now(),
                "title" to binding.title.text.toString(),
                "body" to binding.body.text.toString()
            )

            db.collection("recipes")
                .add(recipe)
                .addOnFailureListener {
                    Log.d("recipe", "Failed to add recipe")
                    Toast.makeText(context, "Failed to add recipe", Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Log.d("recipe", "Recipe added successfully")
                    Toast.makeText(context, "Added recipe successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addRecipeFragment_to_homeFragment)
                }
        }
        return inflater.inflate(R.layout.fragment_add_recipe, container, false)
    }

}