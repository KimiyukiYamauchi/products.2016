package com.example.application.products;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class EditProduct extends AppCompatActivity {

    private String mode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Intent intent = getIntent();

        mode = intent.getStringExtra("mode");

        if(mode.equals("edit")){

            String id = intent.getStringExtra("id");
            String name = intent.getStringExtra("name");
            String price = intent.getStringExtra("price");
            String stock = intent.getStringExtra("stock");

            EditText idEdit = (EditText)findViewById(R.id.et_id);
            idEdit.setText(id);
            EditText nameEdit = (EditText)findViewById(R.id.et_name);
            nameEdit.setText(name);
            EditText priceEdit = (EditText)findViewById(R.id.et_price);
            priceEdit.setText(price);
            EditText stockEdit = (EditText)findViewById(R.id.et_stock);
            stockEdit.setText(stock);
        }
    }
}
