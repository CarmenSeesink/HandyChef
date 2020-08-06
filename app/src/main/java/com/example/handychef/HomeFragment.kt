package com.example.handychef

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.handychef.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
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

        return inflater.inflate(R.layout.fragment_home, container, false)

    }

}