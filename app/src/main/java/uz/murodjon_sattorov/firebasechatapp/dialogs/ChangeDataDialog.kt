package uz.murodjon_sattorov.firebasechatapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.murodjon_sattorov.firebasechatapp.databinding.ChangeDataDialogBinding
import uz.murodjon_sattorov.firebasechatapp.model.User
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/23/2021
 * @project Firebase Chat app
 */
class ChangeDataDialog(var context: Context) {

    private var changeDataDialogBinding: ChangeDataDialogBinding =
        ChangeDataDialogBinding.inflate(LayoutInflater.from(context))

    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var dialog: Dialog

    private var firebaseUser: FirebaseUser = Firebase.auth.currentUser!!
    private var reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

    fun changeName(name: String) {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
        materialAlertDialogBuilder.setView(changeDataDialogBinding.root)
        materialAlertDialogBuilder.setCancelable(true)
        materialAlertDialogBuilder.background = ColorDrawable(Color.TRANSPARENT)

        changeDataDialogBinding.enterText.setText(name)
        changeDataDialogBinding.saveData.setOnClickListener {

            reference.child("userFullName").setValue(changeDataDialogBinding.enterText.text.toString()).addOnCompleteListener {
                dialog.dismiss()
                Toast.makeText(context, "Save data", Toast.LENGTH_SHORT).show()
            }
        }
        dialog = materialAlertDialogBuilder.show()
    }

    fun changeNumber(number: String) {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
        materialAlertDialogBuilder.setView(changeDataDialogBinding.root)
        materialAlertDialogBuilder.setCancelable(true)
        materialAlertDialogBuilder.background = ColorDrawable(Color.TRANSPARENT)

        changeDataDialogBinding.enterText.setText(number)
        changeDataDialogBinding.saveData.setOnClickListener {

            reference.child("userPhoneNumber").setValue(changeDataDialogBinding.enterText.text.toString()).addOnCompleteListener {
                dialog.dismiss()
                Toast.makeText(context, "Save data", Toast.LENGTH_SHORT).show()
            }
        }
        dialog = materialAlertDialogBuilder.show()
    }

    fun changeUsername(username:String){
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
        materialAlertDialogBuilder.setView(changeDataDialogBinding.root)
        materialAlertDialogBuilder.setCancelable(true)
        materialAlertDialogBuilder.background = ColorDrawable(Color.TRANSPARENT)

        if (username.isEmpty()){
            changeDataDialogBinding.enterText.hint = "Your Username"
        }else if (username.length < 4){
            changeDataDialogBinding.enterText.error = "A username must have at least 5 characters."
        }else{
            changeDataDialogBinding.enterText.setText(username)
        }
        changeDataDialogBinding.saveData.setOnClickListener {
            reference.child("userName").setValue(changeDataDialogBinding.enterText.text.toString().toString()).addOnCompleteListener {
                dialog.dismiss()
                Toast.makeText(context, "Save data", Toast.LENGTH_SHORT).show()
            }
        }
        dialog = materialAlertDialogBuilder.show()

    }

    fun changeBio(bio:String){
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
        materialAlertDialogBuilder.setView(changeDataDialogBinding.root)
        materialAlertDialogBuilder.setCancelable(true)
        materialAlertDialogBuilder.background = ColorDrawable(Color.TRANSPARENT)

        changeDataDialogBinding.enterText.maxEms = 70
        changeDataDialogBinding.enterText.setText(bio)

        changeDataDialogBinding.saveData.setOnClickListener {
            reference.child("bio").setValue(changeDataDialogBinding.enterText.text.toString()).addOnCompleteListener {
                dialog.dismiss()
                Toast.makeText(context, "Save data", Toast.LENGTH_SHORT).show()
            }
        }
        dialog = materialAlertDialogBuilder.show()

    }


}