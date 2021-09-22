package uz.murodjon_sattorov.firebasechatapp.adapters.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import uz.murodjon_sattorov.firebasechatapp.R
import uz.murodjon_sattorov.firebasechatapp.databinding.UserTextMessageLeftBinding
import uz.murodjon_sattorov.firebasechatapp.databinding.UserTextMessageRightBinding
import uz.murodjon_sattorov.firebasechatapp.model.User
import uz.murodjon_sattorov.firebasechatapp.model.UserMessageModel
import java.io.File
import java.io.IOException


/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/15/2021
 * @project Firebase Chat app
 */
class UserMessagesReadAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var userMessagesList = emptyList<UserMessageModel>()
    private var fUser: FirebaseUser? = null
    private lateinit var storage: FirebaseStorage
    fun data(message: List<UserMessageModel>) {
        this.userMessagesList = message
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        fUser = Firebase.auth.currentUser
        return if (userMessagesList[position].sender.equals(fUser?.uid)) {
            TYPE_SENDER
        } else {
            TYPE_RECEIVER
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == TYPE_SENDER) {
            ViewHolderRight(
                UserTextMessageRightBinding.bind(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.user_text_message_right, parent, false)
                )
            )
        } else {
            ViewHolderLeft(
                UserTextMessageLeftBinding.bind(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.user_text_message_left, parent, false)
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        return if (viewType == TYPE_SENDER) {
            (holder as ViewHolderRight).bindData(userMessagesList[position])
        } else {
            (holder as ViewHolderLeft).bindData(userMessagesList[position])
        }
    }

    override fun getItemCount(): Int {
        userMessagesList.let {
            return it.size
        }
    }

    inner class ViewHolderLeft(var binding1: UserTextMessageLeftBinding) :
        RecyclerView.ViewHolder(binding1.root) {
        fun bindData(userMessageModel: UserMessageModel) {
            binding1.userText.text = userMessageModel.message
            binding1.messageDate.text = userMessageModel.messageDate
            if (!userMessageModel.equals("0")) {
                binding1.constraint.minWidth = WindowManager.LayoutParams.MATCH_PARENT
                Glide.with(binding1.messageLink.context).load(userMessageModel.messageImageLink)
                    .centerCrop().into(binding1.messageLink)
            }
        }
    }

    inner class ViewHolderRight(var binding2: UserTextMessageRightBinding) :
        RecyclerView.ViewHolder(binding2.root) {
        fun bindData(userMessageModel: UserMessageModel) {
            binding2.userText.text = userMessageModel.message
            binding2.messageDate.text = userMessageModel.messageDate
            Log.d("TTC", "bindData: ${userMessageModel.messageRead}")
            if (userMessageModel.messageRead == true) {
                binding2.messageSendIcon.setBackgroundResource(R.drawable.ic_seen_icon)
            } else {
                binding2.messageSendIcon.setBackgroundResource(R.drawable.ic_send_icon)
            }
            if (!userMessageModel.equals("0")) {
                binding2.constraint.minWidth = WindowManager.LayoutParams.MATCH_PARENT
                loadImage(userMessageModel.messageImageLink.toString(), binding2.messageImageLink)
            }
        }

        private fun loadImage(fileName: String, view: ImageView) {
            storage = FirebaseStorage.getInstance()
            val pathReference = storage.getReferenceFromUrl("gs://fir-chat-app-f3e45.appspot.com").child(fileName)
            try {
                val file: File = File.createTempFile("users_images", "jpg")
                pathReference.getFile(file).addOnSuccessListener { taskSnapshot ->
                    val bitmap: Bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    Glide.with(view.context).load(bitmap).centerCrop().into(view)
                }.addOnFailureListener {
                    Log.d("ERR", "onDataChange: ${it.message}")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private companion object {
        val TYPE_RECEIVER = 0
        val TYPE_SENDER = 1
    }

}