package com.ss.todolist.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ss.todolist.R;
import com.ss.todolist.TodoItems;
import com.ss.todolist.model.TodoItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TodoItemFragment extends Fragment {
    private static final int ADD_NEW_TODO_ITEM_REQUEST_CODE = 1;
    private static final int EDIT_TODO_ITEM_REQUEST_CODE = 2;

    private final int COUNTER_MAX_VALUE = 3;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private TextInputLayout mTitleInputLayout;
    private EditText mTitleEditText, mDescriptionEditText, mDateEditText, mTimeEditText;
    private CheckBox mReminderCheckBox, mRepeatCheckBox;
    private RadioGroup mRepeatRadioGroup;
    private Button mIncCountButton, mDecCountButton;
    private TextView mCounterTextView, mPriorityTextView;

    private TodoItem mItem;
    private int position;

    private int mCounter = 0;
    private Calendar mCalendar;
    private boolean isEditable = false;

    private final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mDateEditText.setText(dateFormat.format(mCalendar.getTime()));
        }
    };

    private final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);

            mTimeEditText.setText(timeFormat.format(mCalendar.getTime()));
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCounter = savedInstanceState.getInt("counter");
            isEditable = savedInstanceState.getBoolean("isEditable");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_item, container, false);
        mTitleInputLayout = view.findViewById(R.id.title_input_layout);

        mTitleEditText = view.findViewById(R.id.title_edit_text);
        mDescriptionEditText = view.findViewById(R.id.description_edit_text);
        mDateEditText = view.findViewById(R.id.date_edit_text);
        mTimeEditText = view.findViewById(R.id.time_edit_text);

        mReminderCheckBox = view.findViewById(R.id.reminder_check_box);
        mRepeatCheckBox = view.findViewById(R.id.repeat_check_box);

        mRepeatRadioGroup = view.findViewById(R.id.repeat_radio_group);

        mIncCountButton = view.findViewById(R.id.inc_count_button);
        mDecCountButton = view.findViewById(R.id.dec_count_button);

        mCounterTextView = view.findViewById(R.id.counter_text_view);
        mPriorityTextView = view.findViewById(R.id.priority_text_view);

        mTitleEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mDescriptionEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mDateEditText.setInputType(InputType.TYPE_NULL);
        mTimeEditText.setInputType(InputType.TYPE_NULL);

        mIncCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCounter < COUNTER_MAX_VALUE) {
                    mCounter++;
                    mCounterTextView.setText(String.valueOf(mCounter));
                }
            }
        });

        mDecCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCounter > 0) {
                    mCounter--;
                    mCounterTextView.setText(String.valueOf(mCounter));
                }
            }
        });

        mRepeatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRepeatRadioGroup.setVisibility(View.VISIBLE);
                } else {
                    mRepeatRadioGroup.setVisibility(View.GONE);
                }
            }
        });

        mDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new DatePickerDialog(getActivity(), dateSetListener,
                            mCalendar.get(Calendar.YEAR),
                            mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return false;
            }
        });

        mTimeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new TimePickerDialog(getActivity(), timeSetListener,
                            mCalendar.get(Calendar.HOUR_OF_DAY),
                            mCalendar.get(Calendar.MINUTE), true).show();
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        switch (getArguments().getInt("request_code")) {
            case ADD_NEW_TODO_ITEM_REQUEST_CODE: {
                if (savedInstanceState == null) {
                    mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.get(Calendar.DAY_OF_MONTH) + 1);
                    mCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    mCalendar.set(Calendar.MINUTE, 0);
                } else {
                    mCalendar = (Calendar) savedInstanceState.getSerializable("calendar");
                }

                mDateEditText.setText(dateFormat.format(mCalendar.getTime()));
                mTimeEditText.setText(timeFormat.format(mCalendar.getTime()));
                mCounterTextView.setText(String.valueOf(mCounter));

                isEditable = true;
            }
            break;
            case EDIT_TODO_ITEM_REQUEST_CODE: {
                position = getArguments().getInt("position");
                mItem = (TodoItem) TodoItems.getInstance().getItem(position);

                if (savedInstanceState == null) {
                    mCalendar = mItem.getCalendar();
                    mCounter = mItem.getPriority();
                } else {
                    mCalendar = (Calendar) savedInstanceState.getSerializable("calendar");
                }

                mTitleEditText.setText(mItem.getTitle());
                mDescriptionEditText.setText(mItem.getDescription());
                mDateEditText.setText(dateFormat.format(mCalendar.getTime()));
                mTimeEditText.setText(timeFormat.format(mCalendar.getTime()));
                mReminderCheckBox.setChecked(mItem.isReminder());
                mRepeatCheckBox.setChecked(mItem.isRepeat());
                if (mItem.isRepeat()) {
                    mRepeatRadioGroup.check(mItem.getRepeatType());
                }
                mCounterTextView.setText(String.valueOf(mCounter));

                isFieldsEnabled(isEditable);
            }
            break;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter", mCounter);
        outState.putSerializable("calendar", mCalendar);
        outState.putBoolean("isEditable", isEditable);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_item, menu);

        if (!isEditable) {
            menu.findItem(R.id.action_save).setIcon(R.drawable.ic_action_edit);
        } else {
            menu.findItem(R.id.action_save).setIcon(R.drawable.ic_action_save);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                switch (getArguments().getInt("request_code")) {
                    case ADD_NEW_TODO_ITEM_REQUEST_CODE: {
                        addTodoItem();
                    }
                    break;
                    case EDIT_TODO_ITEM_REQUEST_CODE: {
                        editTodoItem();
                    }
                    break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void isFieldsEnabled(boolean flag) {
        mTitleEditText.setEnabled(flag);
        mDescriptionEditText.setEnabled(flag);
        mDateEditText.setEnabled(flag);
        mTimeEditText.setEnabled(flag);
        mReminderCheckBox.setEnabled(flag);
        mRepeatCheckBox.setEnabled(flag);
        if (mItem.isRepeat())
            for (int i = 0; i < mRepeatRadioGroup.getChildCount(); i++) {
                mRepeatRadioGroup.getChildAt(i).setEnabled(flag);
            }
        mIncCountButton.setEnabled(flag);
        mDecCountButton.setEnabled(flag);
        mPriorityTextView.setEnabled(flag);
    }

    private boolean isEmptyInputLayout() {
        if (mTitleEditText.getText().toString().trim().isEmpty()) {
            mTitleInputLayout.setError(getString(R.string.empty_title_error));
            mTitleEditText.requestFocus();
            return true;
        } else {
            mTitleInputLayout.setErrorEnabled(false);
        }

        return false;
    }

    private void addTodoItem() {
        if (!isEmptyInputLayout()) {
            TodoItem item = new TodoItem();
            item.setTitle(mTitleEditText.getText().toString().trim());
            item.setDescription(mDescriptionEditText.getText().toString().trim());
            item.setCalendar(mCalendar);
            item.setReminder(mReminderCheckBox.isChecked());
            item.setRepeat(mRepeatCheckBox.isChecked());
            if (mRepeatCheckBox.isChecked()) {
                item.setRepeatType(mRepeatRadioGroup.getCheckedRadioButtonId());
            }
            item.setPriority(mCounter);

            TodoItems.getInstance().addItem(item);
            getFragmentManager().popBackStack();
        }
    }

    private void editTodoItem() {
        if (!isEditable) {
            isEditable = true;
            getActivity().invalidateOptionsMenu();
            isFieldsEnabled(isEditable);
            return;
        }


        mItem.setTitle(mTitleEditText.getText().toString().trim());
        mItem.setDescription(mDescriptionEditText.getText().toString().trim());
        mItem.setCalendar(mCalendar);
        mItem.setReminder(mReminderCheckBox.isChecked());
        mItem.setRepeat(mRepeatCheckBox.isChecked());
        if (mRepeatCheckBox.isChecked()) {
            mItem.setRepeatType(mRepeatRadioGroup.getCheckedRadioButtonId());
        }
        mItem.setPriority(mCounter);

        TodoItems.getInstance().editItem(position, mItem);
        getFragmentManager().popBackStack();
    }

}
