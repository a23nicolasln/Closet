package com.example.closet.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.closet.R

class LoadingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val globeAnimation = view.findViewById<LottieAnimationView>(R.id.globe_animation)

        // Retrieve the argument (default to false if not provided)
        val goingToLocalCloset = arguments?.getBoolean("local") ?: false

        if (goingToLocalCloset) {
            // Play reverse animation
            globeAnimation.speed = -1f
            globeAnimation.progress = 1f
        } else {
            // Play forward animation
            globeAnimation.speed = 1f
            globeAnimation.progress = 0f
        }

        globeAnimation.playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            if (goingToLocalCloset) {
                view.findNavController().navigate(R.id.action_loadingFragment_to_outfits)
            } else {
                view.findNavController().navigate(R.id.action_loadingFragment_to_homeFragment)
            }
        }, 1000)
    }
}
