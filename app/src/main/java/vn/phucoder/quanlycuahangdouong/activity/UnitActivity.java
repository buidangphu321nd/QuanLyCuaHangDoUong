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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import vn.phucoder.quanlycuahangdouong.adapter.UnitAdapter;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Drink;
import vn.phucoder.quanlycuahangdouong.model.History;
import vn.phucoder.quanlycuahangdouong.model.Unit;
import vn.phucoder.quanlycuahangdouong.untils.StringUntil;

public class UnitActivity extends AppCompatActivity {
    private List<Unit> mListUnit;
    private UnitAdapter mUnitAdapter;

    private EditText edtSearchName;
    private String mKeySeach;
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
            Log.d("FirebaseData", "onChildAdded: " + snapshot.toString());
            Unit unit = snapshot.getValue(Unit.class);
            if (unit == null || mListUnit == null || mUnitAdapter == null){
                return;
            }
            if (StringUntil.isEmpty(mKeySeach)){
                mListUnit.add(0,unit);
            } else if (getTextSearch(unit.getName().toLowerCase()).contains(getTextSearch(mKeySeach).toLowerCase())){
                mListUnit.add(0,unit);
            }
            mUnitAdapter.notifyDataSetChanged();
        }
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {
            Unit unit = snapshot.getValue(Unit.class);
            if (unit == null || mListUnit == null || mUnitAdapter == null){
                return;
            }
            for (int i = 0;i<mListUnit.size();i++){
                if (unit.getId() == mListUnit.get(i).getId()){
                    mListUnit.set(i,unit);
                    break;
                }
            }
            mUnitAdapter.notifyDataSetChanged();
        }
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            Unit unit = snapshot.getValue(Unit.class);
            if (unit == null || mListUnit == null || mListUnit.isEmpty() || mUnitAdapter == null) {
                return;
            }
            for (Unit u : mListUnit){
                if (unit.getId()==u.getId()){
                    mListUnit.remove(unit);
                    break;
                }
            }
            mUnitAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(UnitActivity.this,"Không lấy được dữ liệu, vui lòng kiểm tra kết nối mạng của bạn",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);
        initToolbar();
        initUI();
        getListUnit();
    }
    private void initToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Tên đơn vị");
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
    private void initUI(){
        edtSearchName = findViewById(R.id.edt_search_name);
        ImageView img_search = findViewById(R.id.img_search);
        img_search.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                searchUnit();
            }
        });
        edtSearchName.setOnEditorActionListener((v,actionId,event)->{
            if (actionId== EditorInfo.IME_ACTION_SEARCH){
                searchUnit();
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
            public void afterTextChanged(Editable editable) {
                String strKey = editable.toString().trim();
                if(strKey.equals("") || strKey.length() ==0 ){
                    mKeySeach = "";
                    getListUnit();
                    hideSoftKeyboard(UnitActivity.this);
                }
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add_data);
        fabAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                try {
                    onClickAddOrEditUnit(null);
                } catch (Exception e) {
                    System.out.println("ZZZZ ->>>>> " + e);
                }

            }
        });

        LinearLayout layoutDeleteAll =  findViewById(R.id.layout_delete_all);
        layoutDeleteAll.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (mListUnit == null || mListUnit.isEmpty()) {
                    return;
                }
                onClickDeleteAllUnit();
            }
        });

        RecyclerView rcvUnit = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvUnit.setLayoutManager(linearLayoutManager);
        mListUnit = new ArrayList<>();
        mUnitAdapter = new UnitAdapter(mListUnit, new UnitAdapter.IManagerUnitListener() {
            @Override
            public void editUnit(Unit unit) {
                onClickAddOrEditUnit(unit);
            }

            @Override
            public void deleteUnit(Unit unit) {
                onClickDeleteUnit(unit);
            }
        });
        rcvUnit.setAdapter(mUnitAdapter);
        rcvUnit.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy>0){
                    fabAdd.hide();
                } else {
                    fabAdd.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getListUnit() {
        if (mListUnit != null ){
            mListUnit.clear();
            MyApplication.get(this).getUnitDatabaseReference().removeEventListener(mChildEventListener);
        }
        MyApplication.get(this).getUnitDatabaseReference().addChildEventListener(mChildEventListener);
    }

    private void searchUnit() {
        if (mListUnit == null || mListUnit.isEmpty()){
            hideSoftKeyboard(this);
            return;
        }
        mKeySeach = edtSearchName.getText().toString().trim();
        getListUnit();
        hideSoftKeyboard(this);
    }


    private void onClickAddOrEditUnit(Unit unit) {
        Dialog dialog = new Dialog(UnitActivity.this);
        dialog.setContentView(R.layout.layout_dialog_add_and_edit_unit);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        //Get view
        TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        EditText edtUnitName = dialog.findViewById(R.id.edt_unit_name);
        TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        TextView tvDialogAction = dialog.findViewById(R.id.tv_dialog_action);

        //set data
        if (unit == null ){
            tvTitleDialog.setText("Nhập tên đơn vị");
            tvDialogAction.setText("Thêm");
        }else {
            tvTitleDialog.setText("Chỉnh sửa tên đơn vị");
            tvDialogAction.setText("Chỉnh sửa");
            edtUnitName.setText(unit.getName());
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
                String strUnitName = edtUnitName.getText().toString().trim();
                if (StringUntil.isEmpty(strUnitName)){
                    Toast.makeText(UnitActivity.this,"Vui lòng nhập tên đơn vị",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isUnitExist(strUnitName)){
                    Toast.makeText(UnitActivity.this,"Đơn vị đã tồn tại",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (unit == null ){
                    long id =System.currentTimeMillis();
                    Unit unit_new = new Unit();
                    unit_new.setId(id);
                    unit_new.setName(strUnitName);
                    MyApplication.get(UnitActivity.this).getUnitDatabaseReference()
                            .child(String.valueOf(id)).setValue(unit_new,(error, ref) -> {
                                if (error != null) {
                                    // Ghi log thông báo lỗi
                                    Log.e("Firebase", "Lỗi khi thêm đơn vị: " + error.getMessage());
                                    Toast.makeText(UnitActivity.this, "Lỗi khi thêm đơn vị", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UnitActivity.this, "Thêm đơn vị thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    hideSoftKeyboard(UnitActivity.this);
                                }
                            });
                } else {
                    Map <String,Object> map = new HashMap<>();
                    map.put("name",strUnitName);
                    MyApplication.get(UnitActivity.this).getUnitDatabaseReference()
                            .child(String.valueOf(unit.getId())).updateChildren(map, (error, ref) -> {
                                if (error != null) {
                                    // Ghi log thông báo lỗi
                                    Log.e("Firebase", "Lỗi khi cập nhật đơn vị: " + error.getMessage());
                                    Toast.makeText(UnitActivity.this, "Lỗi khi chỉnh sửa đơn vị", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UnitActivity.this, "Chỉnh sửa đơn vị thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    hideSoftKeyboard(UnitActivity.this);
                                    updateUnitInDrink(new Unit(unit.getId(), strUnitName));
                                    updateUnitInHistory(new Unit(unit.getId(), strUnitName));
                                }
                            });
                }
            }
        });
        dialog.show();
    }
    private void onClickDeleteUnit(Unit unit) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa không?")
                .setPositiveButton("Xoá", (dialogInterface, i)
                        -> MyApplication.get(UnitActivity.this).getUnitDatabaseReference()
                        .child(String.valueOf(unit.getId())).removeValue((error, ref) -> {
                            Toast.makeText(UnitActivity.this,"Xoá đơn vị thành công",Toast.LENGTH_SHORT).show();
                            hideSoftKeyboard(UnitActivity.this);
                        }))
                .setNegativeButton("Huỷ",null)
                .show();
    }

    private void onClickDeleteAllUnit() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá tất cả không?")
                .setPositiveButton("Xoá tất cả", (dialogInterface, i) ->
                        MyApplication.get(UnitActivity.this).getUnitDatabaseReference()
                                .removeValue((error, ref) -> {
                                    Toast.makeText(UnitActivity.this,"Xoá tất cả đơn vị thành công",Toast.LENGTH_SHORT).show();
                                    hideSoftKeyboard(UnitActivity.this);
                                }))
                .setNegativeButton("Huỷ",null)
                .show();

    }


    private boolean isUnitExist(String unitName) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            return false;
        }

        for (Unit unit : mListUnit) {
            if (unitName.equals(unit.getName())) {
                return true;
            }
        }

        return false;
    }
    private void updateUnitInHistory(Unit unit) {
        MyApplication.get(this).getUnitDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Drink> list = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Drink drink = dataSnapshot.getValue(Drink.class);
                            if (drink != null && drink.getUnitId() == unit.getId()){
                                list.add(drink);
                            }
                        }
                        MyApplication.get(UnitActivity.this).getDrinkDatabaseReference().removeEventListener(this);
                        if (list.isEmpty()){
                            return;
                        }
                        for (Drink drink : list){
                            MyApplication.get(UnitActivity.this).getDrinkDatabaseReference()
                                    .child(String.valueOf(drink.getId()))
                                    .child("unitName").setValue(unit.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateUnitInDrink(Unit unit) {
        MyApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<History> list = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (history != null && history.getUnitId() == unit.getId()) {
                                list.add(history);
                            }
                        }
                        MyApplication.get(UnitActivity.this).getHistoryDatabaseReference()
                                .removeEventListener(this);
                        if (list.isEmpty()) {
                            return;
                        }
                        for (History history : list) {
                            MyApplication.get(UnitActivity.this).getHistoryDatabaseReference()
                                    .child(String.valueOf(history.getId()))
                                    .child("unitName").setValue(unit.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
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