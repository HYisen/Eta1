package net.alexhyisen.eta1.other.msg;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import net.alexhyisen.eta1.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2017/5/19.
 * A Fragment that used as a list of messages.
 */

public class MessageFragment extends ListFragment {
    private List<Message> data=new ArrayList<>();
    private ArrayAdapter<Message> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, container, false);

        adapter = new MessageArrayAdapter(view.getContext(), data);
        adapter.setNotifyOnChange(true);//additional safety guarantee, on default true
        setListAdapter(adapter);

        return view;
    }

    public void pushMsg(Message msg) {
        System.out.println("push "+msg.getType()+" msg "+msg.getContent());
        data.add(0,msg);
        adapter.notifyDataSetChanged();
    }
}
