package com.example.application.products;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.List;

/**
 * Created by application on 16/07/26.
 */
public class MyHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "products.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "products";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    Columns._ID + " INTEGER primary key autoincrement," +
                    Columns.ID + " TEXT," +
                    Columns.NAME + " TEXT," +
                    Columns.PRICE + " INTEGER," +
                    Columns.STOCK + " INTEGER)";

    public interface Columns extends BaseColumns {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String STOCK = "stock";
    }

    /**
     * コンストラクタ
     */
    public MyHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

        Log.d("MyHelper", "MyHelper");

    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("MyHelper", "onCreate");

        // CREATE文を実行する
        db.execSQL(SQL_CREATE_TABLE);

        //initTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
