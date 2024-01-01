package vn.phucoder.quanlycuahangdouong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Drink;

public class ManageDrinkAdapter extends RecyclerView.Adapter<ManageDrinkAdapter.ManageDrinkViewHolder> {
    private final List<Drink> mListDrink;
    private final IManagerDrinkListener iManagerDrinkListener;

    public interface IManagerDrinkListener {
        void clickItem(Drink drink);
    }

    public ManageDrinkAdapter(List<Drink> mListDrink, IManagerDrinkListener iManagerDrinkListener) {
        this.mListDrink = mListDrink;
        this.iManagerDrinkListener = iManagerDrinkListener;
    }
    @NonNull
    @Override
    public ManageDrinkAdapter.ManageDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_drink, parent, false);
        return new ManageDrinkAdapter.ManageDrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageDrinkAdapter.ManageDrinkViewHolder holder, int position) {
        Drink drink = mListDrink.get(position);
        if (drink == null){
            return;
        }
        holder.tvName.setText(drink.getName());
        String strCurrentQuantity = drink.getQuantity()+" "+ drink.getUnitName();
        holder.tvCurrentQuantity.setText(strCurrentQuantity);

        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                iManagerDrinkListener.clickItem(drink);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mListDrink != null) {
            return mListDrink.size();
        }
        return 0;
    }

    public static class ManageDrinkViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvCurrentQuantity;
        private final RelativeLayout layoutItem;

        public ManageDrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCurrentQuantity = itemView.findViewById(R.id.tv_current_quantity);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
