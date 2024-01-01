package vn.phucoder.quanlycuahangdouong.model;

import java.util.ArrayList;
import java.util.List;

public class Statistical {

    private long drinkId;
    private String drinkName;
    private long drinkUnitId;
    private String drinkUnitName;
    private List<History> histories;

    public long getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(long drinkId) {
        this.drinkId = drinkId;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public long getDrinkUnitId() {
        return drinkUnitId;
    }

    public void setDrinkUnitId(long drinkUnitId) {
        this.drinkUnitId = drinkUnitId;
    }

    public String getDrinkUnitName() {
        return drinkUnitName;
    }

    public void setDrinkUnitName(String drinkUnitName) {
        this.drinkUnitName = drinkUnitName;
    }

    public List<History> getHistories() {
        if (histories == null) {
            histories = new ArrayList<>();
        }
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public int getQuantity() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (History history : histories) {
            result += history.getQuantity();
        }
        return result;
    }

    public int getTotalPrice() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (History history : histories) {
            result += history.getTotalPrice();
        }
        return result;
    }
}
