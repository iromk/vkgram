package pro.xite.dev.vkgram.main.view;

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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.vk.sdk.VKSdk;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pro.xite.dev.vkgram.R;
import pro.xite.dev.vkgram.followers.ui.FollowersFragment;
import pro.xite.dev.vkgram.followers.view.FollowersView;
import pro.xite.dev.vkgram.localalbum.LocalPicturesAlbumFragment;
import pro.xite.dev.vkgram.main.Application;
import pro.xite.dev.vkgram.main.model.VkApiViewModel;
import pro.xite.dev.vkgram.main.presenter.MainViewPresenter;
import timber.log.Timber;

public class MainActivity extends MvpAppCompatActivity implements
        MainView, FollowersView,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, MainActivity.class.getSimpleName());
    private static final int INTENT_IMAGE_CAPTURE = 0x1441;
    public static final int VK_AUTH_SERVICE_TYPE = 10485;
            // == VKServiceActivity.VKServiceType.Authorization.getOuterCode()

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
    TextView tvActiveUserNameInDrawer;

    private File newPictureFile;
    private ViewPagerAdapter viewPagerAdapter;

    @InjectPresenter MainViewPresenter p;

    private VkApiViewModel mVk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate:");

        super.onCreate(savedInstanceState);

        mVk = ViewModelProviders.of(this).get(VkApiViewModel.class);

        initUi();
    }

    @ProvidePresenter
    MainViewPresenter provideMainViewPresenter() {
        return new MainViewPresenter();
    }

    private void makeFollowersTab() {
//        final Fragment f = FollowersFragment.getInstance(vkModel.getLoggedInUser().getValue());
        final Fragment f = FollowersFragment.getInstance(mVk.getLoggedInUser());
        viewPagerAdapter.addFragment("", f);
        viewPagerAdapter.notifyDataSetChanged();
        tabLayout.getTabAt(0).setIcon(R.drawable.followers); // FIXME possible npe/bug point
        followersTab = true;
    }

    private void initUi() {
        setTheme(p.getTheme());
        setContentView(R.layout.drawer_activity_main);

        ButterKnife.bind(this);
        tvActiveUserNameInDrawer = navigationView.getHeaderView(0).findViewById(R.id.vk_user_full_name);

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

    @Override @StateStrategyType(SkipStrategy.class)
    public void invokeVkLoginActivity() {
        VKSdk.login(this);
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
/*    private void debugShowTags(String s) {
        Timber.tag(TAG).v(String.format("getTag() at %s", s));
        for(int i=0; i < getSupportFragmentManager().getFragments().size(); i++)
            Timber.tag(TAG).v("getSupportFragmentManager.getTag() == %s", getSupportFragmentManager().getFragments().get(i).getTag());

        if(viewPagerAdapter != null)
        for(int i=0; i < viewPagerAdapter.getCount(); i++)
            Timber.tag(TAG).v("viewPagerAdapter          getTag() == %s", viewPagerAdapter.getItem(i).getTag());
        else Timber.tag(TAG).v("viewPagerAdapter          getTag() == null");

    }*/

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
                VKSdk.onActivityResult(requestCode, resultCode, data, p.getOnLogin());
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void resultSwitchTheme(int resultCode, Intent data) {
        if (resultCode == ThemeSelectActivity.RESULT_THEME_CHANGED && data != null) {
            final @StyleRes int theme = data.getIntExtra(ThemeSelectActivity.KEY_THEME_ID, ThemeSelectActivity.NONE);
            if (theme != ThemeSelectActivity.NONE) {
                p.onThemeChange(theme);
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

    @Override
    public void showActiveUser() {
        Timber.v("awaiting data");
        mVk.getLoggedInUserLiveData().observe(this, u -> {
            if(u != null) {
                Timber.v("got data");
                setLoggedUserName(u.first_name, u.last_name);
                Timber.v("set user name");
                setLoggedUserAvatar(u.photo_200);
                Timber.v("set ava");
            }});
    }

    private void clearActiveUser() {
        tvActiveUserName.setText("");
        tvActiveUserNameInDrawer.setText("");
        nivActiveUserAvatar.setImageDrawable(getResources().getDrawable(R.drawable.bg_toolbar_shadow_dark));
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
                return p.onLogoutVk();
            case R.id.login:
                invokeVkLoginActivity();
                return true;
        }

        return false;
    }

    @Override
    public void setLoggedUserName(CharSequence firstName, CharSequence lastName) {
        tvActiveUserNameInDrawer.setText(String.format("%s %s", firstName, lastName));
        tvActiveUserName.setText(String.format("%s %s", firstName, lastName));
    }

    @Override
    public void setLoggedUserAvatar(String url) {
        nivActiveUserAvatar.setImageUrl(url, Application.getImageLoader());
    }

    @Override
    public void setUiStateLoggedIn() {
        navigationView.getMenu().setGroupVisible(R.id.logged_in, true);
        navigationView.getMenu().setGroupVisible(R.id.logged_out, false);
        showActiveUser();
    }

    @Override
    public void setUiStateLoggedOut() {
        navigationView.getMenu().setGroupVisible(R.id.logged_in, false);
        navigationView.getMenu().setGroupVisible(R.id.logged_out, true);
        clearActiveUser();
    }

    @Override
    public void updated() {

    }
/*

    private boolean logoutVk() {
        VKSdk.logout();
        onLoginStateChanged();
        return true;
    }
*/

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
