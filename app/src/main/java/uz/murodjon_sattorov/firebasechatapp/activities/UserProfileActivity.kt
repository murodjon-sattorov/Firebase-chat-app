package uz.murodjon_sattorov.firebasechatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import uz.murodjon_sattorov.firebasechatapp.R
import uz.murodjon_sattorov.firebasechatapp.databinding.ActivityUserProfileBinding
import uz.murodjon_sattorov.firebasechatapp.fragments.AllChatsFragment
import uz.murodjon_sattorov.firebasechatapp.fragments.FavoritesFragment
import uz.murodjon_sattorov.firebasechatapp.fragments.GroupsFragment
import uz.murodjon_sattorov.firebasechatapp.fragments.PersonalFragment

class UserProfileActivity : AppCompatActivity() {

    private lateinit var userProfileBinding: ActivityUserProfileBinding

    private var auth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userProfileBinding = ActivityUserProfileBinding.inflate(layoutInflater)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContentView(userProfileBinding.root)

        auth = Firebase.auth
        firebaseUser = auth?.currentUser

        userProfileBinding.profileMore.setOnClickListener {
            onSingleSelectionWithIconsClicked(userProfileBinding.profileMore)
        }


    }

    private fun onSingleSelectionWithIconsClicked(view: View) {
        val popupMenu = popupMenu {
            dropdownGravity = Gravity.END
            section {
                item {
                    label = "Edit name"
                    icon = R.drawable.ic_outline_edit_24
                    callback = {
                        Toast.makeText(this@UserProfileActivity, "Change name", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                item {
                    label = "Set new photo"
                    icon = R.drawable.ic_outline_add_a_photo_24
                    callback = {
                        Toast.makeText(
                            this@UserProfileActivity,
                            "Set new photo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                item {
                    label = "Log out"
                    icon = R.drawable.ic_round_exit_to_app_24
                    callback = {
                        auth?.signOut()
                        startActivity(
                            Intent(
                                this@UserProfileActivity,
                                RegisterActivity::class.java
                            )
                        )
                        finish()
                    }
                }
                item {
                    label = "Delete Account"
                    icon = R.drawable.ic_outline_delete_24
                    iconColor = R.color.red
                    callback = {
                        deleteAccount()
                    }
                }
            }
        }
        popupMenu.show(this, view)
    }

    private fun deleteAccount() {

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Delete account")
        dialog.setMessage("Are you sure delete your account?")
        dialog.setPositiveButton("Ok") { _, _ ->

            val credential: AuthCredential = EmailAuthProvider.getCredential(
                firebaseUser?.email.toString(),
                FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
                    .child("userPassword").toString()
            )

            firebaseUser!!.reauthenticate(credential).addOnCompleteListener {
                firebaseUser?.delete()
            }.addOnCompleteListener {
                if (it.isSuccessful){
                    startActivity(Intent(this, RegisterActivity::class.java))
                    finish()
                }
            }

        }.setNegativeButton("Cancel") { dialog, which ->

            dialog.dismiss()

        }

        dialog.show()

    }
}