package uz.murodjon_sattorov.firebasechatapp.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import uz.murodjon_sattorov.firebasechatapp.R

/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/4/2021
 * @project Firebase Chat app
 */
class LoadProgressDialog(var context: Context) {

    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var dialog: Dialog

    fun loadDialog(){
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
        materialAlertDialogBuilder.setView(R.layout.progress_dialog)
        materialAlertDialogBuilder.setCancelable(false)
        materialAlertDialogBuilder.background = ColorDrawable(Color.TRANSPARENT)
        dialog = materialAlertDialogBuilder.show()
    }

    fun dismissDialog(){
        dialog.dismiss()
    }

}