package com.example.dellxps15.roomwordsample2;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.dellxps15.roomwordsample2.MainActivity.SHARED_PREFS_FILE_NAME;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;
        private final ImageView imageItemView;
        private final TextView descItemView;
        private final TextView priceItemView;
        private final TextView buttonItemView;

        private ProductViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
            imageItemView = itemView.findViewById(R.id.imageView);
            descItemView = itemView.findViewById(R.id.textViewDesc);
            priceItemView = itemView.findViewById(R.id.textViewPrice);
            buttonItemView = itemView.findViewById(R.id.textViewCart);
        }
    }

    private final LayoutInflater mInflater;
    private List<Products> mProducts; // Cached copy of products
    private Context context;

    public MainActivity ma = new MainActivity();

    ProductListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        itemView.setClickable(false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        if (mProducts != null) {
            Products current = mProducts.get(position);
            holder.wordItemView.setText(current.getProduct());

            // IF THERE IS INTERNET
            if(true){
                String imgName = current.getImage();
                Picasso.with(context).load(imgName).into(holder.imageItemView);

                int lin = imgName.lastIndexOf("/");
                String justImgName = imgName.substring(lin+1);

                try {

                    URL myFileUrl = new URL (imgName);
                    HttpURLConnection conn =
                            (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    Bitmap x = BitmapFactory.decodeStream(is);

                    ContextWrapper wrapper = new ContextWrapper(context);
                    File file = wrapper.getDir("Images",MODE_PRIVATE);
                    file = new File(file, justImgName);


                    try (FileOutputStream out = new FileOutputStream(file)) {
                        x.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    Uri savedImageURI = Uri.parse(file.getAbsolutePath());
//                    ma.showToast(context, "LINK: "+savedImageURI);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            // IF NO INTERNET
            if(false) {


                String imgName = current.getImage();
                int lin = imgName.lastIndexOf("/");
                String justImgName = imgName.substring(lin+1);

                ContextWrapper wrapper = new ContextWrapper(context);
                File file = wrapper.getDir("Images",MODE_PRIVATE);
                file = new File(file, justImgName);

                try {

                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
                    ma.showToast(context, justImgName);
                    holder.imageItemView.setImageBitmap(b);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }





            holder.descItemView.setText(current.getDescription());
            holder.priceItemView.setText("Price: EUR " + String.valueOf(current.getPrice()));

            Context cx = holder.priceItemView.getContext();

            SharedPreferences prefs = cx.getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);
            int count = prefs.getInt("count", 0); //0 is the default value.


            for(int i =0; i< count; i++){
                int idName = prefs.getInt("idName"+(i+1), -1);

                if(idName == position){
                    holder.buttonItemView.setText("ITEM ADDED");
                    holder.buttonItemView.setBackgroundResource(R.drawable.removebutton);
                }
            }


        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No Word");
        }
    }

    void setProducts(List<Products> products){
        mProducts = products;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mProducts != null)
            return mProducts.size();
        else return 0;
    }



    public boolean loadImageFromURL(String fileUrl,
                                    ImageView iv){
        try {

            URL myFileUrl = new URL (fileUrl);
            HttpURLConnection conn =
                    (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            iv.setImageBitmap(BitmapFactory.decodeStream(is));

            Bitmap x = BitmapFactory.decodeStream(is);
            int lin = fileUrl.lastIndexOf("/");
            String justImgName = fileUrl.substring(lin);


            ma.showToast(context, justImgName);


            String path = Environment.getDataDirectory().toString();
            File file = new File(path, "FitnessGirl.jpg");

            try (FileOutputStream out = new FileOutputStream(file)) {
                x.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}