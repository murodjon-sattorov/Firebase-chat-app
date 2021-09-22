package uz.murodjon_sattorov.firebasechatapp.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import lv.chi.photopicker.PhotoPickerFragment
import uz.murodjon_sattorov.firebasechatapp.R
import uz.murodjon_sattorov.firebasechatapp.adapters.user.UserMessagesReadAdapter
import uz.murodjon_sattorov.firebasechatapp.databinding.ActivityUserMessagesBinding
import uz.murodjon_sattorov.firebasechatapp.dialogs.LoadPickImageDialog
import uz.murodjon_sattorov.firebasechatapp.model.User
import uz.murodjon_sattorov.firebasechatapp.model.UserMessageModel
import uz.murodjon_sattorov.firebasechatapp.utils.IsNetworkConnection.Companion.status
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UserMessagesActivity : AppCompatActivity(), PhotoPickerFragment.Callback {

    private lateinit var userMessagesBinding: ActivityUserMessagesBinding

    private lateinit var storage: FirebaseStorage

    private val currentUser = Firebase.auth.currentUser
    private lateinit var reference: DatabaseReference

    private var adapter = UserMessagesReadAdapter()
    private var messageList: ArrayList<UserMessageModel>? = null

    private var seenListener: ValueEventListener? = null

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userMessagesBinding = ActivityUserMessagesBinding.inflate(layoutInflater)
        setContentView(userMessagesBinding.root)

        storage = FirebaseStorage.getInstance()

        reference = FirebaseDatabase.getInstance().getReference("user_messages")
        reference.keepSynced(true)

        user = intent.getSerializableExtra("user") as User

        userMessagesBinding.profileName.text = user?.userFullName

        val pathReference =
            storage.getReferenceFromUrl("gs://fir-chat-app-f3e45.appspot.com")
                .child(user?.userImgURL.toString())

        if (user?.userImgURL?.length == 17) {
            userMessagesBinding.userNameLatter.text =
                user?.userFullName?.toUpperCase(Locale.ROOT)
        } else {
            userMessagesBinding.userNameLatter.text = ""
        }

        try {
            val file: File = File.createTempFile("profile_image", "jpg")
            pathReference.getFile(file).addOnSuccessListener { taskSnapshot ->
                val bitmap: Bitmap = BitmapFactory.decodeFile(file.absolutePath)
                userMessagesBinding.profileImage.setImageBitmap(bitmap)
            }.addOnFailureListener {
                Log.d("TAG", "onDataChange: ${it.message}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        userMessagesBinding.closeActivity.setOnClickListener {
            finish()
        }

        changeMessageField()

        userMessagesBinding.sendMessage.setOnClickListener {
            sendMessages(user!!, userMessagesBinding.enterMessage.text.toString())
            userMessagesBinding.enterMessage.setText("")
            changeMessageField()
            setLayoutManager()
        }

        setLayoutManager()

        getOnlineStatus()

        messageList = ArrayList()

        readAllMessages(currentUser!!.uid, user?.id.toString())

        userMessagesBinding.allMessage.adapter = adapter

        userMessagesBinding.sendFiles.setOnClickListener {
            openBottomSheet()
        }

        seenMessage(user?.id.toString())

    }

    private fun setLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true


        userMessagesBinding.allMessage.layoutManager = layoutManager
    }

    private fun changeMessageField() {
        userMessagesBinding.enterMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (userMessagesBinding.enterMessage.text.toString().isNotEmpty()) {
                    userMessagesBinding.sendFiles.visibility = GONE
                    userMessagesBinding.sendSound.visibility = GONE
                    userMessagesBinding.sendMessage.visibility = VISIBLE
                } else {
                    userMessagesBinding.sendFiles.visibility = VISIBLE
                    userMessagesBinding.sendSound.visibility = VISIBLE
                    userMessagesBinding.sendMessage.visibility = GONE
                }
            }

        })
    }

    private fun sendMessages(user: User, message: String) {
        val messageSend: HashMap<String, Any> = HashMap()
        val date: Calendar = Calendar.getInstance()


        currentUser?.uid?.let {
            messageSend.put("sender", it)
        }
        user.id?.let {
            messageSend.put("receiver", it)
        }
        messageSend["messageImageLink"] = "0"
        messageSend["message"] = message
        messageSend["messageDate"] = date.time.hours.toString() + ":" + date.time.minutes.toString()
        messageSend["messageRead"] = false

        reference.push().setValue(messageSend)
    }

    private fun readAllMessages(senderUserId: String, receiverUser: String) {

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList?.clear()
                for (dataSnapshot in snapshot.children) {
                    val message = dataSnapshot.getValue(UserMessageModel::class.java)
                    if (message!!.receiver.equals(senderUserId) && message.sender.equals(
                            receiverUser
                        )
                        || message.receiver.equals(receiverUser) && message.sender.equals(
                            senderUserId
                        )
                    ) {
                        messageList!!.add(message)
                    }
                }
                adapter.data(messageList!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun seenMessage(userId: String){
        reference = FirebaseDatabase.getInstance().getReference("user_messages")
        seenListener = reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children){
                    val message = dataSnapshot.getValue(UserMessageModel::class.java)
                    if (message!!.receiver.equals(currentUser?.uid) && message.sender.equals(userId)){
                        val hashMap: HashMap<String, Boolean> = HashMap()
                        hashMap["messageRead"] = true
                        dataSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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
        val dialog = LoadPickImageDialog(this)
        dialog.loadImage(photos[0], user!!)
    }

    private fun getOnlineStatus() {
        val connectedRef = FirebaseDatabase.getInstance().getReference("Users").child(user?.id.toString())
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue<User>()
                Log.d("TTGG", "onDataChange: ${status?.status}")
                if (status!!.status == "online") {
                    userMessagesBinding.onlineStatus.text = "online"
                } else if (status.status == "offline") {
                    userMessagesBinding.onlineStatus.text = "last seen recently"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenListener!!)
        status("offline")
    }

}