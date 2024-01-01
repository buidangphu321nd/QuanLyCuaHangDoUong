package vn.phucoder.quanlycuahangdouong.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import vn.phucoder.quanlycuahangdouong.MyApplication;
import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.adapter.DrinkAdapter;
import vn.phucoder.quanlycuahangdouong.adapter.SelectDrinkAdapter;
import vn.phucoder.quanlycuahangdouong.adapter.SelectUnitAdapter;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Drink;
import vn.phucoder.quanlycuahangdouong.model.History;
import vn.phucoder.quanlycuahangdouong.model.Unit;
import vn.phucoder.quanlycuahangdouong.untils.StringUntil;

public class ListDrinkActivity extends AppCompatActivity {
    private List<Drink> mListDrink;
    private DrinkAdapter mDrinkAdapter;

    private List<Unit> mListUnit;
    private Unit mUnitSelected;

    private EditText edtSearchName;
    private String mKeySeach;
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
            Drink drink = snapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mDrinkAdapter == null){
                return;
            }
            if (StringUntil.isEmpty(mKeySeach)){
                mListDrink.add(0,drink);
            } else {
                if (getTextSearch(drink.getName().toLowerCase())
                        .contains(getTextSearch(mKeySeach).toLowerCase())) {
                    mListDrink.add(0,drink);
                }
            }
            mDrinkAdapter.notifyDataSetChanged();

        }
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {
            Drink drink = snapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mDrinkAdapter == null) {
                return;
            }
            for (int i =0;i< mListDrink.size();i++){
                if (drink.getId() == mListDrink.get(i).getId()){
                    mListDrink.set(i,drink);
                    break;
                }
            }
            mDrinkAdapter.notifyDataSetChanged();
        }
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            Drink drink = snapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mDrinkAdapter == null) {
                return;
            }
            for (Drink drinkObject : mListDrink) {
                if (drink.getId() == drinkObject.getId()) {
                    mListDrink.remove(drinkObject);
                    break;
                }
            }
            mDrinkAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(ListDrinkActivity.this,"Không lấy được dữ liệu, vui lòng kiểm tra kết nối mạng của bạn",Toast.LENGTH_SHORT).show();

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_drink);
        initToolbar();
        initUi();
        getListUnit();
        getListDrink();
    }



    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tên đồ uống");
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
    private void initUi(){
        edtSearchName = findViewById(R.id.edt_search_name);
        ImageView imgSearch = findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                searchDrink();
            }
        });
        edtSearchName.setOnEditorActionListener((v,actionId,event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                searchDrink();
                return true;
            }
            return false;
        });
        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0){
                    mKeySeach = "";
                    getListDrink();
                    hideSoftKeyboard(ListDrinkActivity.this);
                }
            }
        });
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_data);
        fabAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditDrink(null);
            }
        });

        LinearLayout layoutDeleteAll = findViewById(R.id.layout_delete_all);
        layoutDeleteAll.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mListDrink == null || mListDrink.isEmpty()) {
                    return;
                }
                onClickDeleteAllDrink();
            }
        });
        RecyclerView rcvDrink = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvDrink.setLayoutManager(linearLayoutManager);

        mListUnit = new ArrayList<>();
        mListDrink = new ArrayList<>();

        mDrinkAdapter = new DrinkAdapter(mListDrink, new DrinkAdapter.IManagerDrinkListener() {
            @Override
            public void editDrink(Drink drink) {
                onClickAddOrEditDrink(drink);
            }

            @Override
            public void deleteDrink(Drink drink) {
                onClickDeleteDrink(drink);
            }

            @Override
            public void onClickItemDrink(Drink drink) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("drink_object", drink);
                Intent intent = new Intent(ListDrinkActivity.this, DrinkDetailActivity.class);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent,bundle);
            }
        });
        rcvDrink.setAdapter(mDrinkAdapter);
        rcvDrink.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAdd.hide();
                } else {
                    fabAdd.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void onClickDeleteDrink(Drink drink) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá không")
                .setPositiveButton("Xoá", (dialogInterface, i)
                        -> MyApplication.get(ListDrinkActivity.this).getDrinkDatabaseReference()
                        .child(String.valueOf(drink.getId())).removeValue((error, ref) -> {
                            Toast.makeText(ListDrinkActivity.this,"Xoá đồ uống thành công",Toast.LENGTH_SHORT).show();
                            hideSoftKeyboard(ListDrinkActivity.this);
                        }))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void onClickDeleteAllDrink() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá tất cả không")
                .setPositiveButton("Xoá tất cả", (dialogInterface, i)
                        -> MyApplication.get(ListDrinkActivity.this).getDrinkDatabaseReference()
                            .removeValue((error, ref) -> {
                            Toast.makeText(ListDrinkActivity.this,"Xoá tất cả đồ uống thành công",Toast.LENGTH_SHORT).show();
                            hideSoftKeyboard(ListDrinkActivity.this);
                        }))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void onClickAddOrEditDrink(Drink drink) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            Toast.makeText(ListDrinkActivity.this,"Vui lòng nhập danh sách đơn vị ngoài màn hình chính",Toast.LENGTH_SHORT).show();
            return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_and_edit_drink);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final EditText edtDrinkName = dialog.findViewById(R.id.edt_drink_name);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAction = dialog.findViewById(R.id.tv_dialog_action);
        final Spinner spnUnit = dialog.findViewById(R.id.spinner_unit);

        SelectUnitAdapter selectUnitAdapter = new SelectUnitAdapter(this, R.layout.item_choose_option,mListUnit);
        spnUnit.setAdapter(selectUnitAdapter);
        spnUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUnitSelected = selectUnitAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //set data
        if (drink == null){
            tvTitleDialog.setText("Nhập tên đồ uống");
            tvDialogAction.setText("Thêm");
        } else {
            tvTitleDialog.setText("Chỉnh sửa tên đồ uống");
            tvDialogAction.setText("Chỉnh sửa");
            edtDrinkName.setText(drink.getName());
            spnUnit.setSelection(getPositionUnitUpdate(drink));
        }

        tvDialogCancel.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dialog.dismiss();
            }
        });

        tvDialogAction.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                String strDrinkName = edtDrinkName.getText().toString().trim();
                if (StringUntil.isEmpty(strDrinkName)) {
                    Toast.makeText(ListDrinkActivity.this,"Vui lòng nhập tên đồ uống",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isDrinkExist(strDrinkName)) {
                    Toast.makeText(ListDrinkActivity.this,"Đồ uống đã tồn tại",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (drink == null){
                    long id =System.currentTimeMillis();
                    Drink drinkObject = new Drink();
                    drinkObject.setId(id);
                    drinkObject.setName(strDrinkName);
                    drinkObject.setUnitId(mUnitSelected.getId());
                    drinkObject.setUnitName(mUnitSelected.getName());
                    MyApplication.get(ListDrinkActivity.this).getDrinkDatabaseReference()
                            .child(String.valueOf(id)).setValue(drinkObject, (error, ref) -> {
                                Toast.makeText(ListDrinkActivity.this,"Thêm đồ uống thành công",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                hideSoftKeyboard(ListDrinkActivity.this);
                            });
                } else {
                    Map <String, Object> map = new HashMap<>();
                    map.put("name", strDrinkName);
                    map.put("unitId", mUnitSelected.getId());
                    map.put("unitName", mUnitSelected.getName());
                    MyApplication.get(ListDrinkActivity.this).getDrinkDatabaseReference()
                            .child(String.valueOf(drink.getId())).updateChildren(map,(error, ref) -> {
                                Toast.makeText(ListDrinkActivity.this,"Chỉnh sửa đồ uống thành công",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                hideSoftKeyboard(ListDrinkActivity.this);
                                updateDrinkInHistory(new Drink(drink.getId(), strDrinkName,
                                        mUnitSelected.getId(), mUnitSelected.getName()));
                            });
                }
            }
        });
        dialog.show();
    }

    private void updateDrinkInHistory(Drink drink) {
        MyApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<History> list = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (history != null && history.getDrinkId() == drink.getId()) {
                                list.add(history);
                            }
                        }
                        MyApplication.get(ListDrinkActivity.this).getHistoryDatabaseReference()
                                .removeEventListener(this);
                        if (list.isEmpty()) {
                            return;
                        }
                        for (History history : list) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("drinkName", drink.getName());
                            map.put("unitId", drink.getUnitId());
                            map.put("unitName", drink.getUnitName());

                            MyApplication.get(ListDrinkActivity.this).getHistoryDatabaseReference()
                                    .child(String.valueOf(history.getId()))
                                    .updateChildren(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private boolean isDrinkExist(String strDrinkName) {
        if (mListDrink == null || mListDrink.isEmpty()) {
            return false;
        }

        for (Drink drink : mListDrink) {
            if (strDrinkName.equals(drink.getName())) {
                return true;
            }
        }

        return false;
    }

    private int getPositionUnitUpdate(Drink drink) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < mListUnit.size(); i++) {
            if (drink.getUnitId() == mListUnit.get(i).getId()) {
                return i;
            }
        }
        return 0;
    }

    private void searchDrink() {
        if (mListDrink == null || mListDrink.isEmpty()){
            hideSoftKeyboard(this);
            return;
        }
        mKeySeach = edtSearchName.getText().toString().trim();
        getListDrink();
        hideSoftKeyboard(this);
    }

    private void getListDrink() {
        if ( mListDrink != null){
            mListDrink.clear();
            MyApplication.get(this).getDrinkDatabaseReference().removeEventListener(mChildEventListener);
        }
        MyApplication.get(this).getDrinkDatabaseReference().addChildEventListener(mChildEventListener);
    }
    private void getListUnit() {
        MyApplication.get(this).getUnitDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListUnit != null) mListUnit.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Unit unitObject = dataSnapshot.getValue(Unit.class);
                    mListUnit.add(0, unitObject);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListDrinkActivity.this,"Không lấy được dữ liệu, vui lòng kiểm tra lại mạng",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private static String getTextSearch(String input) {
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
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