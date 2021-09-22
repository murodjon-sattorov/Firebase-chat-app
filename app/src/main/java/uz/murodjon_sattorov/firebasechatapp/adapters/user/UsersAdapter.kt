package uz.murodjon_sattorov.firebasechatapp.adapters.user

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import uz.murodjon_sattorov.firebasechatapp.R
import uz.murodjon_sattorov.firebasechatapp.activities.UserMessagesActivity
import uz.murodjon_sattorov.firebasechatapp.databinding.UserItemBinding
import uz.murodjon_sattorov.firebasechatapp.model.User
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/8/2021
 * @project Firebase Chat app
 */
class UsersAdapter : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private var userList = emptyList<User>()
    fun data(user: List<User>) {
        this.userList = user
        notifyDataSetChanged()
    }

    inner class ViewHolder(var userBinding: UserItemBinding) :
        RecyclerView.ViewHolder(userBinding.root) {

        fun bindData(user: User) {
            val storage = FirebaseStorage.getInstance()
            val pathReference =
                storage.getReferenceFromUrl("gs://fir-chat-app-f3e45.appspot.com")
                    .child(user.userImgURL.toString())
            if (user.userImgURL?.length == 17) {
                userBinding.userNameLatter.text =
                    user.userFullName?.toUpperCase(Locale.ROOT)
            } else {
                userBinding.userNameLatter.text = ""
            }
            if (user.status!!.contains("online") && isNetworkConnected){
                userBinding.statusOnline.setBackgroundResource(R.drawable.widget_online)
            }else{
                userBinding.statusOnline.setBackgroundResource(R.drawable.widget_offline)
            }

            try {
                val file: File = File.createTempFile("profile_image", "jpg")
                pathReference.getFile(file).addOnSuccessListener { taskSnapshot ->
                    val bitmap: Bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    userBinding.profileImage.setImageBitmap(bitmap)
                }.addOnFailureListener {
                    Log.d("TAG", "onDataChange: ${it.message}")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            userBinding.profileName.text = user.userFullName

            userBinding.constraint.setOnClickListener {

                val intent =
                    Intent(userBinding.constraint.context, UserMessagesActivity::class.java)
                intent.putExtra("user", user)
                userBinding.constraint.context.startActivity(intent)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserItemBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.user_item, parent, false
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @get:Throws(InterruptedException::class, IOException::class)
    private val isNetworkConnected: Boolean
        get() {
            val command = "ping -c 1 google.com"
            return Runtime.getRuntime().exec(command).waitFor() == 0
        }
}