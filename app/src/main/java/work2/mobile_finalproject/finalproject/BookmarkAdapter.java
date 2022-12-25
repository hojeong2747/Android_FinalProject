package work2.mobile_finalproject.finalproject;

import android.annotation.SuppressLint;
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
        ViewHolder viewHolder = new ViewHolder();
        view.setTag(viewHolder);
        return view;
    }

    @SuppressLint("Range")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // viewHolder 에 view 들을 담아놓는다. findViewById 호출 횟수 줄이기 위함
        viewHolder.tvBMName = view.findViewById(R.id.tvBMName);
        viewHolder.tvBMPhone = view.findViewById(R.id.tvBMAdress);
        viewHolder.tvBMAddress = view.findViewById(R.id.tvBMPhone);

        // viewHolder 에 담은 view 값 설정
        viewHolder.tvBMName.setText(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_NAME)));
        boolean isPhone = true;
        try{
            cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_PHONE));
        }catch (NullPointerException e){
            viewHolder.tvBMPhone.setText("전화번호 정보가 없습니다.");
            isPhone = false;
        }
        if(isPhone)
            viewHolder.tvBMPhone.setText(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_PHONE)));
        viewHolder.tvBMAddress.setText(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_ADDRESS)));
    }

    // static 클래스 선언언
    static class ViewHolder{
        // 보관할 화면 요소들
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
