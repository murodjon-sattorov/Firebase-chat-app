package uz.murodjon_sattorov.firebasechatapp.app

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import lv.chi.photopicker.ChiliPhotoPicker
import uz.murodjon_sattorov.firebasechatapp.loaders.GlideImageLoader

/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/18/2021
 * @project Firebase Chat app
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        ChiliPhotoPicker.init(
            GlideImageLoader(), "com.murodjon_sattorov.firebasechatapp"
        )
    }

}