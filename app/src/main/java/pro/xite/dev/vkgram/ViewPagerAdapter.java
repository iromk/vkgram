package pro.xite.dev.vkgram;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Pair<CharSequence, Fragment>> tabItems = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(CharSequence title, Fragment fragment) {
        this.tabItems.add(Pair.create(title, fragment));
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
