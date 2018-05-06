package pro.xite.dev.vkgram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKServiceActivity;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiFriends;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.api.model.VKPhotoArray;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, MainActivity.class.getSimpleName());

    private boolean isResumed = false;

    private @StyleRes int theme = R.style.VkgramThemeGreengo;

    @BindView(R.id.user_name) TextView tvUserName;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_main_layout) DrawerLayout drawerMainLayout;
    @BindView(R.id.drawer_main_nav_view) NavigationView navigationView;
    @BindView(R.id.response_json) EditText edResponseJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefSettings = getSharedPreferences(getString(R.string.shared_prefs_default), MODE_PRIVATE);

        if(savedInstanceState != null) {
            theme = savedInstanceState.getInt(ThemeSelectActivity.KEY_THEME_ID, R.style.VkgramThemeGreengo);
            prefSettings.edit().putInt(ThemeSelectActivity.KEY_THEME_ID, theme).apply();
        } else {
            @StyleRes int savedTheme = prefSettings.getInt(ThemeSelectActivity.KEY_THEME_ID, ThemeSelectActivity.NONE);
            if(savedTheme != ThemeSelectActivity.NONE)
                theme = savedTheme;
        }
        setTheme(theme);
        setContentView(R.layout.drawer_main);
        ButterKnife.bind(this);

        initUI();
        VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                if (isResumed) {
                    switch (res) {
                        case LoggedOut:
                            Log.d(TAG, "wakeUpSession: invoke login");
                            doLogin();
                            break;
                        case LoggedIn:
                            Log.d(TAG, "wakeUpSession: already logged in");
                            break;
                        case Pending:
                            Log.d(TAG, "wakeUpSession: pending state");
                            break;
                        case Unknown:
                            Log.d(TAG, "wakeUpSession: unknown state");
                            break;
                    }
                }
            }

            @Override
            public void onError(VKError error) {
                Log.e(TAG, "wakeUpSession.onError: " + error.toString());
            }
        });
//        requestUserName();
    }

    private void doLogin() {
        VKSdk.login(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        if (VKSdk.isLoggedIn()) {
            Log.d(TAG, "onResume: logged in");
            requestUserName();
        } else {
            Log.d(TAG, "onResume: not logged in");
            doLogin();
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;
        super.onPause();
    }

    private void initUI() {
        setSupportActionBar(toolbar);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerMainLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerToggle.syncState();

        drawerMainLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
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
        Log.w(TAG, String.format("onActivityResult: requestCode == %d, resultCode == %d", requestCode, resultCode));
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ThemeSelectActivity.RESULT_THEME_CHANGED && data != null) {
            final @StyleRes int theme = data.getIntExtra(ThemeSelectActivity.KEY_THEME_ID, ThemeSelectActivity.NONE);
            if (theme != ThemeSelectActivity.NONE) {
                this.theme = theme;
                recreate();
            }
            return;
        }
        if (requestCode == VKServiceActivity.VKServiceType.Authorization.getOuterCode()) {
            VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
                @Override
                public void onResult(VKAccessToken res) {
                    Log.d(TAG, String.format("onResult: User passed Authorization\ntoken [%s]",res.accessToken));
                    requestUserName();
                }

                @Override
                public void onError(VKError error) {
                    Log.d(TAG, "onResult: User didn't pass Authorization");
                }
            };
            if(VKSdk.onActivityResult(requestCode, resultCode, data, callback)) return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    VKApiUserFull user;
    private void requestUserName() {
        final String uid = VKAccessToken.currentToken().userId;
        VKRequest request = VKApi.users().get();//VKParameters.from(VKApiConst.USER_IDS, uid), VKApiUser.class);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onComplete(VKResponse response) {
                try {
                    VKList<VKApiUserFull> users = (VKList) (response.parsedModel);
                    user = users.get(0);
                    setUserName(String.format("%s %s", user.first_name, user.last_name));
                    setTitle(String.format("%s / %s %s", getString(R.string.app_name), user.first_name, user.last_name));
                } catch (ClassCastException e) {
                    Log.e(TAG, String.format(Locale.US, "onComplete: %s", e));
                }
            }
        });
    }

    private void setUserName(final String text) {
        tvUserName.setText(text);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.load_friends && user != null) {
            VKApiFriends friends = new VKApiFriends();
            VKRequest request; //= friends.get(VKParameters.from(VKApiConst.USER_IDS, user.id));
//            request = friends.get(VKParameters.from(VKApiConst.FIELDS, "id,first_name,last_name,sex,bdate,city,photo"));
//            VKApiPhotos photos = new VKApiPhotos();
//            request = new VKRequest("photos.get", VKParameters.from(VKApiConst.USER_ID, user.id, VKApiConst.ALBUM_ID, "wall"), VKPhotoArray.class);
            request = new VKRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, user.id, "need_system", "1"));//, VKApiPhotoAlbum.class);
            request.getPreparedParameters().remove("access_token");
            try {
                Log.i(TAG, "onNavigationItemSelected:\n"+request.getPreparedRequest().getQuery().toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
//                    VKUsersArray users = (VKUsersArray) (response.parsedModel);
                    VKPhotoArray users = (VKPhotoArray) (response.parsedModel);
                    edResponseJson.setText(response.json.toString());
                    super.onComplete(response);
                }

                @Override
                public void onError(VKError error) {
                    Log.e(TAG, "onError: request" + error.toString());
                    super.onError(error);
                }
            });
            return true;
        }https://vk.com/id

        return false;
    }
}
//https://api.vk.com/method/photos.get?user_id=29621442&v=5.52&album_id=saved
//https://api.vk.com/method/photos.get?user_id=455492428&v=5.52&album_id=saved
//https://api.vk.com/method/photos.getAlbums?owner_id=455492428&v=5.52&count=10&need_system=1
//https://vk.com/friends?id=&section=all