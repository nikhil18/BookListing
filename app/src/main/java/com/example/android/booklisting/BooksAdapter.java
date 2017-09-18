package com.example.android.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BooksAdapter extends ArrayAdapter<Books> {

    private Context context;

    public BooksAdapter(Context context, ArrayList<Books> booksArrayList) {
        super(context, 0, booksArrayList);
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Books Books = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title_textview_lv.setText(Books.getBooksName());
        viewHolder.author_textview_lv.setText(Books.getAuthorName());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.book_title)
        TextView title_textview_lv;
        @BindView(R.id.book_author)
        TextView author_textview_lv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}
