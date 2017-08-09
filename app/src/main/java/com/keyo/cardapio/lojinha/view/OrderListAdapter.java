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

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by renarosantos1 on 26/06/17.
 */

class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolderOrder> {

    private final Context       mContext;
    private       List<Order>   mOrders;
    private       String        mUltimoPedido;
    private       OnItemClicked listener;

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

    public void setOnItemClickedListener(final OnItemClicked listener) {
        this.listener = listener;
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
        private final TextView mDescription;
        private boolean mIsAvailable;

        public ViewHolderOrder(final View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_status);
            mNumber = (TextView) itemView.findViewById(R.id.number);
            mDescription = (TextView) itemView.findViewById(R.id.textLabel);

            itemView.setOnClickListener(v -> {
                if(listener != null) {
                    listener.itemClicked(mNumber.getText().toString(), mIsAvailable);
                }
            });

            itemView.findViewById(R.id.image_trash).setOnClickListener(v -> {
                new SweetAlertDialog(itemView.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Apagar?")
                        .setContentText("Deseja apagar esse número da lista?")
                        .setConfirmText("Apagar")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            if(listener != null) {
                                listener.deleteClicked(mNumber.getText().toString());
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelText("Cancelar")
                        .show();
            });
        }

        public void bind(final Order order) {
            mIsAvailable = order.isAvailableForRetrieve(mUltimoPedido);
            mNumber.setText(order.getNumber());
            if (mIsAvailable) {
                mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check_ok));
                mDescription.setText("Disponível para retirada");
            } else {
                mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check_deny));
                mDescription.setText("Não está pronto");
            }
        }
    }

    public interface OnItemClicked {
        void deleteClicked(String order);
        void itemClicked(String order, boolean ready);
    }
}
