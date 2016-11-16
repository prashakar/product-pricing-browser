package prashakar.pricingbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BrowseProductsActivity extends AppCompatActivity implements AsyncResponse{

    private ProductDBHelper productDBHelper = new ProductDBHelper(this);
    private ArrayList<Product> productList;
    private int pointer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_products);
        //addNewProduct(0, "Cheese", "Block of cheese", 12.47f);
//        addNewProduct(1, "PlayStation", "Game console 120GB", 399.99f);
//        addNewProduct(2, "Coke", "Pop can drink 355mL", 1.34f);
//        addNewProduct(3, "Desktop Monitor", "4K 144Hz Monitor w/ Speaker", 640f);
    }

    @Override
    protected void onResume(){
        super.onResume();
        pointer  = 0;
        productList = productDBHelper.getAllProducts();
        if (productList.size() == 0){
            noProducts();
            updateButtons();
        } else {
            showProduct(productList.get(pointer));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browse_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNewProduct:
                Intent intent = new Intent(this, AddProductActivity.class);
                if (productList.isEmpty()){
                    intent.putExtra("addToId", 1);
                } else {
                    //used to get a new Id
                    intent.putExtra("addToId", productList.get(productList.size() - 1).getProductId() + 1);
                }
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showProduct(Product product){
        Log.v("pointerValue", String.valueOf(pointer));

        String name = product.getName();
        String description = product.getDescription();
        Float priceDollar = product.getPrice();
        //call method to convert CAD dollar into Bitcoin
        convertToBitcoin(priceDollar);

        EditText nameEdit = (EditText)findViewById(R.id.name);
        EditText descriptionEdit = (EditText)findViewById(R.id.description);
        EditText priceDollarEdit = (EditText)findViewById(R.id.priceDollar);

        TextView noProductText = (TextView)findViewById(R.id.noProductText);
        noProductText.setVisibility(View.VISIBLE);

        nameEdit.setText(name);
        descriptionEdit.setText(description);
        priceDollarEdit.setText(String.valueOf(priceDollar));
        restoreLayout();
        updateButtons();
    }

    //update buttons based on status of where pointer is located
    public void updateButtons(){
        Button previousButton = (Button)findViewById(R.id.previous);
        Button nextButton = (Button)findViewById(R.id.next);

        Log.v("arraySize", String.valueOf(productList.size()));

        Button deleteButton = (Button)findViewById(R.id.delete);

        if(pointer == 0){
            Log.v("listLocation", "START OF LIST");
            previousButton.setEnabled(false);
            nextButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }

        if(0 < pointer && pointer < productList.size() -1 ){
            Log.v("listLocation", "MIDDLE OF LIST");
            nextButton.setEnabled(true);
            deleteButton.setEnabled(true);
            previousButton.setEnabled(true);
        }

        if(pointer == productList.size() - 1) {
            Log.v("listLocation", "END OF LIST");
            previousButton.setEnabled(true);
            nextButton.setEnabled(false);
            deleteButton.setEnabled(true);
        }

        if((pointer == 0) && (pointer == productList.size() -1)){
            Log.v("listLocation", "LAST ELEMENT IN LIST");
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
            deleteButton.setEnabled(true);

        }

        if(productList.size() == 0){
            System.out.println("LIST EMPTY");
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    //when array list is empty, show no products message
    public void noProducts(){
        LinearLayout nameLayout = (LinearLayout)findViewById(R.id.name_layout);
        LinearLayout descriptionLayout = (LinearLayout)findViewById(R.id.description_layout);
        LinearLayout priceDollarLayout = (LinearLayout)findViewById(R.id.priceDollar_layout);
        LinearLayout priceBitcoinLayout = (LinearLayout)findViewById(R.id.priceBitcoin_layout);
        nameLayout.setVisibility(LinearLayout.GONE);
        descriptionLayout.setVisibility(LinearLayout.GONE);
        priceDollarLayout.setVisibility(LinearLayout.GONE);
        priceBitcoinLayout.setVisibility(LinearLayout.GONE);

        TextView noProductText = (TextView)findViewById(R.id.noProductText);
        noProductText.setVisibility(View.VISIBLE);
    }

    //used to restore layout back to showing all text views and edit texts
    public void restoreLayout(){
        LinearLayout nameLayout = (LinearLayout)findViewById(R.id.name_layout);
        LinearLayout descriptionLayout = (LinearLayout)findViewById(R.id.description_layout);
        LinearLayout priceDollarLayout = (LinearLayout)findViewById(R.id.priceDollar_layout);
        LinearLayout priceBitcoinLayout = (LinearLayout)findViewById(R.id.priceBitcoin_layout);
        nameLayout.setVisibility(LinearLayout.VISIBLE);
        descriptionLayout.setVisibility(LinearLayout.VISIBLE);
        priceDollarLayout.setVisibility(LinearLayout.VISIBLE);
        priceBitcoinLayout.setVisibility(LinearLayout.VISIBLE);

        TextView noProductText = (TextView)findViewById(R.id.noProductText);
        noProductText.setVisibility(View.GONE);
    }

    private void convertToBitcoin(Float priceDollar){
        URL url = null;
        try {
            url = new URL("https://blockchain.info/tobtc?currency=CAD&value=" + priceDollar);
            GetBitcoinData getBitcoinData = new GetBitcoinData(this);
            getBitcoinData.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void onNext(View v){
        pointer += 1;
        showProduct(productList.get(pointer));
    }

    public void onPrevious(View v){
        pointer -= 1;
        showProduct(productList.get(pointer));
    }

    public void onDelete(View v){
        //delete current index based on pointer value
        productDBHelper.deleteProduct(productList.get(pointer).getProductId());
        //update the array list with new data
        productList = productDBHelper.getAllProducts();

        //if no elements exist in array list, then show no products message and update buttons to disable them
        if(productList.size() == 0) {
            noProducts();
            updateButtons();
        } else {
            if(pointer == 0){
                showProduct(productList.get(pointer));
            } else {
                pointer -= 1;
                showProduct(productList.get(pointer));
            }
        }
    }

    @Override
    public void onProcessFinish(Float bitcoinValue) {
        EditText priceBitcoinEdit = (EditText)findViewById(R.id.priceBitcoin);
        priceBitcoinEdit.setText(String.valueOf(bitcoinValue));
    }
}
