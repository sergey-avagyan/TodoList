package com.ss.todolist.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss.todolist.R;
import com.ss.todolist.db.DatabaseManager;
import com.ss.todolist.adapter.TodoItemsAdapter;
import com.ss.todolist.model.TodoItem;
import com.ss.todolist.util.KeyboardUtil;

import java.util.UUID;

public class TodoListFragment extends Fragment {

    private TodoItemsAdapter.OnItemClickListener mOnItemSelectedListener = new TodoItemsAdapter.OnItemClickListener() {
        @Override
        public void onClickItem(UUID id) {
            editTodoItem(id);
        }

        @Override
        public void onLongClickItem(int visibility) {
            setFloatButtonVisibility(visibility);
            mTodoItemsAdapter.notifyDataSetChanged();
        }
    };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    addTodoItem();
                    break;
            }
        }
    };

    private TodoItemsAdapter mTodoItemsAdapter;
    private FloatingActionButton mFab;

    public TodoListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        KeyboardUtil.hideKeyboardFrom(getActivity(), getView());

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        if (mTodoItemsAdapter != null) {
            mTodoItemsAdapter.setItems(DatabaseManager.getInstance(getActivity()).getItems());
            mTodoItemsAdapter.notifyDataSetChanged();
        }

    }

    private void init(View view) {
        mTodoItemsAdapter = new TodoItemsAdapter(getActivity());
        mTodoItemsAdapter.setItems(DatabaseManager.getInstance(getActivity()).getItems());
        mTodoItemsAdapter.setOnItemClickListener(mOnItemSelectedListener);

        mFab = view.findViewById(R.id.fab);
        mFab.setOnClickListener(mOnClickListener);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mTodoItemsAdapter);
    }

    private void addTodoItem() {
        Bundle args = new Bundle();
        args.putInt(TodoItemFragment.REQUEST_CODE_ARG, TodoItemFragment.ADD_NEW_TODO_ITEM_REQUEST_CODE);

        replaceFragment(args, "tag2");
    }

    private void editTodoItem(UUID id) {
        Bundle args = new Bundle();
        args.putInt(TodoItemFragment.REQUEST_CODE_ARG, TodoItemFragment.EDIT_TODO_ITEM_REQUEST_CODE);
        args.putSerializable(TodoItemFragment.ID_ARG, id);

        replaceFragment(args, "tag3");
        setFloatButtonVisibility(View.GONE);
    }

    private void replaceFragment(Bundle args, String tag) {
        TodoItemFragment todoItemFragment = new TodoItemFragment();
        todoItemFragment.setArguments(args);
        todoItemFragment.setOnInteractionListener(new TodoItemFragment.OnFragmentInteractionListener() {
            @Override
            public void onAddItem(TodoItem item) {
                mTodoItemsAdapter.addItem(item);
            }

            @Override
            public void onEditItem(TodoItem item) {
                mTodoItemsAdapter.editItem(item);
            }
        });

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, todoItemFragment, tag)
                .addToBackStack(null)
                .commit();
    }

    private void setFloatButtonVisibility(int visibility) {
        mFab.setVisibility(visibility);
    }
}
