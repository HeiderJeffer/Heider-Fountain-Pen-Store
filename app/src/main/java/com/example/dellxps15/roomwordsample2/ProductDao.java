package com.example.dellxps15.roomwordsample2;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    void insert(Products product);

    @Query("DELETE FROM products_table")
    void deleteAll();

    @Query("DELETE FROM products_table WHERE id = :id")
    void deleteOne(int id);

    @Query("SELECT * from products_table ORDER BY id ASC")
    LiveData<List<Products>> getAllProducts();

    @Query("SELECT * FROM products_table WHERE id = :id ")
    Products getProductById(int id);
}
