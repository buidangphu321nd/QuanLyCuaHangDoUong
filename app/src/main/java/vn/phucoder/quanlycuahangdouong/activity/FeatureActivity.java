package vn.phucoder.quanlycuahangdouong.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.adapter.FeatureAdapter;
import vn.phucoder.quanlycuahangdouong.model.Feature;

public class FeatureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        initUI();
    }

    private void initUI() {
        RecyclerView rcvFeature = findViewById(R.id.rcv_feature);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvFeature.setLayoutManager(gridLayoutManager);

        FeatureAdapter featureAdapter = new FeatureAdapter(getListFeature(),this::onClickItemFeature);
        rcvFeature.setAdapter(featureAdapter);
    }

    private List<Feature> getListFeature() {

        List<Feature> list = new ArrayList<>();
        list.add(new Feature(Feature.FEATURE_MANAGE_UNIT,R.drawable.ic_manage_unit,"Tên đơn vị"));
        list.add(new Feature(Feature.FEATURE_LIST_MENU, R.drawable.ic_list_drink, "Tên đồ uống"));
        list.add(new Feature(Feature.FEATURE_ADD_DRINK, R.drawable.ic_add_drink, "Nhập hàng"));
        list.add(new Feature(Feature.FEATURE_DRINK_USED, R.drawable.ic_drink_used, "Tiêu thụ"));
        list.add(new Feature(Feature.FEATURE_MANAGE_DRINK, R.drawable.ic_manage_drink, "Quản lý"));
        list.add(new Feature(Feature.FEATURE_DRINK_OUT_OF_STOCK, R.drawable.ic_drink_out_of_stock, "Hết hàng"));
        list.add(new Feature(Feature.FEATURE_REVELUE, R.drawable.ic_revenue, "Doanh thu"));
        list.add(new Feature(Feature.FEATURE_COST, R.drawable.ic_cost, "Chi phí"));
        list.add(new Feature(Feature.FEATURE_PROFIT, R.drawable.ic_profit, "Lợi nhuận"));
        list.add(new Feature(Feature.FEATURE_DRINK_POPULAR, R.drawable.ic_drink_popular, "Bán chạy"));
        return list;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showDialogExitApp();
    }

    private void showDialogExitApp() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Bạn có chắc chắn muốn thoát ứng dụng không?")
                .setPositiveButton("Đồng ý", (dialogInterface, i) -> finishAffinity())
                .setNegativeButton("Hủy", null)
                .show();
    }
    public void onClickItemFeature(Feature feature){
        switch (feature.getId()){
            case Feature.FEATURE_MANAGE_UNIT:
                Intent intent_manage_unit = new Intent(FeatureActivity.this, UnitActivity.class);
                startActivity(intent_manage_unit);
                break;
            case Feature.FEATURE_LIST_MENU:
                Intent intent_list_menu = new Intent(FeatureActivity.this, ListDrinkActivity.class);
                startActivity(intent_list_menu);
                break;
            case Feature.FEATURE_ADD_DRINK:
                Intent intent_add_drink = new Intent(FeatureActivity.this, HistoryDrinkActivity.class);
                startActivity(intent_add_drink);
                break;
            case Feature.FEATURE_DRINK_USED:
                Bundle bundle_drink_used = new Bundle();
                bundle_drink_used.putBoolean("drink_used", true);
                Intent intent_drink_used = new Intent(FeatureActivity.this, HistoryDrinkActivity.class);
                intent_drink_used.putExtras(bundle_drink_used);
                intent_drink_used.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_drink_used,bundle_drink_used);
                break;
            case Feature.FEATURE_MANAGE_DRINK:
                Intent intent_manage_drink = new Intent(FeatureActivity.this, ManageDrinkActivity.class);
                startActivity(intent_manage_drink);
                break;
            case Feature.FEATURE_DRINK_OUT_OF_STOCK:
                Intent intent_drink_out_of_stock = new Intent(FeatureActivity.this, DrinkOutOfStockActivity.class);
                startActivity(intent_drink_out_of_stock);
                break;
            case Feature.FEATURE_REVELUE:
                Bundle bundle_revelue = new Bundle();
                bundle_revelue.putInt("type_statistical", 1);
                Intent intent_revelue = new Intent(FeatureActivity.this, StatisticalActivity.class);
                intent_revelue.putExtras(bundle_revelue);
                intent_revelue.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_revelue,bundle_revelue);
                break;
            case Feature.FEATURE_COST:
                Bundle bundle_cost = new Bundle();
                bundle_cost.putInt("type_statistical", 2);
                Intent intent_cost = new Intent(FeatureActivity.this, StatisticalActivity.class);
                intent_cost.putExtras(bundle_cost);
                intent_cost.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_cost,bundle_cost);
                break;
            case Feature.FEATURE_PROFIT:
                Intent intent_profit = new Intent(FeatureActivity.this, ProfitActivity.class);
                startActivity(intent_profit);
                break;
            case Feature.FEATURE_DRINK_POPULAR:
                Bundle bundle_drink_popular = new Bundle();
                bundle_drink_popular.putInt("type_statistical", 1);
                bundle_drink_popular.putBoolean("drink_popular", true);
                Intent intent_drink_popular = new Intent(FeatureActivity.this, StatisticalActivity.class);
                intent_drink_popular.putExtras(bundle_drink_popular);
                intent_drink_popular.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_drink_popular,bundle_drink_popular);
                break;
        }



    }
}