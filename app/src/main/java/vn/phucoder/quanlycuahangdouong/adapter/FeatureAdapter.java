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
import vn.phucoder.quanlycuahangdouong.model.Feature;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder> {
    private final List<Feature> mListFeature;
    private final ImanagerFeatureListener imanagerFeatureListener;

    public interface ImanagerFeatureListener{
        void clickFeatureItem(Feature feature);
    }
    public FeatureAdapter(List<Feature> mListFeature,ImanagerFeatureListener imanagerFeatureListener) {
        this.mListFeature = mListFeature;
        this.imanagerFeatureListener = imanagerFeatureListener;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature,parent,false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        Feature feature = mListFeature.get(position);
        if (feature ==null){
            return;
        }
        holder.imgFeature.setImageResource(feature.getResource());
        holder.tvFeature.setText(feature.getTitle());
        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                imanagerFeatureListener.clickFeatureItem(feature);
            }
        });

    }

    @Override
    public int getItemCount() {
       if (mListFeature != null){
            return mListFeature.size();
       }
       return 0;
    }

    public class FeatureViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutItem;
        private final ImageView imgFeature;
        private final TextView tvFeature;
        public FeatureViewHolder(@NonNull  View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            imgFeature = itemView.findViewById(R.id.img_feature);
            tvFeature = itemView.findViewById(R.id.tv_feature);
        }
    }
}
