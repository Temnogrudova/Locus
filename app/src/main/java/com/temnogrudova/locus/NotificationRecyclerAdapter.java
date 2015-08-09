package com.temnogrudova.locus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class NotificationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface onNotificationItemClickListener {
        public void onViewNotificationItem(NotificationItem title, String parent);
    }
    onNotificationItemClickListener notificationItemClickListener;
   // ArrayList<RecyclerViewItem> mRecyclerViewList;
    ArrayList<NotificationItem> mNotificationDataArrayList;
    String mParent;
    View view;
    Context context;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
        try {
            notificationItemClickListener = (onNotificationItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    public NotificationRecyclerAdapter( ArrayList<NotificationItem> notificationDataArrayList, String parent) {
      //  mRecyclerViewList = recyclerViewItemArrayList;
        mNotificationDataArrayList = notificationDataArrayList;
        mParent = parent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        view = LayoutInflater.from(context).inflate(R.layout.notification_recycler_item, parent, false);
        return NotificationRecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        NotificationRecyclerItemViewHolder holder = (NotificationRecyclerItemViewHolder) viewHolder;

        final String itemText = mNotificationDataArrayList.get(position).getItemTitle();
        String itemSubText = mNotificationDataArrayList.get(position).getItemNote();
        holder.setItemText( itemText, itemSubText);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(NotificationItem notificationItem:mNotificationDataArrayList){
                    if (notificationItem.getItemTitle().equals(itemText)) {
                        notificationItemClickListener.onViewNotificationItem(notificationItem, mParent);
                    }
                }
               // notificationItemClickListener.onViewNotificationItem(mRecyclerViewList.get(position).getItemText());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotificationDataArrayList == null ? 0 : mNotificationDataArrayList.size();
    }

}
