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

        ProductItemStr item = new ProductItemStr();

        String err_msg = "";

        EditText et_id = (EditText)findViewById(R.id.et_id);
        item.id = et_id.getText().toString();
        if(item.id.equals("")){
            err_msg += "製品IDを入力くしてください\n";
        }

        EditText et_name = (EditText)findViewById(R.id.et_name);
        item.name = et_name.getText().toString();
        if(item.name.equals("")){
            err_msg += "製品名を入力してください\n";
        }

        EditText et_price = (EditText)findViewById(R.id.et_price);
        item.price = et_price.getText().toString();
        if(item.price.equals("")){
            err_msg += "単価を入力してください\n";
        }

        EditText et_stock = (EditText)findViewById(R.id.et_stock);
        item.stock = et_stock.getText().toString();
        if(item.stock.equals("")){
            err_msg += "在庫を入力してください\n";
        }

        Log.d("EditProduct", "err_msg = " + err_msg);

        if(err_msg.equals("")){

            String ret_msg = "";

            if(mode.equals("edit")){
                ret_msg = updateProduct(_id, item);
            }else{
                ret_msg = insertProduct(item);
            }

            if(ret_msg.equals("")){
                Intent intent = new Intent(this, ProductList.class);
                startActivity(intent);
            }else{
                // 確認ダイアログの生成
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
                if(mode.equals("edit")){
                    alertDlg.setTitle("製品編集");
                }else{
                    alertDlg.setTitle("製品登録");
                }
                alertDlg.setMessage(ret_msg);
                alertDlg.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // OKボタンクリック処理
                            }
                        }
                );

                // 表示
                alertDlg.create().show();
            }



        }else{
            // 確認ダイアログの生成
            AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
            if(mode.equals("edit")){
                alertDlg.setTitle("製品編集");
            }else{
                alertDlg.setTitle("製品登録");
            }
            alertDlg.setMessage(err_msg);
            alertDlg.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // OKボタンクリック処理
                        }
                    }
            );

            // 表示
            alertDlg.create().show();

        }

    }

    private class ProductItemStr {
        String id;
        String name;
        String price;
        String stock;
    }

    private String insertProduct(ProductItemStr item){

        String err_msg = "";

        // 1. SQLiteDatabaseオブジェクト取得
        SQLiteDatabase db_q = myHelper.getReadableDatabase();

        // 2. queryを呼び、検索を行う
        String where = MyHelper.Columns.ID + "=?";
        String [] args = { item.id };
        Cursor cursor = db_q.query(
                MyHelper.TABLE_NAME, null, where, args, null, null, null);

        // 3. 読込位置を先頭にする。trueの場合は結果1件以上
        if(cursor.moveToFirst()){
            cursor.close();
            db_q.close();
            err_msg += "製品IDはすでに存在します";
            return  err_msg;
        }else{
            cursor.close();
            db_q.close();
        }

        SQLiteDatabase db = myHelper.getWritableDatabase();

        // 列に対応する値をセット
        ContentValues values = new ContentValues();

        values.put(MyHelper.Columns.ID, item.id);
        values.put(MyHelper.Columns.NAME, item.name);
        values.put(MyHelper.Columns.PRICE, item.price);
        values.put(MyHelper.Columns.STOCK, item.stock);

        // データベースに行を追加する
        long id = db.insert(MyHelper.TABLE_NAME, null, values);
        if(id == -1){
            Log.d("Database", "行の追加に失敗したよ");
        }

        // データベースを閉じる(処理の終了を伝える)
        db.close();

        return err_msg;

    }

    private String updateProduct(int _id, ProductItemStr item){

        String err_msg = "";

        // 1. SQLiteDatabase取得
        SQLiteDatabase db = myHelper.getWritableDatabase();

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


        return err_msg;

    }


}
