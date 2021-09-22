package uz.murodjon_sattorov.firebasechatapp.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import uz.murodjon_sattorov.firebasechatapp.databinding.AddNewAccountDialogBinding
import uz.murodjon_sattorov.firebasechatapp.model.User
import java.io.File
import java.io.IOException
import java.util.*


/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/8/2021
 * @project Firebase Chat app
 */
class AddNewAccountDialog(context: Context) : AlertDialog(context) {

    private var newAccountDialogBinding: AddNewAccountDialogBinding =
        AddNewAccountDialogBinding.inflate(layoutInflater)

    private var firebaseUser: FirebaseUser = Firebase.auth.currentUser!!
    private var reference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

    private var storage = FirebaseStorage.getInstance()

    init {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                val pathReference =
                    storage.getReferenceFromUrl("gs://fir-chat-app-f3e45.appspot.com")
                        .child(user?.userImgURL.toString())
                if (user!!.userImgURL?.length == 17) {
                    newAccountDialogBinding.userNameLatter.text =
                        user.userFullName?.toUpperCase(Locale.ROOT)
                } else {
                    newAccountDialogBinding.userNameLatter.text = ""
                }

                try {
                    val file: File = File.createTempFile("profile_image", "jpg")
                    pathReference.getFile(file).addOnSuccessListener { taskSnapshot ->
                        val bitmap: Bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        Log.d("PATH", "onDataChange: ${file.absolutePath}")
                        newAccountDialogBinding.profileImage.setImageBitmap(bitmap)
                    }.addOnFailureListener {
                        Log.d("TAG", "onDataChange: ${it.message}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                newAccountDialogBinding.profileName.text = user.userFullName.toString()
                newAccountDialogBinding.profileEmail.text = firebaseUser.email.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        setView(newAccountDialogBinding.root)
    }

}