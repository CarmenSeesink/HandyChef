package com.example.handychef

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import java.util.*

class AddRecipeFragment : Fragment() {

    private  lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentAddRecipeBinding
    private  lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var uri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_recipe, container, false)

        binding.cancelRecipe.setOnClickListener {
            findNavController().navigate(R.id.action_addRecipeFragment_to_homeFragment)
        }

        binding.uploadRecipeImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


        binding.addRecipe.setOnClickListener {
            val recipe = hashMapOf (
                "userId" to auth.currentUser?.uid,
                "timestamp" to Timestamp.now(),
                "title" to binding.title.text.toString(),
                "body" to binding.body.text.toString(),
                "steps" to binding.steps.text.toString()
            )

            db.collection("recipes")
                .add(recipe)
                .addOnFailureListener {
                    Log.d("recipe", "Failed to add recipe")
                    Toast.makeText(context, "Failed to add recipe", Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Log.d("recipe", "Recipe added successfully")
                    UploadImageToStorage(it.id)
                    Toast.makeText(context, "Added recipe successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addRecipeFragment_to_homeFragment)
                }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data!!
            Log.d("AddRecipeFragment", "Photo URI: $uri")
            binding.uploadRecipeImage.setImageURI(uri)
        }
    }

    private fun UploadImageToStorage(recipeId: String) {
        val fileName = UUID.randomUUID().toString()
        Log.d("AddRecipeFragment", "UUID: $fileName")
        val ref = storage.getReference("/images/$fileName")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("AddRecipeFragment", "Image URL: $it")
                    // upload image to recipe object in firestore
                    saveImageToRecipe(it.toString(), recipeId)
                }
            }
            .addOnFailureListener {
                Log.d("AddRecipeFragment", "Failed Upload: $it")
            }
    }

    private fun saveImageToRecipe(imageUrl: String, recipeId: String){
        db.collection("recipes").document(recipeId).update("headerImageUrl", imageUrl)
            .addOnSuccessListener {
                Log.d("AddRecipeFragment", "Document: $it")
            }
            .addOnFailureListener {
                Log.d("AddRecipeFragment", "failed to save image to blog: $it")
            }
    }

}