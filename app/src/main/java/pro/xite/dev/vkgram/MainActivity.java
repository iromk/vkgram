package pro.xite.dev.vkgram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, MainActivity.class.getSimpleName());

    private @StyleRes int theme = R.style.VkgramThemeGreengo;

    @BindView(R.id.user_name) TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefSettings = getSharedPreferences("HW2.SETTINGS", MODE_PRIVATE);

        if(savedInstanceState != null) {
            theme = savedInstanceState.getInt(ThemeSelectActivity.KEY_THEME_ID);
            prefSettings.edit().putInt(ThemeSelectActivity.KEY_THEME_ID, theme).apply();
        } else {
            @StyleRes int savedTheme = prefSettings.getInt(ThemeSelectActivity.KEY_THEME_ID, ThemeSelectActivity.NONE);
            if(savedTheme != ThemeSelectActivity.NONE)
                theme = savedTheme;
        }
        setTheme(theme);
        setContentView(R.layout.content_main);
        ButterKnife.bind(this);
//        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());

        Log.w(TAG, "onCreate: before logng ");
        VKSdk.login(this);
        Log.w(TAG, "onCreate: after login");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ThemeSelectActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ThemeSelectActivity.KEY_THEME_ID, theme);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ThemeSelectActivity.RESULT_THEME_CHANGED && data != null) {
            final @StyleRes int theme = data.getIntExtra(ThemeSelectActivity.KEY_THEME_ID, ThemeSelectActivity.NONE);
            if (theme != ThemeSelectActivity.NONE) {
                this.theme = theme;
                recreate();
            }
        }
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Log.d(TAG, "onResult: User passed Authorization");

                setUserName(res.userId);
            }

            @Override
            public void onError(VKError error) {
                Log.d(TAG, "onResult: User didn't pass Authorization");
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setUserName(final String text) {
        tvUserName.setText(text);
    }
}
