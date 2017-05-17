package net.alexhyisen.eta1.other.msg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.alexhyisen.eta1.R;

import java.util.List;

/**
 * Created by Alex on 2017/5/7.
 * Adapter used for a better ListView of Messages.
 */

public class MessageArrayAdapter extends ArrayAdapter<Message> {
    private final LayoutInflater mInflater;
    private final Context mContext;
    //Here, must be bind to the correct layout resource that fits createViewFromResource().
    private static final int mResource= R.layout.list_item;


    public MessageArrayAdapter(@NonNull Context context, @NonNull List<Message> objects) {
        super(context, mResource, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //super.getView(position, convertView, parent);
        return createViewFromResource(mInflater, position, convertView, parent, mResource);
    }

    //copied and modified from ArrayAdapter.createViewFromResource
    private @NonNull View createViewFromResource(@NonNull LayoutInflater inflater, int position,
                                                 @Nullable View convertView, @NonNull ViewGroup parent, int resource) {
        final View view =
                (convertView == null ? inflater.inflate(resource, parent, false) : convertView);
        final TextView typeText=(TextView) view.findViewById(R.id.typeText);
        final TextView timeText=(TextView) view.findViewById(R.id.timeText);
        final TextView contentText=(TextView) view.findViewById(R.id.contentText);
        final Message item = getItem(position);
        assert item != null;

        switch (item.getType()) {
            case INFO:
                typeText.setText(R.string.msg_type_info);
                //mContext.getColor() is what I want, but it need API 23.
                typeText.setTextColor(mContext.getColor(R.color.colorMsgInfo));
                break;
            case ERROR:
                typeText.setText(R.string.msg_type_error);
                typeText.setTextColor(mContext.getColor(R.color.colorMsgError));
                break;
            case SERVER:
                typeText.setText(R.string.msg_type_server);
                typeText.setTextColor(mContext.getColor(R.color.colorMsgServer));
                break;
            case CLIENT:
                typeText.setText(R.string.msg_type_client);
                typeText.setTextColor(mContext.getColor(R.color.colorMsgClient));
                break;
            default:
                typeText.setText(R.string.msg_type_undefined);
        }

        timeText.setText(item.getTime());

        contentText.setText(item.getContent());

        return view;
    }
}
