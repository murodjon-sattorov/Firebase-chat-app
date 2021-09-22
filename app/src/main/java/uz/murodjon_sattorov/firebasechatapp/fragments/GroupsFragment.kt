package uz.murodjon_sattorov.firebasechatapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.murodjon_sattorov.firebasechatapp.R
import uz.murodjon_sattorov.firebasechatapp.activities.RegisterActivity
import uz.murodjon_sattorov.firebasechatapp.databinding.FragmentGroupsBinding

class GroupsFragment : Fragment() {

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        binding?.button?.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
            requireActivity().finish()
        }

        return binding?.root
    }

}