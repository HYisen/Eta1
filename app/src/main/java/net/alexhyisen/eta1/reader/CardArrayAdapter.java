package net.alexhyisen.eta1.reader;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexhyisen.eta1.R;
import net.alexhyisen.eta1.other.MyCallback;

import java.util.List;
import java.util.Optional;

/**
 * Created by Alex on 2017/6/9.
 * An adapter for card lists.
 */

class CardArrayAdapter extends RecyclerView.Adapter<CardArrayAdapter.CardViewHolder> {
    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        CardViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.cardText);
        }
    }

    private List<Card> data;

    CardArrayAdapter(List<Card> data) {
        this.data = data;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.text.setText(data.get(position).getText());
        //I can't use @TextAlignment in args, what else can I do?
        //noinspection WrongConstant
        holder.text.setTextAlignment(data.get(position).getTextAlignment());

        holder.itemView.setClickable(false);
        holder.itemView.setLongClickable(false);

        Optional<MyCallback<Void>> handler = data.get(position).getHandler();

        if (handler.isPresent()) {
            //It seems as if the replacement of lambda to anonymous class is unstable there.
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d("STEP", handler.get().toString());
                    handler.get().accept(null);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
