package pro.xite.dev.vkgram.main.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.model.VKApiUserFull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pro.xite.dev.vkgram.main.Application;
import pro.xite.dev.vkgram.R;
import pro.xite.dev.vkgram.main.model.VkViewModel;
import pro.xite.dev.vkgram.followers.FollowersFragment;
import pro.xite.dev.vkgram.localalbum.LocalPicturesAlbumFragment;
import pro.xite.dev.vkgram.util.statekeeper.StateKeeper;
import timber.log.Timber;

public class MainActivity extends MvpAppCompatActivity implements
        MainView,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, MainActivity.class.getSimpleName());
    private static final int INTENT_IMAGE_CAPTURE = 0x1441;
    public static final int VK_AUTH_SERVICE_TYPE = 10485;

    private boolean isResumed = false;

//    @KeepState(ThemeSelectActivity.KEY_THEME_ID)
//    @StyleRes
    private int theme;

//    @KeepState
    private boolean followersTab;

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
    private File newPictureFile;
    private ViewPagerAdapter viewPagerAdapter;

    private VkViewModel vkModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:");

        super.onCreate(savedInstanceState);
        vkModel = ViewModelProviders.of(this).get(VkViewModel.class);

        setDefaults();
        loadPreferences();
        StateKeeper.unbundle(savedInstanceState, this);

        setTheme(theme);
        setContentView(R.layout.drawer_activity_main);

        ButterKnife.bind(this);
        initUi();
        initSession();
        updateUi(VKSdk.isLoggedIn());
        initTabs();

        debugShowTags("onCreate");

        setActiveUser(); 

        if(savedInstanceState != null) {
            Application.settings().edit().putInt(ThemeSelectActivity.KEY_THEME_ID, theme).apply();

//            if(followersTab) makeFollowersTab();

            debugShowTags("onRecreate");
        }
    }

    private void makeFollowersTab() {
        final Fragment f = FollowersFragment.newInstance(vkModel.getLoggedInUser().getValue());
        viewPagerAdapter.addFragment("", f);
        viewPagerAdapter.notifyDataSetChanged();
        tabLayout.getTabAt(0).setIcon(R.drawable.followers); // FIXME possible npe/bug point
        followersTab = true;
    }

    private void setDefaults() {
        theme = R.style.VkgramTheme_Greengo;
        followersTab = false;
    }

    private void loadPreferences() {
        @StyleRes int savedTheme = Application.settings().getInt(ThemeSelectActivity.KEY_THEME_ID, ThemeSelectActivity.NONE);
        if(savedTheme != ThemeSelectActivity.NONE)
            theme = savedTheme;
    }

    private void initUi() {
        tvVkUserName = navigationView.getHeaderView(0).findViewById(R.id.vk_user_full_name);

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
    }

    private void initTabs() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
