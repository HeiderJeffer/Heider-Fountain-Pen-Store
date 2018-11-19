package com.example.dellxps15.roomwordsample2;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "products_table")
public class Products {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "product")
    private String mProduct;

    @NonNull
    @ColumnInfo(name = "description")
    private String mDescription;

    @NonNull
    @ColumnInfo(name = "price")
    private int mPrice;

    @NonNull
    @ColumnInfo(name = "image")
    private String mImage;

    public Products(int id, String product, String description, int price, String image) {
        this.id = id;
        this.mProduct = product;
        this.mDescription = description;
        this.mPrice = price;
        this.mImage = image;
    }

    public int getId() {return this.id;}
    public String getProduct(){return this.mProduct;}
    public String getDescription(){return this.mDescription;}
    public int getPrice(){return this.mPrice;}
    public String getImage(){return this.mImage;}
}