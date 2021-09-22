package uz.murodjon_sattorov.firebasechatapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import uz.murodjon_sattorov.firebasechatapp.R
import uz.murodjon_sattorov.firebasechatapp.databinding.PickImageAndLoadDialogBinding
import uz.murodjon_sattorov.firebasechatapp.model.User
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/21/2021
 * @project Firebase Chat app
 */
class LoadPickImageDialog(var context: Context) {

    private var pickImageAndLoadDialogBinding =
        PickImageAndLoadDialogBinding.inflate(LayoutInflater.from(context))

    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var dialog: Dialog
    private var progressDialog = LoadProgressDialog(context)

    private var currentUser = Firebase.auth.currentUser
    private lateinit var reference: DatabaseReference

    fun loadImage(uri: Uri, user: User) {
        Log.d("URI", "loadImage: $uri")
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
        materialAlertDialogBuilder.setView(pickImageAndLoadDialogBinding.root)
        materialAlertDialogBuilder.setCancelable(false)
        materialAlertDialogBuilder.background = ColorDrawable(Color.TRANSPARENT)


        pickImageAndLoadDialogBinding.save.setOnClickListener {
            saveImageAndMessage(uri, user)
        }

        pickImageAndLoadDialogBinding.cancel.setOnClickListener {
            dialog.dismiss()
        }

        Glide.with(context).load(uri).centerCrop().into(pickImageAndLoadDialogBinding.loadImage)

        dialog = materialAlertDialogBuilder.show()
    }

    private fun saveImageAndMessage(uri: Uri, user: User) {
        progressDialog.loadDialog()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("user_images/$fileName")

        storageReference.putFile(uri).addOnSuccessListener {
            progressDialog.dismissDialog()
            sendMessage(user, pickImageAndLoadDialogBinding.enterMessage.text.toString(), fileName)
            Toast.makeText(context, "Successful uploaded", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }.addOnFailureListener {
            progressDialog.dismissDialog()
            Toast.makeText(context, "Failure uploaded", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

    }

    private fun sendMessage(user: User, message: String, fileName: String) {

        reference = FirebaseDatabase.getInstance().getReference("user_messages")

        val messageSend: HashMap<String, Any> = HashMap()
        val date: Calendar = Calendar.getInstance()

        currentUser?.uid?.let {
            messageSend.put("sender", it)
        }
        user.id?.let {
            messageSend.put("receiver", it)
        }
        messageSend["messageImageLink"] = "user_images/$fileName"
        messageSend["message"] = message
        messageSend["messageDate"] = date.time.hours.toString() + ":" + date.time.minutes.toString()
        messageSend["messageRead"] = false

        reference.push().setValue(messageSend)
    }


}