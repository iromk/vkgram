package pro.xite.dev.vkgram.main.model

import android.support.v7.app.AppCompatActivity
import pro.xite.dev.vkgram.R
import java.lang.ref.WeakReference

/**
 * Created by Roman Syrchin on 7/1/18.
 */
class ApplicationModel {

    var theme : Int = R.style.VkgramTheme_Indigo
    lateinit var acti : WeakReference<AppCompatActivity>
//    var theme : Int = R.style.VkgramTheme_Greengo
}