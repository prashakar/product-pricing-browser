package prashakar.pricingbrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by prash on 13/11/16.
 */

public class ProductDBHelper extends SQLiteOpenHelper {

    private Context context;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_FILENAME = "products.db";

    private static String CREATE_STATEMENT = "" +
            "CREATE TABLE products(" +
            "productId int primary key," +
            "name varchar(100) not null," +
            "description varchar(100) not null," +
            "price decimal not null)";

    private static String DROP_STATEMENT = "" +
            "DROP TABLE products";

    public ProductDBHelper(Context context) {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATEMENT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_STATEMENT);
        db.execSQL(CREATE_STATEMENT);
    }

    public ArrayList<Product> getAllProducts(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Product> results = new ArrayList<>();

        String[] columns = new String[] {"productId", "name", "description", "price"};
        String where = "";
        String[] whereArgs = new String[]{};
        String groupBy = "";
        String groupArgs = "";
        String orderBy = "productId";

        Cursor cursor = db.query("products", columns, where, whereArgs, groupBy, groupArgs, orderBy);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            int productId = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            float price = cursor.getFloat(3);

            Product product = new Product(productId, name, description, price);
            results.add(product);

            cursor.moveToNext();
        }

        return results;
    }

    public Product addNewProduct(int productId, String name, String description, float price){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("productId", productId);
        values.put("name", name);
        values.put("description", description);
        values.put("price", price);
        db.insertOrThrow("products", null, values);

        Product product = new Product(productId, name, description, price);

        return product;
    }

    public void deleteProduct(int productId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("products", "productId = ?", new String[]{""+productId});
    }
}
