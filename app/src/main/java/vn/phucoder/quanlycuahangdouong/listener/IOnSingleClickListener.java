package vn.phucoder.quanlycuahangdouong.listener;

import android.os.SystemClock;
import android.view.View;

public abstract class IOnSingleClickListener implements View.OnClickListener{
    private static final long MIN_CLICK_INTERVAL = 600;
    private long mLastClickTime;
    public abstract void onSingleClick(View view);
    @Override
    public void onClick(View view) {
        long currenClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currenClickTime- mLastClickTime;
        mLastClickTime = currenClickTime;
        if (elapsedTime <= MIN_CLICK_INTERVAL){
            return;
        }
        onSingleClick(view);

    }
}
