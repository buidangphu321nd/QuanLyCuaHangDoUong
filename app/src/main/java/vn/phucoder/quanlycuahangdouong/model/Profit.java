package vn.phucoder.quanlycuahangdouong.model;

import java.util.ArrayList;
import java.util.List;

public class Profit {

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

    public int getCurrentQuantity() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (History history : histories) {
            if (history.isAdd()) {
                result += history.getQuantity();
            } else {
                result -= history.getQuantity();
            }
        }
        return result;
    }

    public int getProfit() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (History history : histories) {
            if (history.isAdd()) {
                result -= history.getTotalPrice();
            } else {
                result += history.getTotalPrice();
            }
        }
        return result;
    }
}
