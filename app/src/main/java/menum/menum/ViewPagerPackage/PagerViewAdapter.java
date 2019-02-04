package menum.menum.ViewPagerPackage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import menum.menum.BackgroundImagePackage.Image1;
import menum.menum.BackgroundImagePackage.Image2;
import menum.menum.BackgroundImagePackage.Image3;

/**
 * Created by deniz on 10.2.2018.
 */

public class PagerViewAdapter extends FragmentPagerAdapter {
    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Image1 image1=new Image1();
                return image1;
            case 1:
                Image2 image2=new Image2();
                return image2;
            case 2:
                Image3 image3=new Image3();
                return image3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
