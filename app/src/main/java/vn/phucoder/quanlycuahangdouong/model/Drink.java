package vn.phucoder.quanlycuahangdouong.model;

import java.io.Serializable;

public class Drink implements Serializable {

    private long id;
    private String name;
    private long unitId;
    private String unitName;
    private int quantity;

    public Drink() {
    }

    public Drink(long id, String name, long unitId, String unitName) {
        this.id = id;
        this.name = name;
        this.unitId = unitId;
        this.unitName = unitName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}