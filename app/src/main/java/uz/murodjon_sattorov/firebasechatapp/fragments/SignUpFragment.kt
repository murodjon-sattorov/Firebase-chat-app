package uz.murodjon_sattorov.firebasechatapp.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import uz.murodjon_sattorov.firebasechatapp.activities.MainActivity
import uz.murodjon_sattorov.firebasechatapp.databinding.FragmentSignUpBinding
import uz.murodjon_sattorov.firebasechatapp.dialogs.LoadProgressDialog
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var referance: DatabaseReference

    private lateinit var dialog: LoadProgressDialog

    private val randomImg = arrayOf(
        "colors/color1.jpg",
        "colors/color2.jpg",
        "colors/color3.jpg",
        "colors/color4.jpg",
        "colors/color5.jpg",
        "colors/color6.jpg",
        "colors/color7.jpg"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        dialog = LoadProgressDialog(requireContext())

        //animation block
        run {
            binding.textInputLayoutFillName.translationX = 800F
            binding.textInputLayoutEmail.translationX = 800F
            binding.textInputLayoutPassword.translationX = 800F
            binding.checkbox.translationX = 800F
            binding.text1.translationX = 800F
            binding.text2.translationX = 800F
            binding.text3.translationX = 800F
            binding.text4.translationX = 800F
            binding.signUpBtn.translationX = 800F

            binding.textInputLayoutFillName.alpha = 0F
            binding.textInputLayoutEmail.alpha = 0F
            binding.textInputLayoutPassword.alpha = 0F
            binding.checkbox.alpha = 0F
            binding.text1.alpha = 0F
            binding.text2.alpha = 0F
            binding.text3.alpha = 0F
            binding.text4.alpha = 0F
            binding.signUpBtn.alpha = 0F

            binding.textInputLayoutFillName.animate().translationX(0F).alpha(1F).setDuration(800)
                .setStartDelay(300).start()
            binding.textInputLayoutEmail.animate().translationX(0F).alpha(1F).setDuration(800)
                .setStartDelay(400).start()
            binding.textInputLayoutPassword.animate().translationX(0F).alpha(1F).setDuration(800)
                .setStartDelay(500).start()
            binding.checkbox.animate().translationX(0F).alpha(1F).setDuration(800)
                .setStartDelay(600)
                .start()
            binding.text1.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(700)
                .start()
            binding.text2.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(800)
                .start()
            binding.text3.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(900)
                .start()
            binding.text4.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(1000)
                .start()
            binding.signUpBtn.animate().translationX(0F).alpha(1F).setDuration(800)
                .setStartDelay(1100)
                .start()

        }

        binding.signUpBtn.setOnClickListener {
            checkFields(
                binding.inputFulName.text.toString(),
                binding.inputEmail.text.toString(),
                binding.inputPassword.text.toString(),
                binding.checkbox.isChecked
            )
        }

        return binding.root
    }

    private fun checkFields(
        fullName: String,
        email: String,
        password: String,
        privacyCheck: Boolean
    ) {
        if (fullName.isNotEmpty() && fullName.length > 1) {
            if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            ) {
                if (password.length >= 6) {
                    if (privacyCheck) {

                        dismissKeyboard()
                        dialog.loadDialog()
                        registerUser(fullName, email, password)

                    } else {
                        binding.checkbox.error = ""
                    }
                } else {
                    binding.inputPassword.error = "At least 6 characters"
                    binding.inputPassword.requestFocus()
                }
            } else {
                binding.inputEmail.error = "Invalid Email"
                binding.inputEmail.requestFocus()
            }
        } else {
            binding.inputFulName.error = "Empty space"
            binding.inputFulName.requestFocus()
        }
    }

    private fun registerUser(fullName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser = auth.currentUser!!
                    val userId: String = user.uid

                    referance = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["id"] = userId
                    hashMap["userFullName"] = fullName
                    hashMap["userName"] = "None"
                    hashMap["userImgURL"] = randomImg.random()
                    hashMap["bio"] = "Bio"
                    hashMap["status"] = ""
                    hashMap["userPassword"] = password
                    hashMap["userPhoneNumber"] = "The phone number is empty"

                    referance.setValue(hashMap).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            dialog.dismissDialog()
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }

                } else {
                    dialog.dismissDialog()
                    view?.let {
                        Snackbar.make(
                            it,
                            "Could not register. Please try again!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun dismissKeyboard() {
        val imm: InputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken, 0)
    }

}