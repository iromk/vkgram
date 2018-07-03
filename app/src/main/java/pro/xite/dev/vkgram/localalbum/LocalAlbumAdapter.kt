package pro.xite.dev.vkgram.localalbum

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_picture.*
import pro.xite.dev.vkgram.R
import pro.xite.dev.vkgram.main.Application
import timber.log.Timber

class LocalAlbumAdapter(private val p: LocalAlbumPresenter) : RecyclerView.Adapter<LocalAlbumAdapter.PictureViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        Timber.d("onCreateViewHolder: ")

        val cv = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_picture, parent, false) as CardView
        return PictureViewHolder(cv)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        p.showCard(holder, position)
        /*
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
        holder.setImage(bitmap)*/
    }

    override fun getItemCount(): Int = p.count

    inner class PictureViewHolder(override val containerView: View?) :
            RecyclerView.ViewHolder(containerView), LayoutContainer, AlbumItem {

        private val imageStubKim get() = BitmapFactory.decodeResource(Application.getInstance().resources, R.drawable.icons8_kim_kardashian_filled_100)

        override fun setImage(pictureFile: String?) {
            Timber.v("preparing view %s", pictureFile)
            val s: Single<Bitmap> = Single.create({
                val pic =
                        if (pictureFile.isNullOrEmpty())
                            imageStubKim
                        else {
                            Timber.v("minify img %s", pictureFile)
                            val opt = BitmapFactory.Options()
                            opt.inJustDecodeBounds = true
                            BitmapFactory.decodeFile(pictureFile, opt)
                            val minify = Math.min(opt.outHeight / 10, opt.outWidth / 10)
                            opt.inJustDecodeBounds = false
                            opt.inSampleSize = minify
                            BitmapFactory.decodeFile(pictureFile, opt)
                        }
                Timber.v("setting img %s", pictureFile)
                it.onSuccess(pic)
            })
            s.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {  card_picture_picture.setImageBitmap(it) }
                    .subscribe()
            Timber.v("subcribed %s", pictureFile)
        }
    }
}
