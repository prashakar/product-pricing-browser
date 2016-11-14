package prashakar.pricingbrowser;

/**
 * Created by prash on 14/11/16.
 */

public class Product {

    private int productId;
    private String name;
    private String description;
    private float price;

    public Product(int productId, String name, String description, float price){
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public int getProductId(){
        return productId;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public float getPrice(){
        return price;
    }
}
