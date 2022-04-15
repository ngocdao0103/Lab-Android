package com.example.admin.augscan;


public class Items {

    private String itemName;
    private String itemCategory;
    private String itemPrice;
    private String itemBarcode;
    private String itemImg;
    private String itemYear;
    private String itemOrigin;
    private String itemStatus;

    public Items() {

    }

    public void setItem(
            String itemName,
            String itemCategory,
            String itemPrice,
            String itemBarcode,
            String itemImg,
            String itemYear,
            String itemOrigin,
            String itemStatus
    ) {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemPrice = itemPrice;
        this.itemBarcode = itemBarcode;
        this.itemImg = itemImg;
        this.itemYear = itemYear;
        this.itemOrigin = itemOrigin;
        this.itemStatus = itemStatus;
    }

    public Items(
            String itemName,
            String itemCategory,
            String itemPrice,
            String itemBarcode,
            String itemImg,
            String itemYear,
            String itemOrigin,
            String itemStatus
    ) {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemPrice = itemPrice;
        this.itemBarcode = itemBarcode;
        this.itemImg = itemImg;
        this.itemYear = itemYear;
        this.itemOrigin = itemOrigin;
        this.itemStatus = itemStatus;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getItemBarcode() {
        return itemBarcode;
    }

    public String getItemImg() { return itemImg; }

    public String getItemYear() {
        return itemYear;
    }

    public String getItemOrigin() {
        return itemOrigin;
    }

    public String getItemStatus() {
        return itemStatus;
    }
}