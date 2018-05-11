package pro.xite.dev.vkgram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKServiceActivity;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKUsersArray;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, MainActivity.class.getSimpleName());
    public static final String KEY_VK_FOLLOWERS = "VK_FOLLOWERS";
    private static final int INTENT_IMAGE_CAPTURE = 0x1441;

    private boolean isResumed = false;

    private VKApiUserFull user;

    private @StyleRes int theme = R.style.VkgramThemeGreengo;

    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.drawer_main_layout) DrawerLayout drawerMainLayout;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.drawer_main_nav_view) NavigationView navigationView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.vk_active_user_name) TextView tvActiveUserName;
    @BindView(R.id.vk_active_user_avatar) NetworkImageView nivActiveUserAvatar;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    TextView tvVkUserName;
    private VKUsersArray vkFollowers;
    private File newPictureFile;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefSettings = getSharedPreferences(getString(R.string.shared_prefs_default), MODE_PRIVATE);

        setContentView(R.layout.drawer_activity_main);
        ButterKnife.bind(this);

        if(savedInstanceState != null) {
            theme = savedInstanceState.getInt(ThemeSelectActivity.KEY_THEME_ID, R.style.VkgramThemeGreengo);
            prefSettings.edit().putInt(ThemeSelectActivity.KEY_THEME_ID, theme).apply();
            if(savedInstanceState.containsKey(KEY_VK_FOLLOWERS)) {
                vkFollowers = savedInstanceState.getParcelable(KEY_VK_FOLLOWERS);
            }
        } else {
            @StyleRes int savedTheme = prefSettings.getInt(ThemeSelectActivity.KEY_THEME_ID, ThemeSelectActivity.NONE);
            if(savedTheme != ThemeSelectActivity.NONE)
                theme = savedTheme;
        }
        setTheme(theme);

        initUI();
        initSession();
    }

    private void initUI() {
        tvVkUserName = navigationView.getHeaderView(0).findViewById(R.id.vk_user_full_name);

        updateUI();

        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));
        collapsingToolbarLayout.setExpandedTitleGravity(Gravity.END|Gravity.CENTER_VERTICAL);

        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerMainLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerToggle.syncState();

        drawerMainLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);

        initTabs();
    }

    private void initTabs() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
