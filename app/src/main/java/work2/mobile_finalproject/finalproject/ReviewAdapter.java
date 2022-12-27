package work2.mobile_finalproject.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ReviewAdapter extends CursorAdapter {
    LayoutInflater inflater;
    int layout;
    ViewHolder holder;

    public ReviewAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent,false);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);
        return view;
    }

    @SuppressLint("Range")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        holder = (ViewHolder) view.getTag();

        if(holder.tvName == null){
            holder.tvName = view.findViewById(R.id.tvRVNameItem);
            holder.tvDate = view.findViewById(R.id.tvRVDateItem);
            holder.Ratingbar = view.findViewById(R.id.tvRVR);
            holder.imageView = view.findViewById(R.id.ivRV);
        }

        holder.tvName.setText(cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_NAME)));
        holder.tvDate.setText(cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_DATE)));
        holder.Ratingbar.setRating(Float.parseFloat(cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_RATING))));

        boolean isPath = true;
        String photoPath = "";
        try{
            photoPath = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_PHOTOPATH));
            if(photoPath.equals(""))
                holder.imageView.setImageResource(R.drawable.ic_baseline_check_24);
        }
        catch(NullPointerException e){
            holder.imageView.setImageResource(R.drawable.ic_baseline_check_24);
            isPath = false;
        }
        if(isPath)
            setPic(photoPath);

    }

    private void setPic(String path) {
        // Get the dimensions of the View
        int targetW = 1080;
        int targetH = 720;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        holder.imageView.setImageBitmap(bitmap);
    }

    static class ViewHolder{
        public ViewHolder(){
            tvName = null;
            tvDate = null;
            Ratingbar = null;
            imageView = null;
        }

        TextView tvName;
        TextView tvDate;
        RatingBar Ratingbar;
        ImageView imageView;

    }
}
