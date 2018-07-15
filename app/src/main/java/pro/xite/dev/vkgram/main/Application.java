package pro.xite.dev.vkgram.main;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;
import com.vk.sdk.util.VKUtil;

import java.util.Arrays;

import pro.xite.dev.vkgram.BuildConfig;
import pro.xite.dev.vkgram.R;
import pro.xite.dev.vkgram.di.AppComponent;
import pro.xite.dev.vkgram.di.DaggerAppComponent;
import pro.xite.dev.vkgram.main.view.MainActivity;
import timber.log.Timber;

public class Application extends android.app.Application {

    public static final String APP_TAG = "VKG";

    private RequestQueue requestQueue;
    private static ImageLoader imageLoader;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    static private AppComponent appComponent;

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
    private static SharedPreferences prefSettings;

    public static Application getInstance() {
        return instance;
    }

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("Application", "Application onCreate");
        if(LeakCanary.isInAnalyzerProcess(this))
            return;
        else
            LeakCanary.install(this);

        Stetho.initializeWithDefaults(this);

        instance = this;

        appComponent = DaggerAppComponent.builder()
                .build();

        if(BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
        Timber.d("Application onCreate: ");
        getCertFingerprint();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
        initImageLoader(this);

        prefSettings = getSharedPreferences(getString(R.string.shared_prefs_default), MODE_PRIVATE);
    }

    public static SharedPreferences settings() {
        return prefSettings;
    }

    private void getCertFingerprint() {
        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());

        Timber.d("getCertFingerprint: \n%s", Arrays.toString(fingerprints));

    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    private void initImageLoader(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }


}