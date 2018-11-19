package com.example.dellxps15.roomwordsample2;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Products.class}, version = 2)
public abstract class ProductRoomDatabase extends RoomDatabase{

    public abstract ProductDao productDao();

    private static ProductRoomDatabase INSTANCE;


    static ProductRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ProductRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ProductRoomDatabase.class, "product_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final ProductDao mDao;
        Products word;

        PopulateDbAsync(ProductRoomDatabase db) {
            mDao = db.productDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
//            mDao.deleteAll();
//            word = new Products(0,"Mont Blanc 149", "Gold-tip Mont Blanc (Black and Gold) Made in Switzerland", 678, "a1");
//            mDao.insert(word);
//            word = new Products(1,"Pelikan Premium M1000", "Gold/Silver tip Pelikan (Sea Green and Black) Made in France", 380, "a2");
//            mDao.insert(word);
//            word = new Products(2,"Pilot Custom 823", "Gold-tip Pilot (Brown and Gold) Made in Japan", 359, "a3");
//            mDao.insert(word);
//            word = new Products(3,"Platinum 3776 CENTURY", "Gold-tip Platinum (Royal Blue) Made in Belgium", 72, "a4");
//            mDao.insert(word);
            return null;
        }
    }
}
