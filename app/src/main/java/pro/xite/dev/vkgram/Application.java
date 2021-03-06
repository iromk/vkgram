package pro.xite.dev.vkgram;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;
import com.vk.sdk.util.VKUtil;

import java.util.Arrays;

public class Application extends android.app.Application {

    public static final String APP_TAG = "VKG";
    private static final String TAG = String.format("%s/%s", Application.APP_TAG,  Application.class.getSimpleName());

    private RequestQueue requestQueue;
    private static ImageLoader imageLoader;

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Toast.makeText(Application.this, "AccessToken invalidated", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Application.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Application onCreate: ");
        getCertFingerprint();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
        initImageLoader(this);
    }

    private void getCertFingerprint() {
        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());

        Log.d(TAG, String.format("getCertFingerprint: \n%s", Arrays.toString(fingerprints)));

    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    private void initImageLoader(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }


}

