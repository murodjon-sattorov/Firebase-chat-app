package uz.murodjon_sattorov.firebasechatapp.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import lv.chi.photopicker.PhotoPickerFragment
import uz.murodjon_sattorov.firebasechatapp.R
import uz.murodjon_sattorov.firebasechatapp.databinding.ActivityUserProfileBinding
import uz.murodjon_sattorov.firebasechatapp.dialogs.ChangeDataDialog
import uz.murodjon_sattorov.firebasechatapp.dialogs.LoadPickImageDialog
import uz.murodjon_sattorov.firebasechatapp.dialogs.LoadProgressDialog
import uz.murodjon_sattorov.firebasechatapp.fragments.AllChatsFragment
import uz.murodjon_sattorov.firebasechatapp.fragments.FavoritesFragment
import uz.murodjon_sattorov.firebasechatapp.fragments.GroupsFragment
import uz.murodjon_sattorov.firebasechatapp.fragments.PersonalFragment
import uz.murodjon_sattorov.firebasechatapp.model.User
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserProfileActivity : AppCompatActivity(), PhotoPickerFragment.Callback {

    private lateinit var userProfileBinding: ActivityUserProfileBinding

    private var auth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null
    private var progressDialog = LoadProgressDialog(this)
    private lateinit var storage: FirebaseStorage
    private lateinit var reference: DatabaseReference;

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

        storage = FirebaseStorage.getInstance()

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
            .child("userImgURL")

        userProfileBinding.profileMore.setOnClickListener {
            onSingleSelectionWithIconsClicked(userProfileBinding.profileMore)
        }

        loadUserProfileImage()

        userProfileBinding.selectProfileImage.setOnClickListener {
            openBottomSheet()
        }

        userProfileBinding.layoutChangeNumber.setOnClickListener {
            val dialog = ChangeDataDialog(this)
            dialog.changeNumber(userProfileBinding.userPhoneNumber.text.toString())
        }

        userProfileBinding.layoutChangeUsername.setOnClickListener {
            val dialog = ChangeDataDialog(this)
            dialog.changeUsername(userProfileBinding.username.text.toString())
        }

        userProfileBinding.layoutChangeBio.setOnClickListener {
            val dialog = ChangeDataDialog(this)
            dialog.changeBio(userProfileBinding.bio.text.toString())
        }


    }

    private fun loadUserProfileImage() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                val pathReference =
                    storage.getReferenceFromUrl("gs://fir-chat-app-f3e45.appspot.com")
                        .child(user?.userImgURL.toString())
                if (user!!.userImgURL?.length != 17) {
                    try {
                        val file: File = File.createTempFile("profile_image", "jpg")
                        pathReference.getFile(file).addOnSuccessListener { taskSnapshot ->
                            val bitmap: Bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            Log.d("PATH", "onDataChange: ${file.absolutePath}")
                            userProfileBinding.imageView.setImageBitmap(bitmap)
                        }.addOnFailureListener {
                            Log.d("TAG", "onDataChange: ${it.message}")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    userProfileBinding.imageView.setBackgroundResource(R.drawable.theme_preview_image)
                }

                userProfileBinding.userFullName.text = user.userFullName.toString()
                userProfileBinding.userPhoneNumber.text = user.userPhoneNumber
                userProfileBinding.username.text = user.userName
                userProfileBinding.bio.text = user.bio
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun onSingleSelectionWithIconsClicked(view: View) {
        val popupMenu = popupMenu {
            dropdownGravity = Gravity.END
            section {
                item {
                    label = "Edit name"
                    icon = R.drawable.ic_outline_edit_24
                    callback = {
                        val dialog = ChangeDataDialog(view.context)
                        dialog.changeName(userProfileBinding.userFullName.text.toString())
                    }
                }
                item {
                    label = "Set new photo"
                    icon = R.drawable.ic_outline_add_a_photo_24
                    callback = {
                        openBottomSheet()
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
                if (it.isSuccessful) {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    finish()
                }
            }

        }.setNegativeButton("Cancel") { dialog, which ->

            dialog.dismiss()

        }

        dialog.show()

    }

    private fun openBottomSheet() {
        PhotoPickerFragment.newInstance(
            multiple = true,
            allowCamera = true,
            maxSelection = 1,
            theme = R.style.ChiliPhotoPicker_Light
        ).show(supportFragmentManager, "picker")
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        progressDialog.loadDialog()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("user/$fileName")

        storageReference.putFile(photos[0]).addOnSuccessListener {
            reference =
                FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
                    .child("userImgURL")
            reference.setValue("user/$fileName").addOnCompleteListener {
                if (it.isSuccessful) {
                    progressDialog.dismissDialog()
                    loadUserProfileImage()
                    finish()
                }
            }
        }.addOnFailureListener {
            progressDialog.dismissDialog()
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
        }
    }
}