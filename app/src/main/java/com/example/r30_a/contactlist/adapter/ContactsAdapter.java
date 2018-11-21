package com.example.r30_a.contactlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.r30_a.contactlist.R;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by Luca on 2018/11/19.
 */

public class ContactsAdapter extends BaseAdapter{

    TextView txvName, txvNum;
    ArrayList<Map<String, Object>> list;
    Context context;
    LayoutInflater inflater;

    public ContactsAdapter(Context context, ArrayList<Map<String, Object>> list) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = new ArrayList();
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.contacts_layout,null);
        txvNum = view.findViewById(R.id.txvContactsNumber);
        txvName = view.findViewById(R.id.txvContactName);

        txvName.setText(list.get(position).get(R.string.ContactName).toString());
        txvNum.setText(list.get(position).get(R.string.ContactPhone).toString());

        return view;
    }
}
