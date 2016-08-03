package com.example.application.products;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProductList extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private MyHelper myHelper;
    private Handler mHandler;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

        ProductItem item = (ProductItem)parent.getItemAtPosition(position);

        Intent intent = new Intent(this, EditProduct.class);

        intent.putExtra("mode", "edit");

        intent.putExtra("_id", item._id);
        intent.putExtra("id", item.id);
        intent.putExtra("name", item.name);
        intent.putExtra("price", item.price);
        intent.putExtra("stock", item.stock);

        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, EditProduct.class);

        intent.putExtra("mode", "add");

        startActivity(intent);

    }

    private class ProductItem {
        int _id;
        String id;
        String name;
        int price;
        int stock;
        boolean del_flg;
    }

    private List<ProductItem> itemList;
    private ItemAdapter adapter;

    private void setProductData(){

/*
        ProductItem item = new ProductItem();
        item._id = 1;
        item.id = "A01";
        item.name = "赤鉛筆";
        item.price = 50;
        item.stock = 100;
        itemList.add(item);

        item = new ProductItem();
        item._id = 2;
        item.id = "A02";
        item.name = "青鉛筆";
        item.price = 50;
        item.stock = 50;
        itemList.add(item);
*/

        selectProductList();

        //Log.d("setProductData", "size = " + itemList.size());

        //adapter.notifyDataSetChanged();

    }

    private void selectProductList(){

        // 1. SQLiteDatabaseオブジェクトを取得
        SQLiteDatabase db = myHelper.getReadableDatabase();

        // 2. query()を呼び、検索を行う
        Cursor cursor =
                db.query(MyHelper.TABLE_NAME, null, null, null, null, null,
                        MyHelper.Columns._ID + " ASC");

        // 3. 読込位置を先頭にする。falseの場合は結果0件
        if(!cursor.moveToFirst()){
            cursor.close();
            db.close();
            return;
        }

        // 4. 列のindex(位置)を取得する
        int _idIndex = cursor.getColumnIndex(MyHelper.Columns._ID);
        int idIndex = cursor.getColumnIndex(MyHelper.Columns.ID);
        int nameIndex = cursor.getColumnIndex(MyHelper.Columns.NAME);
        int priceIndex = cursor.getColumnIndex(MyHelper.Columns.PRICE);
        int stockIndex = cursor.getColumnIndex(MyHelper.Columns.STOCK);

        // 5. 行を読み込む。
        itemList.removeAll(itemList);
        do {
            ProductItem item = new ProductItem();
            item._id = cursor.getInt(_idIndex);
            item.id = cursor.getString(idIndex);
            item.name = cursor.getString(nameIndex);
            item.price = cursor.getInt(priceIndex);
            item.stock = cursor.getInt(stockIndex);
            item.del_flg = false;

            /*Log.d("selectProductList",
                    "_id = " + item._id + "\n" +
                    "id = " + item.id + "\n" +
                    "name = " + item.name + "\n" +
                    "price = " + item.price + "\n" +
                    "stock = " + item.stock + "\n" +
                    "del_flg = " + item.del_flg);*/

            itemList.add(item);

            // 読込位置を次の行に移動させる
            // 次の行が無い時はfalseを返すのでループを抜ける
        }while (cursor.moveToNext());

        // 6. Cursorを閉じる
        cursor.close();

        // 7. データベースを閉じる
        db.close();;

        //return itemList;
    }

    private void deleteProductList(){

        // 1. SQLiteDatabaseオブジェクトを取得
        SQLiteDatabase db = myHelper.getWritableDatabase();

        // 2. 削除する行の条件を設定
        ArrayList<String> temp= new ArrayList<String>();
        Iterator iterator = itemList.iterator();
        while(iterator.hasNext()){
            ProductItem item = (ProductItem)iterator.next();
            if(item.del_flg){
                temp.add((String.valueOf(item._id)));
            }
        }
        String [] del_list = temp.toArray(new String[temp.size()]);

        String where = MyHelper.Columns._ID + "=?";
        if(del_list.length > 1){
            for(int i=0; i<del_list.length-1; i++){
                where += " or " + MyHelper.Columns._ID + "=?";
            }
        }

        Log.d("deleteProductList", "where = " + where);

        int count = db.delete(MyHelper.TABLE_NAME, where, del_list);
        if(count == 0){
            Log.d("Delete", "Faild to delete");
        }

        // 3. データベースを閉じる
        db.close();

        //return itemList;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("ProductList", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // MyHelperオブジェクトを作り、フィールドにセット
        myHelper = new MyHelper(this);

        //ハンドラを生成
        mHandler = new Handler();

        //initTable();

        itemList = new ArrayList<ProductItem>();
        adapter =
                new ItemAdapter(getApplicationContext(), 0,
                        itemList);
        adapter.setNotifyOnChange(true);
        ListView listView =
                (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // Table取得したデータをListViewにセットするためのスレッド
        (new Thread(new Runnable() {
            @Override
            public void run() {

                selectProductList();

                //メインスレッドのメッセージキューにメッセージを登録します。
                mHandler.post(new Runnable (){
                    //run()の中の処理はメインスレッドで動作されます。
                    public void run(){
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        })).start();

        listView.setOnItemClickListener(this);

        Button btn_add = (Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);

        Button btn_del = (Button)findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //スレッドを生成して起動します
                (new Thread(new Runnable() {
                    @Override
                    public void run() {

                        deleteProductList();
                        selectProductList();

                        //メインスレッドのメッセージキューにメッセージを登録します。
                        mHandler.post(new Runnable (){
                            //run()の中の処理はメインスレッドで動作されます。
                            public void run(){
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                })).start();
            }
        });

        Button btn_ini = ( Button)findViewById(R.id.btn_ini);
        btn_ini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //スレッドを生成して起動します
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initTable();
                        setProductData();

                        //メインスレッドのメッセージキューにメッセージを登録します。
                        mHandler.post(new Runnable (){
                            //run()の中の処理はメインスレッドで動作されます。
                            public void run(){
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                })).start();
            }
        });
    }

    private class ItemAdapter extends ArrayAdapter<ProductItem>{
        private LayoutInflater inflater;

        public ItemAdapter(Context context, int resouce,
                           List<ProductItem> objects){
            super(context, resouce, objects);
            inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

//            Log.d("ProductList", "getView");

            View view;


            //if(convertView == null){
                view = inflater.inflate(R.layout.product_row, null, false);
            /*}else{
                view = convertView;
            }*/

            final TextView idView = (TextView)view.findViewById(R.id.id);
            TextView nameView = (TextView)view.findViewById(R.id.name);
            TextView priceView = (TextView)view.findViewById(R.id.price);
            TextView stockView = (TextView)view.findViewById(R.id.stock);
            CheckBox cb = (CheckBox)view.findViewById(R.id.checkBox);
            final ProductItem item = getItem(position);
            idView.setText(item.id);
            nameView.setText(item.name);
            priceView.setText(String.valueOf(item.price));
            stockView.setText(String.valueOf(item.stock));
            cb.setChecked(item.del_flg);

            cb.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                            item.del_flg = !item.del_flg;

                            Log.d("getView", "OnCheckedChanged item.del_flg = " + item.del_flg);

                        }
                    }
            );


            /*Log.d("getView",
                    "_id = " + item._id + "\n" +
                    "id = " + item.id + "\n" +
                    "name = " + item.name + "\n" +
                    "price = " + item.price + "\n" +
                    "stock = " + item.stock + "\n" +
                    "del_flg = " + item.del_flg);*/


            cb.setFocusable(false);
            cb.setFocusableInTouchMode(false);
            return  view;
        }
    }

    /**
     * テーブルを初期化するための処理
     */

    private class ProductDbItem {
        String id;
        String name;
        int price;
        int stock;
    }

    private List<ProductDbItem> itemDbList;

    private void setProductDbData(){

        itemDbList = new ArrayList<ProductDbItem>();

        ProductDbItem item = new ProductDbItem();
        item.id = "A01";
        item.name = "赤鉛筆";
        item.price = 50;
        item.stock = 100;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A02";
        item.name = "青鉛筆";
        item.price = 50;
        item.stock = 50;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A03";
        item.name = "消しゴム";
        item.price = 75;
        item.stock = 1000;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A04";
        item.name = "三角定規";
        item.price = 120;
        item.stock = 10;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A05";
        item.name = "ボールペン黒";
        item.price = 80;
        item.stock = 25;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A06";
        item.name = "ボールペン赤";
        item.price = 90;
        item.stock = 24;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A07";
        item.name = "３色ボールペン";
        item.price = 120;
        item.stock = 30;
        itemDbList.add(item);

    }


    private void initTable(){

        Log.d("ProductList", "initTable");

        SQLiteDatabase db = myHelper.getWritableDatabase();

        // 一旦削除
        int count = db.delete(MyHelper.TABLE_NAME, null, null);
        Log.d("initTable", "count =" + count);

        setProductDbData();

        for(int i = 0; i< itemDbList.size(); i++){
            ProductDbItem item = itemDbList.get(i);

            // 列に対応する値をセットする
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
        }

    }


}
