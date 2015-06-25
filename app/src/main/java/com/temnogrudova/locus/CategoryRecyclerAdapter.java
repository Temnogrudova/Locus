package com.temnogrudova.locus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class CategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface onCategoryItemClickListener {
        public void onViewCategoryItem(CategoryItem categoryItem);
    }
    onCategoryItemClickListener categoryItemClickListener;

    ArrayList<RecyclerViewItem> mRecyclerViewList;
    ArrayList<CategoryItem> mCategoryDataArrayList;
    View view;
    Context context;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();

        try {
            categoryItemClickListener = (onCategoryItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    public CategoryRecyclerAdapter(ArrayList<RecyclerViewItem> recyclerViewItemArrayList, ArrayList<CategoryItem> categoryDataArrayList) {
        mRecyclerViewList = recyclerViewItemArrayList;
        mCategoryDataArrayList = categoryDataArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        view = LayoutInflater.from(context).inflate(R.layout.category_recycler_item, parent, false);
        return CategoryRecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        CategoryRecyclerItemViewHolder holder = (CategoryRecyclerItemViewHolder) viewHolder;

        final String itemText = mRecyclerViewList.get(position).getItemText();
        String itemSubText= mRecyclerViewList.get(position).getItemSubText();
        holder.setItemText(itemText, itemSubText);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            for(CategoryItem categoryItem:mCategoryDataArrayList){
                if (categoryItem.getItemTitle().equals(itemText)) {
                    categoryItemClickListener.onViewCategoryItem(categoryItem);
                }
            }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mRecyclerViewList == null ? 0 : mRecyclerViewList.size();
    }

}
