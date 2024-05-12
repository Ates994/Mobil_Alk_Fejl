package com.example.webshopgyakorlas;

public class ShoppingItem {
   private String id;
    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private int imageResource;
    private int cartedCount;




    public ShoppingItem(String name, String info, String price, float ratedInfo, int imageResource, int cartedCount){
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
        this.cartedCount = cartedCount;
    }

    public ShoppingItem(){

    }
    public String getName() {return name;}
    public String getInfo() {return info;}
    public String getPrice() {return price;}
    public float getRatedInfo() {return ratedInfo;}
    public String getImageResource() {return String.valueOf(imageResource);}
    public int getCartedCount() {return cartedCount; }
    public String _getID() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}
