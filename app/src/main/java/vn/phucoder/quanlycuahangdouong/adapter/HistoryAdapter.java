package vn.phucoder.quanlycuahangdouong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.History;
import vn.phucoder.quanlycuahangdouong.untils.DateTimeUntil;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private final List<History> mListHistory;
    private final boolean mIsShowDate;
    private final IManagerHistoryListener iManagerHistoryistener;

    public interface IManagerHistoryListener {
        void editHistory(History history);

        void deleteHistory(History history);

        void onClickItemHistory(History history);
    }
    public HistoryAdapter(List<History> list, boolean isShowDate, IManagerHistoryListener listener) {
        this.mListHistory = list;
        this.mIsShowDate = isShowDate;
        this.iManagerHistoryistener = listener;
    }

    @NonNull
    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_buy_or_used,parent,false);
        return new HistoryAdapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HistoryViewHolder holder, int position) {
        History history = mListHistory.get(position);
        if (history == null) {
            return;
        }
        if (history.isAdd()){
            holder.layoutItemHistory.setBackgroundColor(R.drawable.bg_white_corner_radius_6);
        } else {
            holder.layoutItemHistory.setBackgroundColor(R.drawable.bg_gray_corner_radius_6);
        }
        if (mIsShowDate) {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(DateTimeUntil.convertTimeStampToDate(String.valueOf(history.getDate())));
            holder.layoutItemHistory.setOnClickListener(null);
        } else {
            holder.tvDate.setVisibility(View.GONE);
            holder.layoutItemHistory.setOnClickListener(new IOnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    iManagerHistoryistener.onClickItemHistory(history);
                }
            });
        }
        holder.tvDrinkName.setText(history.getDrinkName());
        String strPrice =history.getPrice() + " 000 VNĐ";
        holder.tvPrice.setText(strPrice);
        String strQuantity = history.getQuantity()+ " " + history.getUnitName();
        holder.tvQuantity.setText(strQuantity);
        String strTotalPrice = history.getTotalPrice() + " 000 VNĐ";
        holder.tvTotalPrice.setText(strTotalPrice);

        holder.imgEdit.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                iManagerHistoryistener.editHistory(history);
            }
        });

        holder.imgDelete.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                iManagerHistoryistener.deleteHistory(history);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mListHistory != null) {
            return mListHistory.size();
        }
        return 0;
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        private final LinearLayout layoutItemHistory;
        private final TextView tvDrinkName;
        private final TextView tvPrice;
        private final TextView tvQuantity;
        private final TextView tvTotalPrice;
        private final ImageView imgEdit;
        private final ImageView imgDelete;
        private final TextView tvDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItemHistory = itemView.findViewById(R.id.layout_item_history);
            tvDrinkName = itemView.findViewById(R.id.tv_drink_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
