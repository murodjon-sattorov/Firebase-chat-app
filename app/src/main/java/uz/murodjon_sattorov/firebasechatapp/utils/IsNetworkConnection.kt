package uz.murodjon_sattorov.firebasechatapp.utils

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/20/2021
 * @project Firebase Chat app
 */
class IsNetworkConnection {

    companion object {
        private lateinit var referance: DatabaseReference
        fun status(status: String) {
            referance = FirebaseDatabase.getInstance().getReference("Users")
                .child(Firebase.auth.currentUser!!.uid)
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["status"] = status
            referance.updateChildren(hashMap as Map<String, Any>)
        }
    }


}