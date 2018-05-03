package pro.xite.dev.vkgram;


import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;
import com.vk.sdk.util.VKUtil;

import java.util.Arrays;

public class Application extends android.app.Application {

    public static final String APP_TAG = "VKG";
    private static final String TAG = String.format("%s/%s", Application.APP_TAG,  Application.class.getSimpleName());

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
        vkAccessTokenTracker.startTracking();
        getCertFingerprint();
        VKSdk.initialize(this);
    }

    private void getCertFingerprint() {
        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());

        Log.d(TAG, String.format("getCertFingerprint: \n%s", Arrays.toString(fingerprints)));

    }

}

