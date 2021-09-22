package uz.murodjon_sattorov.firebasechatapp.loaders

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import lv.chi.photopicker.loader.ImageLoader
import uz.murodjon_sattorov.firebasechatapp.R

/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/18/2021
 * @project Firebase Chat app
 */
class GlideImageLoader : ImageLoader {
    override fun loadImage(context: Context, view: ImageView, uri: Uri) {
        Glide.with(context).asBitmap().load(uri).placeholder(R.drawable.icplaceholder).centerCrop().into(view)
    }
}