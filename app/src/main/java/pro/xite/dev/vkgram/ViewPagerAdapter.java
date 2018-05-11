package pro.xite.dev.vkgram;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment f = null;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment f) {
        this.f = f;
    }

    @Override
    public Fragment getItem(int position) {
        return f;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "followers";
    }

    @Override
    public int getCount() {
        return 1;
    }
}