//        TabLayout.Tab tab = tabLayout.newTab();
//        tab.setText("followers");
//        tabLayout.addTab(tab);
    }

    private void updateUi(final boolean isLoggedIn) {
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
                            Timber.tag(TAG).d("wakeUpSession: invoke login");
                            tryLogin();
                            break;
                        case LoggedIn:
                            Timber.tag(TAG).d("wakeUpSession: already logged in");
                            break;
                        case Pending:
                            Timber.tag(TAG).d("wakeUpSession: pending state");
                            break;
                        case Unknown:
                            Timber.tag(TAG).d("wakeUpSession: unknown state");
                            break;
                    }
                }
            }

            @Override
            public void onError(VKError error) {
                Timber.tag(TAG).e("wakeUpSession.onError: %s", error.toString());
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
            Timber.tag(TAG).d("onResume: logged in");
            vkModel.getLoggedInUser();
        } else {
            Timber.tag(TAG).d("onResume: not logged in");
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

                Timber.tag(TAG).d("onFabClick: \nUri: %s\nFile:%s",
                        fileUri.toString(), newPictureFile.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takePictureIntent, INTENT_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_settings) {
            final Intent intent = new Intent(this, ThemeSelectActivity.class);
            startActivityForResult(intent, ThemeSelectActivity.REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Timber.tag(TAG).d("onSaveInstanceState: ");
        debugShowTags("onSaveInstanceState");
        StateKeeper.bundle(this, outState);
        super.onSaveInstanceState(outState);
    }

    private void debugShowTags(String s) {
        Timber.tag(TAG).v(String.format("getTag() at %s", s));
        for(int i=0; i < getSupportFragmentManager().getFragments().size(); i++)
            Timber.tag(TAG).v("getSupportFragmentManager.getTag() == %s", getSupportFragmentManager().getFragments().get(i).getTag());

        if(viewPagerAdapter != null)
        for(int i=0; i < viewPagerAdapter.getCount(); i++)
            Timber.tag(TAG).v("viewPagerAdapter          getTag() == %s", viewPagerAdapter.getItem(i).getTag());
        else Timber.tag(TAG).v("viewPagerAdapter          getTag() == null");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.tag(TAG).w("onActivityResult: requestCode == %d, resultCode == %d", requestCode, resultCode);

        switch (requestCode) {
            case ThemeSelectActivity.REQUEST_CODE:
                resultSwitchTheme(resultCode, data);
                break;
            case INTENT_IMAGE_CAPTURE:
                resultCaptureImage(resultCode);
                break;
            case VK_AUTH_SERVICE_TYPE:
                resultVkAuthorization(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void resultSwitchTheme(int resultCode, Intent data) {
        if (resultCode == ThemeSelectActivity.RESULT_THEME_CHANGED && data != null) {
            final @StyleRes int theme = data.getIntExtra(ThemeSelectActivity.KEY_THEME_ID, ThemeSelectActivity.NONE);
            if (theme != ThemeSelectActivity.NONE) {
                this.theme = theme;
                recreate();
            }
        }
    }

    private void resultCaptureImage(int resultCode) {
        if (resultCode == RESULT_OK) {
            Timber.tag(TAG).d("onActivityResult: OK, INTENT_IMAGE_CAPTURE'd");
            Snackbar.make(coordinatorLayout, "Picture saved. See local album.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Timber.tag(TAG).d("onActivityResult: FAIL, INTENT_IMAGE_CAPTURE'd");
            Snackbar.make(coordinatorLayout, "Capture canceled.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            if (!newPictureFile.delete())
                throw new AssertionError("Problem deleting canceled newPictureFile");
        }
        newPictureFile = null;
    }

    private void resultVkAuthorization(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Timber.tag(TAG).d("onResult: User passed Authorization\ntoken [%s]", res.accessToken);
                onLoginStateChanged();
            }

            @Override
            public void onError(VKError error) {
                Timber.tag(TAG).d("onResult: User didn't pass Authorization");
            }
        };
        VKSdk.onActivityResult(requestCode, resultCode, data, callback);
    }

    private File createImageFile() throws IOException {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "JPEG_" + timeStamp + "_";
        final File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Timber.tag(TAG).d("createImageFile: %s %s", storageDir.toString(), imageFileName.toString());
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
            );
    }

    private void setActiveUser() {
        final Observer<VKApiUserFull> activeUserObserver = u -> {
            if(u != null) {
                tvVkUserName.setText(String.format("%s %s", u.first_name, u.last_name));
                tvActiveUserName.setText(String.format("%s %s", u.first_name, u.last_name));
                nivActiveUserAvatar.setImageUrl(u.photo_200, Application.getImageLoader());
            }
        };
        vkModel.getLoggedInUser().observe(this, activeUserObserver);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_albums:
                viewPagerAdapter.addFragment(
                        "My pics",
                        LocalPicturesAlbumFragment.create());
                viewPagerAdapter.notifyDataSetChanged();
                return true; //loadAlbums();
            case R.id.load_followers:
                makeFollowersTab();
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
            vkModel.getLoggedInUser();
            updateUi(true);
        } else {
            vkModel.clearLoggedInUser();
            updateUi(false);
        }
    }

    private boolean logoutVk() {
        VKSdk.logout();
        onLoginStateChanged();
        return true;
    }

/*
    private boolean loadAlbums() {
        if (vkModel.getLoggedInUser() != null) {
            VKRequest request;
            request = new VKRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, vkModel.getLoggedInUser().id, "need_system", "1"));//, VKApiPhotoAlbum.class);
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
*/

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
