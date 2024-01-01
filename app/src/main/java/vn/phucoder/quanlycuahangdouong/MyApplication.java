package vn.phucoder.quanlycuahangdouong;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }

    public DatabaseReference getUnitDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("/my_unit");
    }

    public DatabaseReference getDrinkDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("/drink");
    }

    public DatabaseReference getHistoryDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("/history");
    }

    public DatabaseReference getQuantityDatabaseReference(long drinkId) {
        return FirebaseDatabase.getInstance().getReference("/drink/" + drinkId + "/quantity");
    }
}
