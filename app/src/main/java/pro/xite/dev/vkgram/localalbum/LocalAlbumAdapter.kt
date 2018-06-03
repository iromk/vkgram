package pro.xite.dev.vkgram.localalbum

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_picture.*
import pro.xite.dev.vkgram.Application
import pro.xite.dev.vkgram.R

class LocalAlbumAdapter : RecyclerView.Adapter<LocalAlbumAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder: ")
        val cv = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_picture, parent, false) as CardView
        return ViewHolder(cv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the dimensions of the View
        val targetW = 150
        val targetH = 150

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        val mCurrentPhotoPath = "/storage/emulated/0/Android/data/pro.xite.dev.vkgram/files/Pictures/images-3.jpeg"
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        holder.setImage(bitmap)
    }

    override fun getItemCount(): Int {
        return 15
    }

    inner class ViewHolder(override val containerView: View?) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
        }
        
        fun setImage(bitmap: Bitmap) {
            card_picture_picture.setImageBitmap(bitmap)
        }
    }

    companion object {
        private val TAG = String.format("%s/%s", Application.APP_TAG, LocalAlbumAdapter::class.java.simpleName)
    }

}