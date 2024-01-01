package vn.phucoder.quanlycuahangdouong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Drink;
import vn.phucoder.quanlycuahangdouong.model.Unit;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {
    private final List<Drink> mListDrink;
    private final IManagerDrinkListener iManagerDrinkListener;

    public interface IManagerDrinkListener {
        void editDrink (Drink drink);
        void deleteDrink (Drink drink);
        void onClickItemDrink (Drink drink);
    }
    public DrinkAdapter(List<Drink> list, IManagerDrinkListener iManagerDrinkListener) {
        this.mListDrink = list;
        this.iManagerDrinkListener = iManagerDrinkListener;
    }

    @NonNull
    @Override
    public DrinkAdapter.DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drink,parent,false);
        return new DrinkAdapter.DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkAdapter.DrinkViewHolder holder, int position) {
        Drink drink = mListDrink.get(position);
        if (drink == null){
            return;
        }
        holder.tvName.setText(drink.getName());
        holder.tvUnitName.setText(drink.getUnitName());

        holder.imgEdit.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                iManagerDrinkListener.editDrink(drink);
            }
        });
        holder.imgDelete.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                iManagerDrinkListener.deleteDrink(drink);
            }
        });
        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                iManagerDrinkListener.onClickItemDrink(drink);
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

    public class DrinkViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvName;
        private final TextView tvUnitName;
        private final ImageView imgEdit;
        private final ImageView imgDelete;
        private final RelativeLayout layoutItem;
        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvUnitName = itemView.findViewById(R.id.tv_unit_name);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
