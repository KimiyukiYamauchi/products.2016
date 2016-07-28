package com.example.application.products;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditProduct extends Activity implements View.OnClickListener{

    private String mode = "";
    private int _id;
    private MyHelper myHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Intent intent = getIntent();

        mode = intent.getStringExtra("mode");

        EditText idEdit = (EditText)findViewById(R.id.et_id);
        EditText nameEdit = (EditText)findViewById(R.id.et_name);
        EditText priceEdit = (EditText)findViewById(R.id.et_price);
        EditText stockEdit = (EditText)findViewById(R.id.et_stock);

        if(mode.equals("edit")){

            _id = intent.getIntExtra("_id", 0);
            String id = intent.getStringExtra("id");
            String name = intent.getStringExtra("name");
            int price = intent.getIntExtra("price", 0);
            int stock = intent.getIntExtra("stock", 0);

            idEdit.setText(id);
            nameEdit.setText(name);
            priceEdit.setText(String.valueOf(price));
            stockEdit.setText(String.valueOf(stock));

        }else{

            idEdit.setText("");
            nameEdit.setText("");
            priceEdit.setText("");
            stockEdit.setText("");

        }

        // MyHelperオブジェクトを作り、フィールドにセット
        myHelper = new MyHelper(this);

        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        updateProduct(_id);

        Intent intent = new Intent(this, ProductList.class);
        startActivity(intent);
        ProductItemStr item = new ProductItemStr();


    }

    private class ProductItemStr {
        String id;
        String name;
        String price;
        String stock;
    }

    private String insertProduct(ProductItemStr item){


        SQLiteDatabase db = myHelper.getWritableDatabase();

        // 列に対応する値をセット
        ContentValues values = new ContentValues();

        values.put(MyHelper.Columns.ID, item.id);
        values.put(MyHelper.Columns.NAME, item.name);
        values.put(MyHelper.Columns.PRICE, item.price);
        values.put(MyHelper.Columns.STOCK, item.stock);



    private String updateProduct(int _id, ProductItemStr item){



        // 2. 更新する値をセット
        ContentValues values = new ContentValues();

        values.put(MyHelper.Columns.ID, item.id);
        values.put(MyHelper.Columns.NAME, item.name);
        values.put(MyHelper.Columns.PRICE, item.price);
        values.put(MyHelper.Columns.STOCK, item.stock);

        /*Log.d("updateProduct",
                "ID = " + id_str + "\n" +
                "NAME = " + name_str + "\n" +
                "PRICE = " + price_str + "\n" +
                "STOCK = " + stock_str);*/

        // 3. 更新する行をWHEREで指定
        String where = MyHelper.Columns._ID + "=?";
        String [] args = { String.valueOf(_id)};

        int count = db.update(MyHelper.TABLE_NAME, values, where, args);
        if(count == 0){
            Log.d("Edit", "Failed to update");
        }

        // 4. データベースを閉じる
        db.close();

    }


}
