package uz.murodjon_sattorov.firebasechatapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.murodjon_sattorov.firebasechatapp.activities.MainActivity
import uz.murodjon_sattorov.firebasechatapp.databinding.FragmentSignInBinding
import uz.murodjon_sattorov.firebasechatapp.dialogs.LoadProgressDialog

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null

    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var dialog: LoadProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        run {
            binding.textInputLayoutEmail.translationX = 800F
            binding.textInputLayoutPassword.translationX = 800F
            binding.forgotYourPassword.translationX = 800F
            binding.signInBtn.translationX = 800F

            binding.textInputLayoutEmail.alpha = 0F
            binding.textInputLayoutPassword.alpha = 0F
            binding.forgotYourPassword.alpha = 0F
            binding.signInBtn.alpha = 0F

            binding.textInputLayoutEmail.animate().translationX(0F).alpha(1F).setDuration(800)
                .setStartDelay(300).start()
            binding.textInputLayoutPassword.animate().translationX(0F).alpha(1F).setDuration(800)
                .setStartDelay(400).start()
            binding.forgotYourPassword.animate().translationX(0F).alpha(1F).setDuration(800)
                .setStartDelay(400).start()
            binding.signInBtn.animate().translationX(0F).alpha(1F).setDuration(800)
                .setStartDelay(400)
                .start()

        }

        binding.signInBtn.setOnClickListener {
            checkFields(
                binding.inputEmail.text.toString(),
                binding.inputPassword.text.toString()
            )
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        dialog = LoadProgressDialog(requireContext())
    }

    private fun checkFields(
        email: String,
        password: String,
    ) {
        if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) {
            if (password.length >= 6) {

                dismissKeyboard()
                dialog.loadDialog()
                loginUser(email, password)

            } else {
                binding.inputPassword.error = "At least 6 characters"
                binding.inputPassword.requestFocus()
            }
        } else {
            binding.inputEmail.error = "Invalid Email"
            binding.inputEmail.requestFocus()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dialog.dismissDialog()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    dialog.dismissDialog()
                    view?.let {
                        Snackbar.make(it, "Authentication failed", Snackbar.LENGTH_SHORT).show()
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