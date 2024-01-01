package vn.phucoder.quanlycuahangdouong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Profit;

public class ProfitAdapter extends RecyclerView.Adapter<ProfitAdapter.StatisticalViewHolder> {

    private Context mContext;
    private final List<Profit> mListProfit;
    private final IManagerProfitListener iManagerProfitListener;

    public interface IManagerProfitListener {
        void onClickItem (Profit profit);
    }

    public ProfitAdapter(Context mContext, List<Profit> mListProfit,
                         IManagerProfitListener iManagerProfitListener) {
        this.mContext = mContext;
        this.mListProfit = mListProfit;
        this.iManagerProfitListener = iManagerProfitListener;
    }

    @NonNull
    @Override
    public ProfitAdapter.StatisticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profit, parent, false);
        return new ProfitAdapter.StatisticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfitAdapter.StatisticalViewHolder holder, int position) {
        Profit profit = mListProfit.get(position);
        if (profit == null) {
            return;
        }
        holder.tvStt.setText(String.valueOf(position + 1));
        holder.tvDrinkName.setText(profit.getDrinkName());
        String strCurrentQuantity = profit.getCurrentQuantity() + " " + profit.getDrinkUnitName();
        holder.tvCurrentQuantity.setText(strCurrentQuantity);

        int profitValue = profit.getProfit();
        String strProfit;
        if (profitValue > 0) {
            holder.tvProfit.setTextColor(mContext.getResources().getColor(R.color.green));
            strProfit = "+" + profitValue + " 000 VNĐ";
        } else if (profitValue == 0) {
            holder.tvProfit.setTextColor(mContext.getResources().getColor(R.color.yellow));
            strProfit = profitValue +  " 000 VNĐ";
        } else {
            holder.tvProfit.setTextColor(mContext.getResources().getColor(R.color.red));
            strProfit = profitValue +  " 000 VNĐ";
        }
        holder.tvProfit.setText(strProfit);

        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                iManagerProfitListener.onClickItem(profit);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListProfit != null) {
            return mListProfit.size();
        }
        return 0;
    }

    public void release() {
        mContext = null;
    }

    public static class StatisticalViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvStt;
        private final TextView tvDrinkName;
        private final TextView tvCurrentQuantity;
        private final TextView tvProfit;
        private final LinearLayout layoutItem;

        public StatisticalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tv_stt);
            tvDrinkName = itemView.findViewById(R.id.tv_drink_name);
            tvCurrentQuantity = itemView.findViewById(R.id.tv_current_quantity);
            tvProfit = itemView.findViewById(R.id.tv_profit);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
