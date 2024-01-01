package vn.phucoder.quanlycuahangdouong.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.phucoder.quanlycuahangdouong.MyApplication;
import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.activity.DrinkDetailActivity;
import vn.phucoder.quanlycuahangdouong.adapter.HistoryAdapter;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Drink;
import vn.phucoder.quanlycuahangdouong.model.History;
import vn.phucoder.quanlycuahangdouong.untils.DateTimeUntil;
import vn.phucoder.quanlycuahangdouong.untils.StringUntil;

public class DrinkDetailAddFragment extends Fragment {
    private View mView;
    private TextView tvTotalPrice;
    private TextView tvTotalQuantity;
    private final Drink mDrink;
    private List<History> mListHistory;
    private HistoryAdapter mHistoryAdapter;

    public DrinkDetailAddFragment(Drink drink) {
        this.mDrink = drink;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_drink_detail_added,container,false);
        initUi();
        getListHistoryAdded();
        return mView;

    }

    public void initUi(){
        tvTotalQuantity = mView.findViewById(R.id.tv_total_quantity);
        tvTotalPrice = mView.findViewById(R.id.tv_total_price);
        RecyclerView rcvHistory = mView.findViewById(R.id.rcv_history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvHistory.setLayoutManager(linearLayoutManager);

        mListHistory = new ArrayList<>();
        mHistoryAdapter = new HistoryAdapter(mListHistory, true, new HistoryAdapter.IManagerHistoryListener() {
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

            }
        });
        rcvHistory.setAdapter(mHistoryAdapter);

        FloatingActionButton fabAddData = mView.findViewById(R.id.fab_add_data);
        fabAddData.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                onClickAddOrEditHistory(null);
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void getListHistoryAdded(){
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mListHistory != null) {
                            mListHistory.clear();
                        }
                       for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                           History history = dataSnapshot.getValue(History.class);
                           if (history != null){
                               if (mDrink.getId() == history.getDrinkId() && history.isAdd()){
                                   mListHistory.add(0, history);
                               }
                           }
                       }
                        mHistoryAdapter.notifyDataSetChanged();

                        displayLayoutBottomInfor();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Lỗi truy cập data, vui lòng kiểm tra lại mạng",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayLayoutBottomInfor() {
        // Calculator quantity
        String strTotalQuantity = getTotalQuantity() + " " + mDrink.getUnitName();
        tvTotalQuantity.setText(strTotalQuantity);
        // Calculator price
        String strTotalPrice = getTotalPrice() + " 000 VNĐ";
        tvTotalPrice.setText(strTotalPrice);
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

    private int getTotalQuantity() {
        if (mListHistory == null || mListHistory.isEmpty()) {
            return 0;
        }

        int totalQuantity = 0;
        for (History history : mListHistory) {
            totalQuantity += history.getQuantity();
        }
        return totalQuantity;
    }


    private void onClickAddOrEditHistory(History history) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_detail_drink_edit);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final TextView tvDrinkName = dialog.findViewById(R.id.tv_drink_name);
        final EditText edtQuantity = dialog.findViewById(R.id.edt_quantity);
        final TextView tvUnitName = dialog.findViewById(R.id.tv_unit_name);
        final EditText edtPrice = dialog.findViewById(R.id.edt_price);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAdd = dialog.findViewById(R.id.tv_dialog_add);

        //set data
        if (history == null){
            tvTitleDialog.setText("Nhập hàng");
            tvDrinkName.setText(mDrink.getName());
            tvUnitName.setText(mDrink.getUnitName());
        } else {
            tvTitleDialog.setText("Chỉnh sửa lịch sử nhập đồ uống");
            tvDrinkName.setText(history.getDrinkName());
            tvUnitName.setText(history.getUnitName());
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
                if (StringUntil.isEmpty(strQuantity) || StringUntil.isEmpty(strPrice)) {
                    Toast.makeText(getActivity(),"Vui lòng nhập đầy đủ thông tin",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (history == null) {
                    History history = new History();
                    history.setId(System.currentTimeMillis());
                    history.setDrinkId(mDrink.getId());
                    history.setDrinkName(mDrink.getName());
                    history.setUnitId(mDrink.getUnitId());
                    history.setUnitName(mDrink.getUnitName());
                    history.setQuantity(Integer.parseInt(strQuantity));
                    history.setPrice(Integer.parseInt(strPrice));
                    history.setTotalPrice(history.getQuantity() * history.getPrice());
                    history.setAdd(true);

                    String currentDate = new SimpleDateFormat(DateTimeUntil.DEFAULT_FORMAT_DATE, Locale.ENGLISH).format(new Date());
                    String strDate = DateTimeUntil.convertDateToTimeStamp(currentDate);
                    history.setDate(Long.parseLong(strDate));

                    if (getActivity() != null){
                        MyApplication.get(getActivity()).getHistoryDatabaseReference()
                                .child(String.valueOf(history.getId())).setValue(history, (error, ref) -> {
                                    Toast.makeText(getActivity(),"Thêm đồ uống thành công",Toast.LENGTH_SHORT).show();
                                    changeQuantity(history.getDrinkId(),history.getQuantity(),true);
                                    hideSoftKeyboard(getActivity());
                                    dialog.dismiss();
                                });
                    }
                } else {
                        //edit history
                        Map<String,Object> map = new HashMap<>();
                        map.put("quantity",Integer.parseInt(strQuantity));
                        map.put("price",Integer.parseInt(strPrice));
                        map.put("totalPrice", Integer.parseInt(strQuantity) * Integer.parseInt(strPrice));

                        if (getActivity() != null){
                            MyApplication.get(getActivity()).getHistoryDatabaseReference()
                                    .child(String.valueOf(history.getId()))
                                    .updateChildren(map, (error, ref) -> {
                                        hideSoftKeyboard(getActivity());
                                        Toast.makeText(getActivity(),"Chỉnh sửa lịch sử nhập đồ uống thành công",Toast.LENGTH_SHORT).show();
                                        changeQuantity(history.getDrinkId(), history.getQuantity(), true);
                                        dialog.dismiss();
                                    });
                        }

                }
            }
        });
        dialog.show();

    }

    private void changeQuantity(long drinkId, int quantity, boolean isAdd) {
        if (getActivity() == null){
            return;
        }
        MyApplication.get(getActivity()).getQuantityDatabaseReference(drinkId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentQuantity = snapshot.getValue(Integer.class);
                        if (currentQuantity != null){
                            int totalQuantity;
                            if (isAdd){
                                totalQuantity = currentQuantity + quantity;
                            }
                            else {
                                totalQuantity = currentQuantity - quantity;
                            }
                            if (getActivity() != null){
                                MyApplication.get(getActivity()).getQuantityDatabaseReference(drinkId).removeEventListener(this);
                                MyApplication.get(getActivity()).getQuantityDatabaseReference(drinkId).setValue(totalQuantity);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void onClickDeleteHistory(History history) {
        if (getActivity() == null) {
            return;
        }
        new AlertDialog.Builder(getActivity())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa không?")
                .setPositiveButton("Xóa", (dialogInterface, i)
                        -> MyApplication.get(getActivity()).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .removeValue((error, ref) -> {
                            Toast.makeText(getActivity(),"Xoá lịch sử nhập đồ uống thành công",Toast.LENGTH_SHORT).show();
                            changeQuantity(history.getDrinkId(), history.getQuantity(), false);
                            hideSoftKeyboard(getActivity());
                        }))
                .setNegativeButton("Hủy", null)
                .show();
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
