package vn.phucoder.quanlycuahangdouong.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import vn.phucoder.quanlycuahangdouong.MyApplication;
import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.adapter.StatisticalAdapter;
import vn.phucoder.quanlycuahangdouong.listener.IGetDateListener;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Drink;
import vn.phucoder.quanlycuahangdouong.model.History;
import vn.phucoder.quanlycuahangdouong.model.Statistical;
import vn.phucoder.quanlycuahangdouong.untils.DateTimeUntil;
import vn.phucoder.quanlycuahangdouong.untils.StringUntil;

public class StatisticalActivity extends AppCompatActivity {
    private TextView tvTotalValue;
    private TextView tvDateFrom, tvDateTo;
    private RecyclerView rcvData;

    private int mType;
    private boolean isDrinkPopular;
    private List<Statistical> mListStatisticals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistical);

        getDataIntent();
        initToolbar();
        initUi();
        getListStatistical();
    }
    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        mType = bundle.getInt("type_statistical");
        isDrinkPopular = bundle.getBoolean("drink_popular");
    }
    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        switch (mType) {
            case 1:
                getSupportActionBar().setTitle("Doanh thu");
                break;

            case 2:
                getSupportActionBar().setTitle("Chi phí");
                break;
        }
        if (isDrinkPopular) {
            getSupportActionBar().setTitle("Bán chạy");
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
    private void initUi(){
        tvDateFrom = findViewById(R.id.tv_date_from);
        tvDateTo = findViewById(R.id.tv_date_to);
        tvTotalValue = findViewById(R.id.tv_total_value);
        rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);

        LinearLayout layoutFilter = findViewById(R.id.layout_filter);
        View viewDivider = findViewById(R.id.view_divider);
        RelativeLayout layoutBottom = findViewById(R.id.layout_bottom);
        if (isDrinkPopular) {
            layoutFilter.setVisibility(View.GONE);
            viewDivider.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
        } else {
            layoutFilter.setVisibility(View.VISIBLE);
            viewDivider.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
        }
        TextView labelTotalValue = findViewById(R.id.label_total_value);
        switch (mType) {
            case 1:
                labelTotalValue.setText("Tổng doanh thu");
                break;

            case 2:
                labelTotalValue.setText("Tổng chi phí");
                break;
        }

        tvDateFrom.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                    showDatePicker(StatisticalActivity.this, tvDateFrom.getText().toString(), date -> {
                    tvDateFrom.setText(date);
                    getListStatistical();
                });
            }
        });
        tvDateTo.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                    showDatePicker(StatisticalActivity.this, tvDateTo.getText().toString(), date -> {
                    tvDateTo.setText(date);
                    getListStatistical();
                });
            }
        });
    }

    private void getListStatistical() {
        MyApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<History> list = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            History history = dataSnapshot.getValue(History.class);
                            if (canAddHistory(history)) {
                                list.add(history);
                            }
                        }
                        handleDataHistories(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StatisticalActivity.this,"Không lấy được dữ liệu, vui lòng kiểm tra lại mạng",Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private boolean canAddHistory(History history) {
        if (history == null){
            return false;
        }
        if (mType == 1){
            if (history.isAdd()){
                return false;
            }
        } else {
            if (!history.isAdd()) {
                return false;
            }
        }
        String strDateFrom = tvDateFrom.getText().toString();
        String strDateTo = tvDateTo.getText().toString();

        if (StringUntil.isEmpty(strDateFrom) && StringUntil.isEmpty(strDateTo)) {
            return true;
        }
        if (!StringUntil.isEmpty(strDateFrom) && StringUntil.isEmpty(strDateTo)) {
            long longDateFrom = Long.parseLong(DateTimeUntil.convertDateToTimeStamp(strDateFrom));
            return history.getDate() >= longDateFrom;
        }
        long longDateTo = Long.parseLong(DateTimeUntil.convertDateToTimeStamp(strDateTo));
        long longDateFrom = Long.parseLong(DateTimeUntil.convertDateToTimeStamp(strDateFrom));
        return history.getDate() >= longDateFrom && history.getDate() <= longDateTo;
    }

    private void handleDataHistories(List<History> list) {
        // Kiểm tra xem danh sách lịch sử có tồn tại và không rỗng không
        if (list == null || list.isEmpty()) {
            // Nếu không, thoát khỏi phương thức
            return;
        }

        // Kiểm tra xem danh sách thống kê đã tồn tại hay không
        if (mListStatisticals != null) {
            // Nếu tồn tại, xóa toàn bộ phần tử trong danh sách
            mListStatisticals.clear();
        } else {
            // Nếu không tồn tại, khởi tạo danh sách thống kê mới
            mListStatisticals = new ArrayList<>();
        }

        // Duyệt qua danh sách lịch sử để xử lý
        for (History history : list) {
            long drinkId = history.getDrinkId();

            // Kiểm tra xem có thống kê đã tồn tại cho đối tượng Drink này hay không
            if (checkStatisticalExist(drinkId)) {
                // Nếu tồn tại, thêm lịch sử vào danh sách lịch sử của thống kê
                getStatisticalFromDrinkId(drinkId).getHistories().add(history);
            } else {
                // Nếu không tồn tại, tạo một đối tượng Statistical mới và thêm vào danh sách thống kê
                Statistical statistical = new Statistical();
                statistical.setDrinkId(history.getDrinkId());
                statistical.setDrinkName(history.getDrinkName());
                statistical.setDrinkUnitId(history.getUnitId());
                statistical.setDrinkUnitName(history.getUnitName());
                statistical.getHistories().add(history);
                mListStatisticals.add(statistical);
            }
        }

        // Kiểm tra xem có đang hiển thị danh sách theo mức độ phổ biến hay không
        if (isDrinkPopular) {
            // Nếu đang hiển thị theo mức độ phổ biến, sắp xếp danh sách theo tổng giá trị giảm dần
            List<Statistical> listPopular = new ArrayList<>(mListStatisticals);
            Collections.sort(listPopular, (statistical1, statistical2)
                    -> statistical2.getTotalPrice() - statistical1.getTotalPrice());
            StatisticalAdapter statisticalAdapter = new StatisticalAdapter(listPopular, statistical -> {
                // Xử lý khi người dùng nhấn vào một mục trong danh sách
                Drink drink = new Drink(statistical.getDrinkId(), statistical.getDrinkName(),
                        statistical.getDrinkUnitId(), statistical.getDrinkUnitName());
                Bundle bundle = new Bundle();
                bundle.putSerializable("drink_object",drink);
                Intent intent = new Intent(this, DrinkDetailActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            });
            rcvData.setAdapter(statisticalAdapter);
        } else {
            // Nếu không hiển thị theo mức độ phổ biến, sử dụng danh sách thống kê gốc
            StatisticalAdapter statisticalAdapter = new StatisticalAdapter(mListStatisticals, statistical -> {
                // Xử lý khi người dùng nhấn vào một mục trong danh sách
                Drink drink = new Drink(statistical.getDrinkId(), statistical.getDrinkName(),
                        statistical.getDrinkUnitId(), statistical.getDrinkUnitName());
                Bundle bundle = new Bundle();
                bundle.putSerializable("drink_object",drink);
                Intent intent = new Intent(this, DrinkDetailActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            });
            rcvData.setAdapter(statisticalAdapter);
        }

        // Tính tổng giá trị và cập nhật lên giao diện người dùng
        String strTotalValue = getTotalValues() + " 000 VNĐ";
        tvTotalValue.setText(strTotalValue);
    }

    private boolean checkStatisticalExist(long drinkId) {
        if (mListStatisticals == null || mListStatisticals.isEmpty()) {
            return false;
        }
        boolean result = false;
        for (Statistical statistical : mListStatisticals) {
            if (drinkId == statistical.getDrinkId()) {
                result = true;
                break;
            }
        }
        return result;
    }
    private Statistical getStatisticalFromDrinkId(long drinkId) {
        Statistical result = null;
        for (Statistical statistical : mListStatisticals) {
            if (drinkId == statistical.getDrinkId()) {
                result = statistical;
                break;
            }
        }
        return result;
    }
    private int getTotalValues() {
        if (mListStatisticals == null || mListStatisticals.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (Statistical statistical : mListStatisticals) {
            total += statistical.getTotalPrice();
        }
        return total;
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
}