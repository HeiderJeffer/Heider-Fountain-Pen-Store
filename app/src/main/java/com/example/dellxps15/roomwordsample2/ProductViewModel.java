package com.example.dellxps15.roomwordsample2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    private ProductRepository mRepository;
    private LiveData<List<Products>> mAllProducts, iAllProducts;

    public ProductViewModel (Application application) {
        super(application);
        mRepository = new ProductRepository(application);
        mAllProducts = mRepository.getAllProducts();
    }

    LiveData<List<Products>> getAllProducts() { return mAllProducts; }

    public void insert(Products product) { mRepository.insert(product); }
    public void delAll(Products product) { mRepository.delAll(product); }
    public void deleteOne(int id) { mRepository.deleteOne(id); }
    public Products findItem(int id) { return mRepository.getProductById(id);}
}
