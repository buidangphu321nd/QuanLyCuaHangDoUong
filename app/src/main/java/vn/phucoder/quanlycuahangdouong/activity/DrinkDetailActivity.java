package vn.phucoder.quanlycuahangdouong.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.adapter.MyPagerAdapter;
import vn.phucoder.quanlycuahangdouong.model.Drink;

public class DrinkDetailActivity extends AppCompatActivity {
    private Drink mDrink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail);
        getDataIntent();
        initToolbar();
        initView();
    }
    public void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        mDrink = (Drink) bundle.get("drink_object");
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mDrink.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initView(){
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager_2);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this,mDrink);
        viewPager2.setAdapter(myPagerAdapter);
        new TabLayoutMediator(tabLayout,viewPager2,(tab, position) -> {
            if (position == 0){
                tab.setText("Đã nhập");
            }
            else {
                tab.setText("Đã Tiêu thụ");
            }
        }).attach();
    }

}