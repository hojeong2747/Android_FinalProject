package work2.mobile_finalproject.finalproject;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class BookmarkAdapter extends CursorAdapter {

    LayoutInflater inflater;
    int layout;

    public BookmarkAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = inflater.inflate(layout, viewGroup,false);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();


        holder.tvBMName = view.findViewById(R.id.tvBMName);
        holder.tvBMPhone = view.findViewById(R.id.tvBMAdress);
        holder.tvBMAddress = view.findViewById(R.id.tvBMPhone);

//        holder.tvBMName.setText(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_NAME)));
//
//        boolean isPhone = true;
//        try{
//            cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_PHONE));
//        }catch (NullPointerException e){
//            holder.tvBMPhone.setText("전화번호 정보가 없습니다.");
//            isPhone = false;
//        }
//        if(isPhone)
//            holder.tvBMPhone.setText(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_PHONE)));
//
//        holder.tvBMAddress.setText(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_ADDRESS)));
    }

    static class ViewHolder{
        TextView tvBMName;
        TextView tvBMAddress;
        TextView tvBMPhone;

        public ViewHolder(){
            tvBMName = null;
            tvBMAddress = null;
            tvBMPhone = null;
        }
    }
}
