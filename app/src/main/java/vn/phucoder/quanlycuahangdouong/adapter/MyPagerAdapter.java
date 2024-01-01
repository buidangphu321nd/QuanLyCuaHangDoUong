package vn.phucoder.quanlycuahangdouong.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.phucoder.quanlycuahangdouong.fragment.DrinkDetailAddFragment;
import vn.phucoder.quanlycuahangdouong.fragment.DrinkDetailUsedFragment;
import vn.phucoder.quanlycuahangdouong.model.Drink;

public class MyPagerAdapter extends FragmentStateAdapter {
    private final Drink mDrink;

    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, Drink drink) {
        super(fragmentActivity);
        this.mDrink = drink;

    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new DrinkDetailUsedFragment(mDrink);
        }
        return new DrinkDetailAddFragment(mDrink);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
