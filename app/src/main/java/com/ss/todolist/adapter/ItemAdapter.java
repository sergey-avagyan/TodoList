package com.ss.todolist.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ss.todolist.R;
import com.ss.todolist.model.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import static com.ss.todolist.model.MonthItem.MONTH_ITEM_TYPE;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener mListener;

    private Context mContext;
    private ActionMode mActionMode;

    private List<Item> mItems;
    private TreeSet<Integer> mSelectedItems = new TreeSet<>();

    public ItemAdapter(Context context, List<Item> list) {
        mContext = context;
        mItems = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case MonthItem.MONTH_ITEM_TYPE: {
                view = LayoutInflater.from(mContext).inflate(R.layout.month_item_view, parent, false);
                return new MonthItemViewHolder(view);

            }
            case TodoItem.TODO_ITEM_TYPE: {
                view = LayoutInflater.from(mContext).inflate(R.layout.todo_item, parent, false);
                return new TodoItemViewHolder(view);
            }
            default: {
                throw new IllegalArgumentException("View type was not found");
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TodoItem.TODO_ITEM_TYPE: {
                ((TodoItemViewHolder) holder).bind((TodoItem) mItems.get(position), mListener);
            }
            break;
            case MonthItem.MONTH_ITEM_TYPE: {
                ((MonthItemViewHolder) holder).bind((MonthItem) mItems.get(position));
            }
            break;
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (mItems.get(position).getType()) {
            case MonthItem.MONTH_ITEM_TYPE:
                return MonthItem.MONTH_ITEM_TYPE;
            case TodoItem.TODO_ITEM_TYPE:
                return TodoItem.TODO_ITEM_TYPE;
            default:
                return -1;
        }
    }

    public void addItem(TodoItem item) {
        mItems.add(item);
        sort();
        int i = mItems.indexOf(item);
        Calendar monthCalendar = Calendar.getInstance();
        monthCalendar.set(item.getCalendar().get(Calendar.YEAR),
                item.getCalendar().get(Calendar.MONTH), 1, 0, 0);
        if (i == 0) {
            mItems.add(0, new MonthItem(monthCalendar));
        } else if (mItems.get(i - 1).getCalendar().get(Calendar.MONTH) != item.getCalendar().get(Calendar.MONTH)) {
            mItems.add(i, new MonthItem(monthCalendar));
        }
    }

    public void editItem(int position, TodoItem item) {
        if (mItems.get(position - 1).getType() == MONTH_ITEM_TYPE) {
            if (position < mItems.size() - 1 && mItems.get(position + 1).getType() != mItems.get(position).getType()) {
                mItems.remove(position - 1);
            } else if (position == mItems.size() - 1) {
                mItems.remove(position - 1);
            }
        }
        sort();
        int i = mItems.indexOf(item);
        Calendar monthCalendar = Calendar.getInstance();
        monthCalendar.set(item.getCalendar().get(Calendar.YEAR),
                item.getCalendar().get(Calendar.MONTH), 1, 0, 0);
        if (i == 0) {
            mItems.add(0, new MonthItem(monthCalendar));
        } else if (mItems.get(i - 1).getCalendar().get(Calendar.MONTH) != item.getCalendar().get(Calendar.MONTH)) {
            mItems.add(i, new MonthItem(monthCalendar));
        }
    }

    private void deleteItems(TreeSet<Integer> selectedItems) {
        int positionChangedBy = 0;
        for (Integer i : selectedItems) {
            int position = i - positionChangedBy;
            if (mItems.get(position - 1).getType() == MONTH_ITEM_TYPE) {
                if ((position < mItems.size() - 1 && mItems.get(position + 1).getType() != mItems.get(position).getType())
                        || position == mItems.size() - 1) {
                    mItems.remove(position - 1);
                    mItems.remove(position - 1);
                    positionChangedBy++;
                } else {
                    mItems.remove(position);
                }
            } else {
                mItems.remove(position);
            }
            positionChangedBy++;
        }
    }

    private void sort() {
        Collections.sort(mItems, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                int c = Long.compare(o1.getCalendar().getTimeInMillis() / 60000,
                        o2.getCalendar().getTimeInMillis() / 60000);
                if (c == 0)
                    c = Integer.compare(o1.getType(), o2.getType());
                return c;

            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onClickItem(int position);
        void onLongClickItem(int visibility);
    }

    class TodoItemViewHolder extends RecyclerView.ViewHolder {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());

        private TextView titleTextView, descriptionTextView, dateTextView;
        private CheckBox deleteCheckBox;

        TodoItemViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            descriptionTextView = itemView.findViewById(R.id.description);
            dateTextView = itemView.findViewById(R.id.date);
            deleteCheckBox = itemView.findViewById(R.id.delete_checkbox);

        }

        void bind(final TodoItem item, final OnItemClickListener listener) {
            titleTextView.setText(item.getTitle());
            if (item.getDescription().isEmpty()) {
                descriptionTextView.setVisibility(View.GONE);
            } else {
                descriptionTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setText(item.getDescription());
            }
            dateTextView.setText(dateFormat.format(item.getCalendar().getTime()));

            if (mActionMode != null) {
                deleteCheckBox.setVisibility(View.VISIBLE);

                if (mSelectedItems.contains(getAdapterPosition())) {
                    deleteCheckBox.setChecked(true);
                } else {
                    deleteCheckBox.setChecked(false);
                }
            } else {
                deleteCheckBox.setChecked(false);
                deleteCheckBox.setVisibility(View.GONE);
            }

            deleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        selectItem(getAdapterPosition(), true);
                    else
                        selectItem(getAdapterPosition(), false);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActionMode == null && listener != null) {
                        listener.onClickItem(getAdapterPosition());
                    } else {
                        deleteCheckBox.setChecked(!deleteCheckBox.isChecked());
                    }
                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mActionMode == null) {
                        mActionMode = ((AppCompatActivity) itemView.getContext()).startSupportActionMode(new android.support.v7.view.ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                                mode.getMenuInflater().inflate(R.menu.menu_list, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                                return false;
                            }

                            @Override
                            public boolean onActionItemClicked(final android.support.v7.view.ActionMode mode, MenuItem item) {
                                if (mSelectedItems.size() != 0) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setMessage("Delete " + mSelectedItems.size() + " items?")
                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteItems(mSelectedItems);
                                                    mode.finish();
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .create().show();
                                } else {
                                    mode.finish();
                                }
                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
                                mActionMode = null;
                                mSelectedItems.clear();
                                mListener.onLongClickItem(View.VISIBLE);
                            }
                        });
                        selectItem(getAdapterPosition(), true);
                        mListener.onLongClickItem(View.INVISIBLE);
                    }
                    return true;
                }
            });
        }

        void selectItem(int position, boolean flag) {
            if (flag) {
                mSelectedItems.add(position);
            } else {
                mSelectedItems.remove(position);
            }
            if (mActionMode != null)
                mActionMode.setTitle(String.valueOf(mSelectedItems.size()) + " selected");
        }

    }

    class MonthItemViewHolder extends RecyclerView.ViewHolder {
        private final SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());

        private TextView mMonthTextView;

        MonthItemViewHolder(View itemView) {
            super(itemView);
            mMonthTextView = itemView.findViewById(R.id.month_text_view);
        }

        void bind(MonthItem item) {
            mMonthTextView.setText(monthFormat.format(item.getCalendar().getTimeInMillis()));
        }
    }
}