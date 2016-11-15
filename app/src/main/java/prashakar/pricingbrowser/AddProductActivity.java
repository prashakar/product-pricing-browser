package prashakar.pricingbrowser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddProductActivity extends AppCompatActivity {

    private ProductDBHelper productDBHelper = new ProductDBHelper(this);
    private int addToId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Intent intent = getIntent();
        addToId = intent.getIntExtra("addToId", -1);
    }

    public void onSave(View v){
        EditText nameEdit = (EditText)findViewById(R.id.name);
        EditText descriptionEdit = (EditText)findViewById(R.id.description);
        EditText priceDollarEdit = (EditText)findViewById(R.id.priceDollar);

        String name = nameEdit.getText().toString();
        String description = descriptionEdit.getText().toString();
        Float priceDollar = Float.valueOf(priceDollarEdit.getText().toString());

        productDBHelper.addNewProduct(addToId, name, description, priceDollar);
        finish();
    }

    public void onCancel(View v){

    }
}
