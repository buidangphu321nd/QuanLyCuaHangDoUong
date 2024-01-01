package vn.phucoder.quanlycuahangdouong.model;

public class Feature {
    public static final int FEATURE_LIST_MENU = 0;
    public static final int FEATURE_MANAGE_UNIT = 1;
    public static final int FEATURE_ADD_DRINK = 2;
    public static final int FEATURE_MANAGE_DRINK = 3;
    public static final int FEATURE_DRINK_USED = 4;
    public static final int FEATURE_DRINK_OUT_OF_STOCK = 5;
    public static final int FEATURE_REVELUE = 6;
    public static final int FEATURE_COST = 7;
    public static final int FEATURE_PROFIT = 8;
    public static final int FEATURE_DRINK_POPULAR = 9;

    public int id;
    private int resource;
    public String title;

    public Feature(int id, int resource, String title) {
        this.id = id;
        this.resource = resource;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
