package prashakar.pricingbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        productDBHelper.addNewProduct(0, "test", "testdescription", 12.47f);
        productDBHelper.addNewProduct(1, "test1", "testdescription2", 2.14f);
        productDBHelper.addNewProduct(2, "test2", "testdescription3", 1.34f);
        productDBHelper.addNewProduct(3, "test3", "testdescription4", 144f);
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
                    intent.putExtra("addToId", productList.get(productList.size() - 1).getProductId() + 1);
                }
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showProduct(Product product){
        System.out.println("POINTER VALUE: " + pointer);

        String name = product.getName();
        String description = product.getDescription();
        Float priceDollar = product.getPrice();
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

    public void updateButtons(){
        Button previousButton = (Button)findViewById(R.id.previous);
        Button nextButton = (Button)findViewById(R.id.next);
        System.out.println("ARRAY SIZE: " + productList.size());

        Button deleteButton = (Button)findViewById(R.id.delete);
        if(pointer == 0){
            System.out.println("START OF LIST");
            previousButton.setEnabled(false);
            nextButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }

        if(0 < pointer && pointer < productList.size() -1 ){
            System.out.println("MIDDLE OF LIST");

            nextButton.setEnabled(true);
            deleteButton.setEnabled(true);
            previousButton.setEnabled(true);
        }

        if(pointer == productList.size() - 1) {
            System.out.println("END OF LIST");
            previousButton.setEnabled(true);
            nextButton.setEnabled(false);
            deleteButton.setEnabled(true);
        }

        if((pointer == 0) && (pointer == productList.size() -1)){
            System.out.println("LAST ELEMENT IN LIST");
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
        productDBHelper.deleteProduct(productList.get(pointer).getProductId());
        productList = productDBHelper.getAllProducts();
        System.out.println("onDelete POINTER " + pointer);
        System.out.println("ARRAY SIZE " + productList.size());
        //pointer += 1;

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
