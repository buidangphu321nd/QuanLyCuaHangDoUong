package vn.phucoder.quanlycuahangdouong.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import vn.phucoder.quanlycuahangdouong.MyApplication;
import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.adapter.ManageDrinkAdapter;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Drink;
import vn.phucoder.quanlycuahangdouong.untils.StringUntil;

public class DrinkOutOfStockActivity extends AppCompatActivity {
    private List<Drink> mListDrink;
    private ManageDrinkAdapter mManageDrinkAdapter;

    private EditText edtSearchName;
    private String mKeySeach;

    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
            Drink drink = snapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mManageDrinkAdapter == null) {
                return;
            }
            if (drink.getQuantity() <= 0){
                if (StringUntil.isEmpty(mKeySeach)){
                    mListDrink.add(0,drink);
                } else {
                    if (getTextSearch(drink.getName().toLowerCase()).contains(getTextSearch(mKeySeach))){
                        mListDrink.add(0,drink);
                    }
                }
            }
            mManageDrinkAdapter.notifyDataSetChanged();
        }
        @SuppressLint("NotifyDataSetChanged")

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {
            Drink drink = snapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mManageDrinkAdapter == null) {
                return;
            }
            if (drink.getQuantity() > 0) {
                for (Drink drinkObject : mListDrink) {
                    if (drink.getId() == drinkObject.getId()) {
                        mListDrink.remove(drinkObject);
                        break;
                    }
                }
            } else {
                for (int i = 0; i < mListDrink.size(); i++) {
                    if (drink.getId() == mListDrink.get(i).getId()) {
                        mListDrink.set(i, drink);
                        break;
                    }
                }
            }
            mManageDrinkAdapter.notifyDataSetChanged();

        }
        @SuppressLint("NotifyDataSetChanged")

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            Drink drink = snapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mManageDrinkAdapter == null) {
                return;
            }
            for (Drink drinkObject : mListDrink) {
                if (drink.getId() == drinkObject.getId()) {
                    mListDrink.remove(drinkObject);
                    break;
                }
            }
            mManageDrinkAdapter.notifyDataSetChanged();

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(DrinkOutOfStockActivity.this,"Không lấy được dữ liệu, vui lòng kiểm tra lại mạng",Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_out_of_stock);
        initToolbar();
        initUi();
        getListDrink();
    }
    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Hết hàng");
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
    public static String getTextSearch (String input){
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
    private void initUi(){
        edtSearchName = findViewById(R.id.edt_search_name);
        ImageView imgSearch = findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                searchDrink();
            }
        });

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchDrink();
                return true;
            }
            return false;
        });

        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    mKeySeach = "";
                    getListDrink();
                    hideSoftKeyboard(DrinkOutOfStockActivity.this);
                }
            }
        });

        RecyclerView rcvDrink = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvDrink.setLayoutManager(linearLayoutManager);

        mListDrink = new ArrayList<>();
        mManageDrinkAdapter = new ManageDrinkAdapter(mListDrink,drink -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("drink_object",drink);
            Intent intent = new Intent(this, DrinkDetailActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);
        });
        rcvDrink.setAdapter(mManageDrinkAdapter);
    }

    private void searchDrink() {
        if (mListDrink == null || mListDrink.isEmpty()) {
            hideSoftKeyboard(this);
            return;
        }
        mKeySeach = edtSearchName.getText().toString().trim();
        getListDrink();
        hideSoftKeyboard(this);
    }

    private void getListDrink() {
        if (mListDrink != null) {
            mListDrink.clear();
            MyApplication.get(this).getDrinkDatabaseReference().removeEventListener(mChildEventListener);
        }
        MyApplication.get(this).getDrinkDatabaseReference().addChildEventListener(mChildEventListener);
    }

    private static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}