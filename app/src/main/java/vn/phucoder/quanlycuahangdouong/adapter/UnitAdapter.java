package vn.phucoder.quanlycuahangdouong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.phucoder.quanlycuahangdouong.R;
import vn.phucoder.quanlycuahangdouong.listener.IOnSingleClickListener;
import vn.phucoder.quanlycuahangdouong.model.Unit;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.UnitViewHolder> {
    private final List<Unit> mListUnit;
    private final IManagerUnitListener iManagerUnitListener;
    public interface IManagerUnitListener{
        void editUnit(Unit unit);
        void deleteUnit(Unit unit);
    }
    public UnitAdapter(List<Unit> list, IManagerUnitListener listener) {
        this.mListUnit = list;
        this.iManagerUnitListener = listener;
    }


    @NonNull
    @Override
    public UnitAdapter.UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unit, parent, false);
        return new UnitAdapter.UnitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitAdapter.UnitViewHolder holder, int position) {
        Unit unit = mListUnit.get(position);
        if (unit == null){
            return;
        }
        holder.tvName.setText(unit.getName());
        holder.imgEdit.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                iManagerUnitListener.editUnit(unit);
            }
        });
        holder.imgDelete.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                iManagerUnitListener.deleteUnit(unit);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListUnit != null) {
            return mListUnit.size();
        }
        return 0;
    }
    public static class UnitViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final ImageView imgEdit;
        private final ImageView imgDelete;

        public UnitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

}
