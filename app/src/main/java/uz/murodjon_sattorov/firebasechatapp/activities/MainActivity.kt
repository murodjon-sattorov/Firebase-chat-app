package uz.murodjon_sattorov.firebasechatapp.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import uz.murodjon_sattorov.firebasechatapp.R
import uz.murodjon_sattorov.firebasechatapp.databinding.ActivityMainBinding
import uz.murodjon_sattorov.firebasechatapp.dialogs.AddNewAccountDialog
import uz.murodjon_sattorov.firebasechatapp.fragments.AllChatsFragment
import uz.murodjon_sattorov.firebasechatapp.fragments.FavoritesFragment
import uz.murodjon_sattorov.firebasechatapp.fragments.GroupsFragment
import uz.murodjon_sattorov.firebasechatapp.fragments.PersonalFragment
import uz.murodjon_sattorov.firebasechatapp.model.User
import uz.murodjon_sattorov.firebasechatapp.utils.IsNetworkConnection.Companion.status
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemReselectedListener {

    private lateinit var mainBinding: ActivityMainBinding

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var referance: DatabaseReference
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        firebaseUser = Firebase.auth.currentUser!!
        storage = FirebaseStorage.getInstance()
        referance = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        referance.keepSynced(true)

        loadUserImage()

        loadNewAccountDialog()

        currentFragment(PersonalFragment())
        mainBinding.viewCategoryText.text = getString(R.string.personal)

        mainBinding.bottomNav.selectedItemId = R.id.show_personal

        mainBinding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.show_all -> {
                    currentFragment(AllChatsFragment())
                    mainBinding.viewCategoryText.text = getString(R.string.all)
                    true
                }
                R.id.show_personal -> {
                    currentFragment(PersonalFragment())
                    mainBinding.viewCategoryText.text = getString(R.string.personal)
                    true
                }
                R.id.show_group -> {
                    currentFragment(GroupsFragment())
                    mainBinding.viewCategoryText.text = getString(R.string.group)
                    true
                }
                R.id.show_fav -> {
                    currentFragment(FavoritesFragment())
                    mainBinding.viewCategoryText.text = getString(R.string.fav)
                    true
                }
                else -> false
            }
        }

        mainBinding.bottomNav.setOnNavigationItemReselectedListener(this)

        mainBinding.drawerView.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

    }

    private fun loadUserImage() {
        referance.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                val pathReference =
                    storage.getReferenceFromUrl("gs://fir-chat-app-f3e45.appspot.com")
                        .child(user?.userImgURL.toString())

                if (user!!.userImgURL?.length == 17) {
                    mainBinding.userNameLatter.text =
                        user.userFullName?.toUpperCase(Locale.ROOT)
                } else {
                    mainBinding.userNameLatter.text = ""
                }

                try {
                    val file: File = File.createTempFile("profile_image", "jpg")
                    pathReference.getFile(file).addOnSuccessListener { taskSnapshot ->
                        val bitmap: Bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        mainBinding.profileImage.setImageBitmap(bitmap)
                    }.addOnFailureListener {
                        Log.d("TAG", "onDataChange: ${it.message}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadNewAccountDialog() {
        mainBinding.profileImage.setOnClickListener {
            val newAccountDialog = AddNewAccountDialog(this)
            newAccountDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            newAccountDialog.show()
        }
    }

    private fun currentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            commit()
        }
    }

    override fun onNavigationItemReselected(item: MenuItem) {

    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }



}