package com.beesham.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Beesham on 9/20/2016.
 */
public class BookListAdapter extends ArrayAdapter {

    public BookListAdapter(Context context, List<Book> objects) {
        super(context, 0, objects);
    }

    static class ViewHolder{
        TextView title;
        TextView authors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) listItemView.findViewById(R.id.title_textview);
            viewHolder.authors = (TextView) listItemView.findViewById(R.id.authors_textview);

            listItemView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) listItemView.getTag();
        }

        Book book = (Book) getItem(position);

        if(book != null){
            viewHolder.title.setText(book.getmTitle());

            StringBuilder builder = new StringBuilder();
            String[] authors = book.getmAuthors();
            for(int i = 0;i < authors.length; i++){
                if(i == (authors.length -1)){
                    builder.append(authors[i]);
                }else{
                    builder.append(authors[i] + ", ");
                }
            }

            viewHolder.authors.setText(builder.toString());
        }

        return listItemView;
    }
}
