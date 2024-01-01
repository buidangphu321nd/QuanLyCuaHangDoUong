package vn.phucoder.quanlycuahangdouong.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import vn.phucoder.quanlycuahangdouong.MyApplication;
import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.adapter.ProfitAdapter;
import vn.phucoder.quanlycuahangdouong.listener.IGetDateListener;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Drink;
import vn.phucoder.quanlycuahangdouong.model.History;
import vn.phucoder.quanlycuahangdouong.model.Profit;
import vn.phucoder.quanlycuahangdouong.untils.DateTimeUntil;
import vn.phucoder.quanlycuahangdouong.untils.StringUntil;

public class ProfitActivity extends AppCompatActivity {
    private TextView tvTotalProfit;
    private TextView tvDateFrom, tvDateTo;
    private RecyclerView rcvData;

    private List<Profit> mListProfit;
    private ProfitAdapter mProfitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);
        initToolbar();
        initUi();
        getListProfit();
    }
    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setTitle("Lợi nhuận");
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
        tvDateFrom = findViewById(R.id.tv_date_from);
        tvDateTo = findViewById(R.id.tv_date_to);
        tvTotalProfit = findViewById(R.id.tv_total_profit);
        rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);

        tvDateFrom.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                    showDatePicker(ProfitActivity.this, tvDateFrom.getText().toString(), date -> {
                    tvDateFrom.setText(date);
                    getListProfit();
                });
            }
        });

        tvDateTo.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                    showDatePicker(ProfitActivity.this, tvDateTo.getText().toString(), date -> {
                    tvDateTo.setText(date);
                    getListProfit();
                });
            }
        });
    }

    private void getListProfit() {
        MyApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<History> list = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (canAddHistory(history)) {
                                list.add(history);
                            }
                        }
                        handleDataHistories(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProfitActivity.this,"Không lấy được dữ liệu, vui lòng kiểm tra lại mạng",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean canAddHistory(@Nullable History history) {
        if (history == null) {
            return false;
        }
        String strDateFrom = tvDateFrom.getText().toString();
        String strDateTo = tvDateTo.getText().toString();
        if (StringUntil.isEmpty(strDateFrom) && StringUntil.isEmpty(strDateTo)) {
            return true;
        }
        if (StringUntil.isEmpty(strDateFrom) && !StringUntil.isEmpty(strDateTo)) {
            long longDateTo = Long.parseLong(DateTimeUntil.convertDateToTimeStamp(strDateTo));
            return history.getDate() <= longDateTo;
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
        if (list == null || list.isEmpty()) {
            return;
        }
        if (mListProfit != null) {
            mListProfit.clear();
        } else {
            mListProfit = new ArrayList<>();
        }
        for (History history : list) {
            long drinkId = history.getDrinkId();
            if (checkProfitExist(drinkId)) {
                getProfitFromDrinkId(drinkId).getHistories().add(history);
            } else {
                Profit profit = new Profit();
                profit.setDrinkId(history.getDrinkId());
                profit.setDrinkName(history.getDrinkName());
                profit.setDrinkUnitId(history.getUnitId());
                profit.setDrinkUnitName(history.getUnitName());
                profit.getHistories().add(history);
                mListProfit.add(profit);
            }
        }
        mProfitAdapter = new ProfitAdapter(this, mListProfit, profit -> {
            Drink drink = new Drink(profit.getDrinkId(), profit.getDrinkName(),
                    profit.getDrinkUnitId(), profit.getDrinkUnitName());
            Bundle bundle = new Bundle();
            bundle.putSerializable("drink_object",drink);
            Intent intent = new Intent(this, DrinkDetailActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);
        });
        rcvData.setAdapter(mProfitAdapter);

        // Calculate total
        int profitValue = getTotalProfit();
        String strTotalProfit;
        if (profitValue > 0) {
            tvTotalProfit.setTextColor(getResources().getColor(R.color.green));
            strTotalProfit = "+" + profitValue + " 000 VNĐ";
        } else if (profitValue == 0) {
            tvTotalProfit.setTextColor(getResources().getColor(R.color.yellow));
            strTotalProfit = profitValue + " 000 VNĐ";
        } else {
            tvTotalProfit.setTextColor(getResources().getColor(R.color.red));
            strTotalProfit = profitValue + " 000 VNĐ";
        }
        tvTotalProfit.setText(strTotalProfit);
    }

    private boolean checkProfitExist(long drinkId) {
        if (mListProfit == null || mListProfit.isEmpty()) {
            return false;
        }
        boolean result = false;
        for (Profit profit : mListProfit) {
            if (drinkId == profit.getDrinkId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private Profit getProfitFromDrinkId(long drinkId) {
        Profit result = null;
        for (Profit profit : mListProfit) {
            if (drinkId == profit.getDrinkId()) {
                result = profit;
                break;
            }
        }
        return result;
    }

    private int getTotalProfit() {
        if (mListProfit == null || mListProfit.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (Profit profit : mListProfit) {
            total += profit.getProfit();
        }
        return total;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProfitAdapter != null) {
            mProfitAdapter.release();
        }
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