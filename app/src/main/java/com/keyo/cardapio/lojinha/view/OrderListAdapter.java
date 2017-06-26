package com.keyo.cardapio.lojinha.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.keyo.cardapio.R;
import com.keyo.cardapio.lojinha.model.Order;

import java.util.List;

/**
 * Created by renarosantos1 on 26/06/17.
 */

class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolderOrder> {

    private final Context mContext;
    private List<Order> mOrders;
    private String mUltimoPedido;

    public OrderListAdapter(final Context context) {
        mContext = context;
    }

        public void setOrders(final List<Order> orders) {
        mOrders = orders;
        notifyDataSetChanged();
    }

    public void setLastOrder(final String ultimoPedido) {
        mUltimoPedido = ultimoPedido;
    }

    @Override
    public ViewHolderOrder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pedido_item, viewGroup, false);
        return new ViewHolderOrder(item);
    }

    @Override
    public void onBindViewHolder(final ViewHolderOrder viewHolder, final int i) {
        Order order = mOrders.get(i);
        if (order != null) {
            viewHolder.bind(order);
        }
    }

    @Override
    public int getItemCount() {
        return mOrders != null ? mOrders.size() : 0;
    }


    public class ViewHolderOrder extends RecyclerView.ViewHolder {


        private final ImageView mImageView;
        private final TextView mNumber;

        public ViewHolderOrder(final View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_status);
            mNumber = (TextView) itemView.findViewById(R.id.number);
        }

        public void bind(final Order order) {
            boolean isAvailable = order.isAvailableForRetrieve(mUltimoPedido);
            mNumber.setText(order.getNumber());
            if (isAvailable) {
                mImageView.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.presence_online));
            } else {
                mImageView.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.presence_offline));
            }
        }
    }
}