//        TabLayout.Tab tab = tabLayout.newTab();
//        tab.setText("followers");
//        tabLayout.addTab(tab);
    }

    private void updateUI() {
        final boolean isLoggedIn = VKSdk.isLoggedIn();
        navigationView.getMenu().setGroupVisible(R.id.logged_in, isLoggedIn);
        navigationView.getMenu().setGroupVisible(R.id.logged_out, !isLoggedIn);
    }

    private void initSession() {
        VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                if (isResumed) {
                    switch (res) {
                        case LoggedOut:
                            Log.d(TAG, "wakeUpSession: invoke login");
                            tryLogin();
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
    }

    private void tryLogin() {
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
            tryLogin();
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @OnClick(R.id.fab)
    void onFabClick() {
        if (newPictureFile != null) throw new AssertionError("newPictureFile is already prepared.");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            newPictureFile = null;
            try {
                newPictureFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(newPictureFile != null) {
                Uri fileUri = FileProvider.getUriForFile(
                        this,
                        "pro.xite.dev.vkgram.fileprovider",
                        newPictureFile);

                Log.d(TAG, String.format("onFabClick: \nUri: %s\nFile:%s",
                           fileUri.toString(), newPictureFile.toString()));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takePictureIntent, INTENT_IMAGE_CAPTURE);
            }
        }
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
        outState.putInt(ThemeSelectActivity.KEY_THEME_ID, theme);
        if(vkFollowers != null) outState.putParcelable(KEY_VK_FOLLOWERS, vkFollowers);
        super.onSaveInstanceState(outState);
    }

    //TODO переписать в switch и методы
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
        if(requestCode == INTENT_IMAGE_CAPTURE) {
            if(resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: OK, INTENT_IMAGE_CAPTURE'd");
                Snackbar.make(coordinatorLayout, "Picture saved. See local album.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else {
                Log.d(TAG, "onActivityResult: FAIL, INTENT_IMAGE_CAPTURE'd");
                Snackbar.make(coordinatorLayout, "Capture canceled.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if(!newPictureFile.delete())
                    throw new AssertionError("Problem deleting canceled newPictureFile");
            }
            newPictureFile = null;
        }
        if(requestCode == VKServiceActivity.VKServiceType.Authorization.getOuterCode()) {
            VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
                @Override
                public void onResult(VKAccessToken res) {
                    Log.d(TAG, String.format("onResult: User passed Authorization\ntoken [%s]",res.accessToken));
                    onLoginStateChanged();
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

    private File createImageFile() throws IOException {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "JPEG_" + timeStamp + "_";
        final File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
            );
    }
    private void requestUserName() {
        requestUserName(VKAccessToken.currentToken().userId);
    }

    private void requestUserName(String uid) {
        VKRequest request = new VKRequest("users.get",
        VKParameters.from(
                VKApiConst.USER_ID, uid,
                VKApiConst.FIELDS, "id,first_name,last_name,sex,bdate,city,photo_200"),
        VKUsersArray.class);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onComplete(VKResponse response) {
                try {
                    VKUsersArray users = (VKUsersArray) (response.parsedModel);
                    user = users.get(0);
                    setActiveUser();
                } catch (ClassCastException e) {
                    Log.e(TAG, String.format(Locale.US, "onComplete: %s", e));
                }
            }
        });
    }

    private void setActiveUser() {
        tvVkUserName.setText(String.format("%s %s", user.first_name, user.last_name));
        tvActiveUserName.setText(String.format("%s %s", user.first_name, user.last_name));
        nivActiveUserAvatar.setImageUrl(user.photo_200, Application.getImageLoader());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_albums:
                return loadAlbums();
            case R.id.load_followers:
                requestUserName("1");
                final Fragment f = FollowersFragment.newInstance(user);
                viewPagerAdapter.addFragment(f);
                viewPagerAdapter.notifyDataSetChanged();
                return true;
            case R.id.logout:
                return logoutVk();
            case R.id.login:
                tryLogin();
                return true;
        }

        return false;
    }

    private void onLoginStateChanged() {
        if (VKSdk.isLoggedIn()) {
            requestUserName();
        } else {
            user = null;
        }
        updateUI();
    }

    private boolean logoutVk() {
        VKSdk.logout();
        onLoginStateChanged();
        return true;
    }

    private boolean loadAlbums() {
        if (user != null) {
            VKRequest request;
            request = new VKRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, user.id, "need_system", "1"));//, VKApiPhotoAlbum.class);
            request.getPreparedParameters().remove("access_token");
            try {
                Log.i(TAG, String.format("loadAlbums:\n%s", request.getPreparedRequest().getQuery().toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    VKPhotoArray users = (VKPhotoArray) (response.parsedModel);
                    super.onComplete(response);
                }

                @Override
                public void onError(VKError error) {
                    Log.e(TAG, "onError: request" + error.toString());
                    super.onError(error);
                }
            });
            return true;
        }
        return false;
    }

}

/**
https://api.vk.com/method/photos.get?user_id=455492428&v=5.52&album_id=saved
https://api.vk.com/method/photos.getAlbums?owner_id=455492428&v=5.52&count=10&need_system=1
https://vk.com/friends?id=&section=all
https://api.vk.com/method/friends.get?user_id=1&fields=id%2Cfirst_name%2Clast_name%2Csex%2Cbdate%2Ccity%2Cphoto&access_token=7a1838a401bcf4db0aca3c94d147e2cac585a281d90511de43358b7862b4e8c509580757556317d7b9659&v=5.21&lang=en&https=1
https://api.vk.com/method/friends.get?user_id=1&access_token=7a1838a401bcf4db0aca3c94d147e2cac585a281d90511de43358b7862b4e8c509580757556317d7b9659&v=5.21&lang=en&https=1
https://api.vk.com/method/users.getFollowers?user_id=1&fields=id%2Cfirst_name%2Clast_name%2Csex%2Cbdate%2Ccity%2Cphoto_100&count=333&offset=0&access_token=592a8ad912b05787d46bef75dbb203d409617a2135c1e3de9cfea31ad1ef8b66d69960900fd4e09357618&v=5.21&lang=en&https=1
https://api.vk.com/method/users.get?user_id=455492428&fields=id%2Cfirst_name%2Clast_name%2Csex%2Cbdate%2Ccity%2Cphoto_100&count=333&offset=0&access_token=592a8ad912b05787d46bef75dbb203d409617a2135c1e3de9cfea31ad1ef8b66d69960900fd4e09357618&v=5.21&lang=en&https=1

 */
