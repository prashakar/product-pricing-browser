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

    }

    @Override
    protected void onResume(){
        super.onResume();
        productList = productDBHelper.getAllProducts();
        if (!productList.isEmpty()) {
            for (Product product : productList) {
                System.out.println(product.getName());
            }
        }
        if (productList.size() == 0){
            noProducts();
            updateButtons();
        } else {

            showProduct(productList.get(pointer));
            pointer = productList.size();
            System.out.println(pointer);


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
        pointer += 1;
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
        System.out.println("UPDATE BUTTON" + productList.size());
        if (productList.size() == pointer){
            nextButton.setEnabled(false);
        } else {
            nextButton.setEnabled(true);
        }
        if (pointer == 1) {
            previousButton.setEnabled(false);
        } else {
            previousButton.setEnabled(true);
        }
        Button deleteButton = (Button)findViewById(R.id.delete);
        if (productList.size() == 0){
            deleteButton.setEnabled(false);
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
        } else {
            deleteButton.setEnabled(true);

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
        showProduct(productList.get(pointer -1 ));
    }

    public void onPrevious(View v){
        pointer -= 2;
        showProduct(productList.get(pointer));
    }

    public void onDelete(View v){
        productDBHelper.deleteProduct(productList.get(pointer - 1).getProductId());
        pointer += 1;
        if (productList.size() == pointer) {
            showProduct(productList.get(pointer));
        } else {
            pointer -= 2;
            noProducts();
            updateButtons();
        }
    }

    @Override
    public void onProcessFinish(Float bitcoinValue) {
        EditText priceBitcoinEdit = (EditText)findViewById(R.id.priceBitcoin);
        priceBitcoinEdit.setText(String.valueOf(bitcoinValue));
    }
}
