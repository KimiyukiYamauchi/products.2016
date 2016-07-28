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

        if(mode.equals("edit")){

            _id = intent.getIntExtra("_id", 0);
            String id = intent.getStringExtra("id");
            String name = intent.getStringExtra("name");
            int price = intent.getIntExtra("price", 0);
            int stock = intent.getIntExtra("stock", 0);

            EditText idEdit = (EditText)findViewById(R.id.et_id);
            idEdit.setText(id);
            EditText nameEdit = (EditText)findViewById(R.id.et_name);
            nameEdit.setText(name);
            EditText priceEdit = (EditText)findViewById(R.id.et_price);
            priceEdit.setText(String.valueOf(price));
            EditText stockEdit = (EditText)findViewById(R.id.et_stock);
            stockEdit.setText(String.valueOf(stock));
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

    }
    private void updateProduct(int _id){

        // 1. SQLiteDatabase取得
        SQLiteDatabase db = myHelper.getWritableDatabase();

        // 2. 更新する値をセット
        ContentValues values = new ContentValues();

        EditText et_id = (EditText)findViewById(R.id.et_id);
        String id_str = et_id.getText().toString();

        EditText et_name = (EditText)findViewById(R.id.et_name);
        String name_str = et_name.getText().toString();

        EditText et_price = (EditText)findViewById(R.id.et_price);
        String price_str = et_price.getText().toString();

        EditText et_stock = (EditText)findViewById(R.id.et_stock);
        String stock_str = et_stock.getText().toString();

        values.put(MyHelper.Columns.ID, id_str);
        values.put(MyHelper.Columns.NAME, name_str);
        values.put(MyHelper.Columns.PRICE, price_str);
        values.put(MyHelper.Columns.STOCK, stock_str);

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
