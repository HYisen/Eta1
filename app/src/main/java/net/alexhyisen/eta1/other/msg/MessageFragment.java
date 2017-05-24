package net.alexhyisen.eta1.other.msg;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.alexhyisen.eta1.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2017/5/19.
 * A Fragment that used as a list of messages.
 */

public class MessageFragment extends Fragment {
    private List<Message> data=new ArrayList<>();
    private RecyclerView.Adapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, container, false);

        adapter = new MessageArrayAdapter(view.getContext(), data);

        RecyclerView listView = (RecyclerView) view.findViewById(R.id.list_view);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        listView.setAdapter(adapter);

        return view;
    }

    public void pushMsg(Message msg) {
        System.out.println("push "+msg.getType()+" msg "+msg.getContent());
        data.add(0,msg);
        adapter.notifyDataSetChanged();
    }
}
