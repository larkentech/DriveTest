package com.theoakway.drivetest.ModalClasses;

public class ItemListModal {

    private String barcode;
    private String name;
    private String uom;
    private String costPrice;
    private String retailPrice;

    public ItemListModal(String barcode, String name, String uom, String costPrice, String retailPrice) {
        this.barcode = barcode;
        this.name = name;
        this.uom = uom;
        this.costPrice = costPrice;
        this.retailPrice = retailPrice;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }

    public String getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
    }
}
