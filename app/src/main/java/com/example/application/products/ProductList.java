package com.example.application.products;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProductList extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    MyHelper myHelper;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

        ProductItem item = (ProductItem)parent.getItemAtPosition(position);

        Intent intent = new Intent(this, EditProduct.class);

        intent.putExtra("mode", "edit");

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
        String id;
        String name;
        String price;
        String stock;
    }

    private List<ProductItem> itemList;
    private ItemAdapter adapter;

    private void setProductData(){
        ProductItem item = new ProductItem();
        item.id = "A01";
        item.name = "赤鉛筆";
        item.price = "50";
        item.stock = "100";
        itemList.add(item);

        item = new ProductItem();
        item.id = "A02";
        item.name = "青鉛筆";
        item.price = "50";
        item.stock = "50";
        itemList.add(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // MyHelperオブジェクトを作り、フィールドにセット
        myHelper = new MyHelper(this);


        initTable();

        itemList = new ArrayList<ProductItem>();
        adapter =
                new ItemAdapter(getApplicationContext(), 0,
                        itemList);
        ListView listView =
                (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
        setProductData();

        listView.setOnItemClickListener(this);

        Button btn_add = (Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
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
            View view = inflater.inflate(R.layout.product_row, null, false);
            TextView idView = (TextView)view.findViewById(R.id.id);
            TextView nameView = (TextView)view.findViewById(R.id.name);
            TextView priceView = (TextView)view.findViewById(R.id.price);
            TextView stockView = (TextView)view.findViewById(R.id.stock);
            ProductItem item = getItem(position);
            idView.setText(item.id);
            nameView.setText(item.name);
            priceView.setText(item.price);
            stockView.setText(item.stock);
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

        Log.d("MyHelper", "initTable");

        SQLiteDatabase db = myHelper.getWritableDatabase();

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
