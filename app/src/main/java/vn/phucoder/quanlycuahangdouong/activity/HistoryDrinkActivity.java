package vn.phucoder.quanlycuahangdouong.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.phucoder.quanlycuahangdouong.MyApplication;
import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.adapter.HistoryAdapter;
import vn.phucoder.quanlycuahangdouong.adapter.SelectDrinkAdapter;
import vn.phucoder.quanlycuahangdouong.listener.IGetDateListener;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Drink;
import vn.phucoder.quanlycuahangdouong.model.History;
import vn.phucoder.quanlycuahangdouong.untils.DateTimeUntil;
import vn.phucoder.quanlycuahangdouong.untils.StringUntil;

public class HistoryDrinkActivity extends AppCompatActivity {
    private TextView mTvDateSelected;
    private TextView tvTotalPrice;

    private List<Drink> mListDrink;

    private List<History> mListHistory;
    private HistoryAdapter mHistoryAdapter;

    private Drink mDrinkSelected;
    private boolean isDrinkUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_drink);
        getDataIntent();
        initToolbar();
        initUi();
        getListDrinks();
    }


    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null){
            return;
        }
        isDrinkUsed = bundle.getBoolean("drink_used");
    }
    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        if (isDrinkUsed) {
            getSupportActionBar().setTitle("Tiêu thụ");
        } else {
            getSupportActionBar().setTitle("Nhập hàng");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    private void initUi() {
        TextView tvListTitle = findViewById(R.id.tv_list_title);
        if (isDrinkUsed){
            tvListTitle.setText("Danh sách đồ uống đã tiêu thụ");
        } else {
            tvListTitle.setText("Danh sách đồ uống đã mua");
        }
        tvTotalPrice = findViewById(R.id.tv_total_price);
        mTvDateSelected = findViewById(R.id.tv_date_selected);
        String currentDate = new SimpleDateFormat(DateTimeUntil.DEFAULT_FORMAT_DATE, Locale.ENGLISH).format(new Date());
        mTvDateSelected.setText(currentDate);
        getListHistoryDrinkOfDate(currentDate);

        RelativeLayout layoutSelectDate = findViewById(R.id.layout_select_date);
        layoutSelectDate.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                showDatePicker(HistoryDrinkActivity.this,mTvDateSelected.getText().toString(),date -> {
                    mTvDateSelected.setText(date);
                    getListHistoryDrinkOfDate(date);
                });

            }
        });
        FloatingActionButton fabAddData = findViewById(R.id.fab_add_data);
        fabAddData.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditHistory(null);
            }
        });
        RecyclerView rcvHistory = findViewById(R.id.rcv_history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvHistory.setLayoutManager(linearLayoutManager);

        mListDrink = new ArrayList<>();
        mListHistory = new ArrayList<>();

        mHistoryAdapter = new HistoryAdapter(mListHistory, false,
                new HistoryAdapter.IManagerHistoryListener() {
                    @Override
                    public void editHistory(History history) {
                        onClickAddOrEditHistory(history);
                    }

                    @Override
                    public void deleteHistory(History history) {
                        onClickDeleteHistory(history);
                    }

                    @Override
                    public void onClickItemHistory(History history) {
                        Drink drink = new Drink(history.getDrinkId(), history.getDrinkName(),
                                history.getUnitId(), history.getUnitName());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("drink_object", drink);
                        Intent intent = new Intent(HistoryDrinkActivity.this, DrinkDetailActivity.class);
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent,bundle);
                    }
                });
        rcvHistory.setAdapter(mHistoryAdapter);
        rcvHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAddData.hide();
                } else {
                    fabAddData.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }
    private void getListDrinks() {
        MyApplication.get(this).getDrinkDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListDrink != null) mListDrink.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Drink drink = dataSnapshot.getValue(Drink.class);
                    mListDrink.add(0, drink);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryDrinkActivity.this,"Không lấy được dữ liệu, vui lòng kiểm tra kết nối mạng của bạn",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onClickDeleteHistory(History history) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa không?")
                .setPositiveButton("Xóa", (dialogInterface, i)
                        -> MyApplication.get(HistoryDrinkActivity.this).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .removeValue((error, ref) -> {
                            if (isDrinkUsed) {
                                Toast.makeText(HistoryDrinkActivity.this,"Xoá lịch sử tiêu thụ đồ uống thành công",Toast.LENGTH_SHORT).show();
                            } else {Toast.makeText(HistoryDrinkActivity.this,"Xoá lịch sử nhập đồ uống thành công",Toast.LENGTH_SHORT).show();

                            }
                            changeQuantity(history.getDrinkId(), history.getQuantity(), isDrinkUsed);
                            hideSoftKeyboard(HistoryDrinkActivity.this);
                        }))
                .setNegativeButton("Hủy", null)
                .show();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getListHistoryDrinkOfDate(@NonNull String date) {
        long longDate = Long.parseLong(DateTimeUntil.convertDateToTimeStamp(date));
        MyApplication.get(HistoryDrinkActivity.this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mListHistory != null) mListHistory.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            History history = dataSnapshot.getValue(History.class);
                            if (history != null){
                                if (longDate == history.getDate()){
                                    addHistoryToList(history);
                                }
                            }
                        }
                        mHistoryAdapter.notifyDataSetChanged();

                        String strTotalPrice = getTotalPrice() + " 000 VNĐ";
                        tvTotalPrice.setText(strTotalPrice);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void onClickAddOrEditHistory(History history) {
        if (mListDrink == null || mListDrink.isEmpty()) {
            Toast.makeText(HistoryDrinkActivity.this,"Vui lòng nhập danh sách đồ uống ngoài màn hình chính",Toast.LENGTH_SHORT).show();
            return;
        }
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_history);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final Spinner spnDrink = dialog.findViewById(R.id.spinner_drink);
        final EditText edtQuantity = dialog.findViewById(R.id.edt_quantity);
        final TextView tvUnitName = dialog.findViewById(R.id.tv_unit_name);
        final EditText edtPrice = dialog.findViewById(R.id.edt_price);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAdd = dialog.findViewById(R.id.tv_dialog_add);

        if (isDrinkUsed){
            tvTitleDialog.setText("Tiêu thụ");
        } else {
            tvTitleDialog.setText("Nhập hàng");
        }

        SelectDrinkAdapter selectDrinkAdapter = new SelectDrinkAdapter(this,R.layout.item_choose_option,mListDrink);
        spnDrink.setAdapter(selectDrinkAdapter);
        spnDrink.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mDrinkSelected = selectDrinkAdapter.getItem(i);
                tvUnitName.setText(mDrinkSelected.getUnitName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (history != null) {
            if (isDrinkUsed) {
                tvTitleDialog.setText("Chỉnh sửa lịch sử tiêu thụ đồ uống");
            } else {
                tvTitleDialog.setText("Chỉnh sửa lịch sử nhập đồ uống");
            }
            spnDrink.setSelection(getPositionDrinkUpdate(history));
            edtQuantity.setText(String.valueOf(history.getQuantity()));
            edtPrice.setText(String.valueOf(history.getPrice()));
        }

        tvDialogCancel.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dialog.dismiss();
            }
        });

        tvDialogAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                String strQuantity = edtQuantity.getText().toString().trim();
                String strPrice = edtPrice.getText().toString().trim();
                if (StringUntil.isEmpty(strQuantity) || StringUntil.isEmpty(strPrice)){
                    Toast.makeText(HistoryDrinkActivity.this,"Vui lòng nhập đầy đủ thông tin",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (history == null){
                    History history1 = new History();
                    history1.setId(System.currentTimeMillis());
                    history1.setDrinkId(mDrinkSelected.getId());
                    history1.setDrinkName(mDrinkSelected.getName());
                    history1.setUnitId(mDrinkSelected.getUnitId());
                    history1.setUnitName(mDrinkSelected.getUnitName());
                    history1.setQuantity(Integer.parseInt(strQuantity));
                    history1.setPrice(Integer.parseInt(strPrice));
                    history1.setTotalPrice(history1.getQuantity() * history1.getPrice());
                    history1.setAdd(!isDrinkUsed);
                    String strDate = DateTimeUntil.convertDateToTimeStamp(mTvDateSelected.getText().toString());
                    history1.setDate(Long.parseLong(strDate));

                    MyApplication.get(HistoryDrinkActivity.this).getHistoryDatabaseReference()
                            .child(String.valueOf(history1.getId()))
                            .setValue(history1, (error, ref) -> {
                                if (isDrinkUsed){
                                    Toast.makeText(HistoryDrinkActivity.this,"Thêm lịch sử tiêu thụ đồ uống thành công",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HistoryDrinkActivity.this,"Thêm đồ uống thành công",Toast.LENGTH_SHORT).show();
                                }
                                changeQuantity(history1.getDrinkId(),history1.getQuantity(),!isDrinkUsed);
                                hideSoftKeyboard(HistoryDrinkActivity.this);
                                dialog.dismiss();
                            });
                    return;
                }
                // Edit history
                Map<String, Object> map = new HashMap<>();
                map.put("drinkId", mDrinkSelected.getId());
                map.put("drinkName", mDrinkSelected.getName());
                map.put("unitId", mDrinkSelected.getUnitId());
                map.put("unitName", mDrinkSelected.getUnitName());
                map.put("quantity", Integer.parseInt(strQuantity));
                map.put("price", Integer.parseInt(strPrice));
                map.put("totalPrice", Integer.parseInt(strQuantity) * Integer.parseInt(strPrice));

                MyApplication.get(HistoryDrinkActivity.this).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .updateChildren(map, (error, ref) -> {
                            hideSoftKeyboard(HistoryDrinkActivity.this);
                            if (isDrinkUsed) {
                                Toast.makeText(HistoryDrinkActivity.this,"Chỉnh sửa lịch sử tiêu thụ đồ uống thành công",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HistoryDrinkActivity.this,"Chỉnh sửa lịch sử nhập đồ uống thành công",Toast.LENGTH_SHORT).show();
                            }
                            changeQuantity(history.getDrinkId(), Integer.parseInt(strQuantity) - history.getQuantity(), !isDrinkUsed);

                            dialog.dismiss();
                        });
            }
        });

        dialog.show();
    }

    private void changeQuantity(long drinkId, int quantity, boolean isAdd) {
        MyApplication.get(HistoryDrinkActivity.this).getQuantityDatabaseReference(drinkId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentQuantity = snapshot.getValue(Integer.class);
                        if (currentQuantity != null ){
                            int totalQuantity;
                            if (isAdd){
                                totalQuantity = currentQuantity + quantity;
                            } else{
                                totalQuantity = currentQuantity - quantity;
                            }
                            MyApplication.get(HistoryDrinkActivity.this).getQuantityDatabaseReference(drinkId).removeEventListener(this);
                            updateQuantityToFirebase(drinkId, totalQuantity);
                        }
                        
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateQuantityToFirebase(long drinkId, int quantity) {
        MyApplication.get(HistoryDrinkActivity.this).getQuantityDatabaseReference(drinkId)
                .setValue(quantity);
    }

    private int getPositionDrinkUpdate(History history) {
        if (mListDrink == null || mListDrink.isEmpty()) {
            return 0;
        }
        for (int i=0; i<mListDrink.size();i++){
            if (history.getId() == mListDrink.get(i).getId()) {
                return i;
            }
        }
        return 0;
    }

    private void addHistoryToList(History history) {
        if (history == null){
            return;
        }
        if (isDrinkUsed){
            if (!history.isAdd()){
                mListHistory.add(0,history);
            }
        } else {
            if (history.isAdd()){
                mListHistory.add(0,history);
            }
        }
    }
    private int getTotalPrice() {
        if (mListHistory == null || mListHistory.isEmpty()) {
            return 0;
        }

        int totalPrice = 0;
        for (History history : mListHistory) {
            totalPrice += history.getTotalPrice();
        }
        return totalPrice;
    }
    public static void showDatePicker(Context context, String currentDate, final IGetDateListener getDateListener) {
        Calendar mCalendar = Calendar.getInstance();
        int currentDay = mCalendar.get(Calendar.DATE);
        int currentMonth = mCalendar.get(Calendar.MONTH);
        int currentYear = mCalendar.get(Calendar.YEAR);
        mCalendar.set(currentYear, currentMonth, currentDay);

        if (!StringUntil.isEmpty(currentDate)) {
            String[] split = currentDate.split("/");
            currentDay = Integer.parseInt(split[0]);
            currentMonth = Integer.parseInt(split[1]);
            currentYear = Integer.parseInt(split[2]);
            mCalendar.set(currentYear, currentMonth - 1, currentDay);
        }

        DatePickerDialog.OnDateSetListener callBack = (view, year, monthOfYear, dayOfMonth) -> {
            String date = StringUntil.getDoubleNumber(dayOfMonth) + "/" +
                    StringUntil.getDoubleNumber(monthOfYear + 1) + "/" + year;
            getDateListener.getDate(date);
        };
        DatePickerDialog datePicker = new DatePickerDialog(context,
                callBack, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DATE));
        datePicker.show();
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