package net.alexhyisen.eta1.other.msg;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexhyisen.eta1.R;

import java.util.List;

/**
 * Created by Alex on 2017/5/7.
 * Adapter used for a better ListView of Messages.
 */

class MessageArrayAdapter extends RecyclerView.Adapter<MessageArrayAdapter.MessageViewHolder> {
    static class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView typeText;
        TextView timeText;
        TextView contentText;

        MessageViewHolder(View itemView) {
            super(itemView);
            typeText = (TextView) itemView.findViewById(R.id.typeText);
            timeText = (TextView) itemView.findViewById(R.id.timeText);
            contentText = (TextView) itemView.findViewById(R.id.contentText);
        }
    }

    private final Context mContext;

    private List<Message> data;

    MessageArrayAdapter(Context mContext, List<Message> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        switch (data.get(position).getType()) {
            case INFO:
                holder.typeText.setText(R.string.msg_type_info);
                holder.typeText.setTextColor(mContext.getColor(R.color.colorMsgInfo));
                break;
            case ERROR:
                holder.typeText.setText(R.string.msg_type_error);
                holder.typeText.setTextColor(mContext.getColor(R.color.colorMsgError));
                break;
            case SERVER:
                holder.typeText.setText(R.string.msg_type_server);
                holder.typeText.setTextColor(mContext.getColor(R.color.colorMsgServer));
                break;
            case CLIENT:
                holder.typeText.setText(R.string.msg_type_client);
                holder.typeText.setTextColor(mContext.getColor(R.color.colorMsgClient));
                break;
            default:
                holder.typeText.setText(R.string.msg_type_undefined);
        }

        holder.timeText.setText(data.get(position).getTime());

        holder.contentText.setText(data.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
