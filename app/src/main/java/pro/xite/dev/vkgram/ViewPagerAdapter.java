package pro.xite.dev.vkgram;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, ViewPagerAdapter.class.getSimpleName());

    private ArrayList<Pair<CharSequence, Fragment>> tabItems = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        if(fm.getFragments().size() > 0) {
            for(Fragment f: fm.getFragments()) {
                tabItems.add(Pair.create("XYZ", f));
                Log.d(TAG, String.format("ViewPagerAdapter: %s", f.toString()));
            }
        }
    }

    public void addFragment(CharSequence title, Fragment fragment) {
        this.tabItems.add(Pair.create(title, fragment));
        Log.d(TAG, String.format("ViewPagerAdapter: %s", fragment.toString()));
    }

    @Override
    public Fragment getItem(int position) {
        return tabItems.get(position).second;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabItems.get(position).first;
    }

    @Override
    public int getCount() {
        return tabItems.size();
    }
}
