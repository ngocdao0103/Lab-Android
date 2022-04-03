package com.example.admin.augscan;


public class Items {

    private String itemname;
    private String itemcategory;
    private String itemprice;
    private String itembarcode;
    private String itemimg;

    public Items() {

    }
    public void setItem (String itemname, String itemcategory, String itemprice, String itembarcode, String itemimg) {
        this.itemname=itemname;
        this.itemcategory=itemcategory;
        this.itemprice=itemprice;
        this.itembarcode= itembarcode;
        this.itemimg = itemimg;
    }

    public Items(String itemname,String itemcategory,String itemprice,String itembarcode, String itemimg){

        this.itemname=itemname;
        this.itemcategory=itemcategory;
        this.itemprice=itemprice;
        this.itembarcode= itembarcode;
        this.itemimg = itemimg;
    }

    public String getItemname() {
        return itemname;
    }

    public String getItemcategory() {
        return itemcategory;
    }

    public String getItemprice() {
        return itemprice;
    }

    public String getItembarcode() {
        return itembarcode;
    }

    public String getItemimg() {
        return itemimg;
    }
}