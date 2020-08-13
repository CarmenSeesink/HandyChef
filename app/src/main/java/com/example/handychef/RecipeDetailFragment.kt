package com.example.handychef

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.handychef.data.RecipePost
import com.example.handychef.R
import com.example.handychef.databinding.FragmentRecipeDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.google.firebase.firestore.ktx.toObject

class RecipeDetailFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailBinding
    private lateinit var db: FirebaseFirestore
    private val args: RecipeDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail, container, false)
        binding.recipeDetailTitle.text = args.recipeId
        db = Firebase.firestore

        db.collection("recipes").document(args.recipeId).get()
            .addOnSuccessListener {
                val item = it.toObject<RecipePost>()
                if(item == null) return@addOnSuccessListener
                binding.recipeDetailTitle.text = item?.title
                binding.recipeDetailBody.text = item?.body
                binding.recipeDetailSteps.text = item?.steps
                binding.recipeDetailTimeStamp.text = item?.timestamp?.toDate().toString()
                if(item.headerImageUrl != ""){
                    Picasso.get().load(item.headerImageUrl).fit().centerCrop().into(binding.recipeHeaderImage)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Sorry could not get recipe", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_recipeDetailFragment_to_homeFragment)
            }

        return binding.root
    }

}