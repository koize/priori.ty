package com.koize.priority.ui.monthlyplanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.koize.priority.GetMap;
import com.koize.priority.NotiReceiver;
import com.koize.priority.R;
import com.koize.priority.ShowMap;
import com.koize.priority.ui.category.CategoryData;
import com.koize.priority.ui.category.CategoryPopUp;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class EventShowAndEditPopUp extends AppCompatActivity implements CategoryPopUp.CategoryCallBack {
    public static final int INPUT_METHOD_NEEDED = 1;
    private static final int image_chooser_request_code = 999;
    private static final int get_map_request_code= 998;
    EventData eventData;
    CategoryData categoryData;
    EditText eventTitle;
    EditText eventType;
    TableRow dateChip;
    TextView dateText;
    TableRow timeChip;
    TextView timeText;
    Switch allDay;
    TableRow reminderDateChip;
    TextView reminderText;
    EditText eventLocationText;
    Chip eventLocationChip;
    double eventLatitude;
    double eventLongitude;
    Chip eventCategoryChip;
    Chip eventDescImageChip;
    EditText eventDescText;
    Chip eventSaveChip;
    private String eventStartDate;
    private String eventEndDate;
    private long eventStartDateEpoch;
    private long eventEndDateEpoch;
    private long eventStartDateStartTime;
    private long eventEndDateTime;

    private int eventStartHr;
    private int eventStartMin;
    private int eventEndHr;
    private int eventEndMin;

    private String reminderDate;
    private long reminderDateTime;
    private int reminderHr;
    private int reminderMin;
    private boolean isAllDay;
    Chip eventCategoryCard;
    private ImageView eventImageView;
    private Uri eventImageUri;
    DatabaseReference databaseEventListReference;
    FirebaseUser user;
    private MaterialTextView eventShowTitle;
    private MaterialTextView eventShowType;
    private MaterialTextView eventShowDate;
    private MaterialTextView eventShowTime;
    private TableRow eventShowReminderRow;
    private MaterialTextView eventShowReminder;
    private TableRow eventShowLocationRow;
    private MaterialTextView eventShowLocation;
    private Chip eventShowLocationMap;
    private Chip eventShowCategory;
    private TableRow eventShowDescRow;
    private MaterialTextView eventShowDesc;
    private ImageView eventShowImage;
    private Chip eventShowEdit;
    private Chip eventShowDelete;
    boolean checkExists = false;
    StorageReference storageRef;
    FragmentManager fragmentManager;
    Activity activity;
    Context context;
    Fragment fragment;

    public EventShowAndEditPopUp() {
    }

    public EventShowAndEditPopUp(EventData eventData, Activity activity, DatabaseReference databaseEventListReference, FirebaseUser user, StorageReference storageRef, FragmentManager fragmentManager, Context context, Fragment fragment) {
        this.eventData = eventData;
        this.activity = activity;
        this.databaseEventListReference = databaseEventListReference;
        this.user = user;
        this.storageRef = storageRef;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.fragment = fragment;
    }


    @Override
    public void sendCategory(CategoryData categoryData) {
        eventCategoryCard.setText(categoryData.getCategoryTitle());
        eventCategoryCard.setChipBackgroundColor(ColorStateList.valueOf(categoryData.getCategoryColor()));
        eventCategoryCard.setVisibility(View.VISIBLE);
        this.categoryData = categoryData;

    }



    public void showSavedEventPopupWindow(final View view, EventData eventData) {

        ConstraintLayout reminderView;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_events_show, null);

        //Specify the length and width through constants

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        // Closes the popup window when touch outside
        //Handler for clicking on the inactive zone of the window

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setAnimationStyle(com.google.android.material.R.style.Animation_AppCompat_Dialog);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setInputMethodMode(INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        View container = popupWindow.getContentView().getRootView();
        if (container != null) {
            WindowManager wm = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.3f;
            if (wm != null) {
                wm.updateViewLayout(container, p);
            }
        }
        //Initialize the elements of our window, install the handler

        eventShowTitle = popupView.findViewById(R.id.title_show_event);
        eventShowType = popupView.findViewById(R.id.show_event_type);
        eventShowDate = popupView.findViewById(R.id.show_event_date);
        eventShowTime = popupView.findViewById(R.id.show_event_time);
        eventShowReminder = popupView.findViewById(R.id.show_event_reminder);
        eventShowReminderRow = popupView.findViewById(R.id.button_show_event_reminder);
        eventShowLocationRow = popupView.findViewById(R.id.show_event_location_table_row);
        eventShowLocation = popupView.findViewById(R.id.show_event_location_name);
        eventShowLocationMap = popupView.findViewById(R.id.button_show_event_get_location);
        eventShowCategory = popupView.findViewById(R.id.show_event_category_card);
        eventShowDescRow = popupView.findViewById(R.id.show_event_desc_table_row);
        eventShowDesc = popupView.findViewById(R.id.show_event_desc);
        eventShowImage = popupView.findViewById(R.id.show_event_desc_image);
        eventShowEdit = popupView.findViewById(R.id.button_show_event_edit);
        eventShowEdit.setVisibility(View.INVISIBLE); //DO NOT REMOVVE
        eventShowDelete = popupView.findViewById(R.id.button_show_event_delete);

        eventShowTitle.setText(eventData.getEventTitle());
        eventShowType.setText(eventData.getEventType());
        eventShowDate.setText(eventData.getEventStartDate());
        if (eventData.getEventAllDay()) {
            eventShowTime.setText("All day");
        } else {
            eventShowTime.setText(convertTimestampToTimeRange(eventData.getEventStartDateTime(), eventData.getEventEndDateTime(), eventData.getEventAllDay()));
        }
        if (eventData.getEventReminderDateTime() == 0) {
            eventShowReminderRow.setVisibility(View.GONE);
        } else {
            eventShowReminderRow.setVisibility(View.VISIBLE);
        }
        LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(eventData.getEventReminderDateTime() - 28800000 ), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedTime = formatter.format(dateTime1);
        eventShowReminder.setText(eventData.getEventReminderDate() + ", " + formattedTime);
        if (eventData.getEventLocationName().isEmpty() && eventData.getEventLatitude() == 0 && eventData.getEventLongitude() == 0) {
            eventShowLocationRow.setVisibility(View.GONE);
        } else {
            eventShowLocationRow.setVisibility(View.VISIBLE);
        }
        eventShowLocation.setText(eventData.getEventLocationName());
        eventShowCategory.setText(eventData.getEventCategory().getCategoryTitle());
        eventShowCategory.setChipBackgroundColor(ColorStateList.valueOf(eventData.getEventCategory().getCategoryColor()));
        if (eventData.getEventDesc().isEmpty()) {
            eventShowDesc.setText("No description");
        } else {
            eventShowDesc.setText(eventData.getEventDesc());
        }


        Glide.with(view.getContext())
                .load(storageRef.child(eventData.getEventTextId()))
                .into(eventShowImage);

        eventShowLocationMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventData.getEventLatitude() == 0 && eventData.getEventLongitude() == 0) {
                    Snackbar.make(v, "No location set!", Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    Intent intent = new Intent(context, ShowMap.class);
                    intent.putExtra("locationName", eventData.getEventLocationName());
                    intent.putExtra("lat", eventData.getEventLatitude());
                    intent.putExtra("lon", eventData.getEventLongitude());
                    context.startActivity(intent);
                }
            }
        });
        eventShowEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventData.getEventType() != "Holiday") {
                    showEditEventPopupWindow(v, eventData);
                    popupWindow.dismiss();
                } else {
                    Snackbar.make(v, "Cannot edit holiday!", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
        eventShowDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Set the message show for the Alert time
                builder.setMessage("Delete event: " + eventData.getEventTitle() + "? ");

                // Set Alert Title
                builder.setTitle("Delete?");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(true);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click yes button then app will close
                    Intent intent = new Intent(context, NotiReceiver.class);
                    intent.putExtra(NotiReceiver.NOTIFICATION, eventData.getEventTitle());
                    intent.putExtra("id", eventData.getEventId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, eventData.getEventId(), intent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    databaseEventListReference.child(eventData.getEventTextId()).removeValue();
                    storageRef.child(eventData.getEventTextId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });
                    Snackbar.make(v, "Event deleted!", Snackbar.LENGTH_SHORT)
                            .show();
                    dialog.dismiss();
                    popupWindow.dismiss();
                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

    }

    public void showEditEventPopupWindow(final View view, EventData eventData) {

        ConstraintLayout reminderView;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_events_add, null);

        //Specify the length and width through constants

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        // Closes the popup window when touch outside
        //Handler for clicking on the inactive zone of the window

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setAnimationStyle(com.google.android.material.R.style.Animation_AppCompat_Dialog);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setInputMethodMode(INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        View container = popupWindow.getContentView().getRootView();
        if (container != null) {
            WindowManager wm = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.3f;
            if (wm != null) {
                wm.updateViewLayout(container, p);
            }
        }
        //Initialize the elements of our window, install the handler
        TextView eventTopTile = popupView.findViewById(R.id.title_new_event);
        eventTitle = popupView.findViewById(R.id.new_event_title);
        eventType = popupView.findViewById(R.id.new_event_type);
        dateChip = popupView.findViewById(R.id.button_new_event_datepicker);
        dateText = popupView.findViewById(R.id.new_event_date_start_title);
        timeChip = popupView.findViewById(R.id.button_new_event_timepicker);
        timeText = popupView.findViewById(R.id.new_event_time_title);
        allDay = popupView.findViewById(R.id.switch_new_event_time_allday);
        reminderDateChip = popupView.findViewById(R.id.button_new_event_reminder);
        reminderText = popupView.findViewById(R.id.new_event_reminder_header);
        eventLocationText = popupView.findViewById(R.id.new_event_location_header);
        eventLocationChip = popupView.findViewById(R.id.button_new_event_get_location);
        eventCategoryChip = popupView.findViewById(R.id.button_new_event_get_category);
        eventCategoryCard = popupView.findViewById(R.id.new_event_category_card);
        eventDescImageChip = popupView.findViewById(R.id.button_new_event_get_image);
        eventImageView = popupView.findViewById(R.id.new_event_image);
        eventDescText = popupView.findViewById(R.id.new_event_desc);

        eventSaveChip = popupView.findViewById(R.id.button_new_event_save);

        eventTopTile.setText("Edit event");
        eventTitle.setText(eventData.getEventTitle());
        eventType.setText(eventData.getEventType());
        dateText.setText(eventData.getEventStartDate());
        if (eventData.getEventAllDay()) {
            timeText.setText("All day");
            eventStartHr = 0;
            eventStartMin = 0;
            eventEndHr = 23;
            eventEndMin = 59;
            isAllDay = true;
            allDay.setChecked(true);

        } else {
            isAllDay = false;
            allDay.setChecked(false);
            timeText.setText(convertTimestampToTimeRange(eventData.getEventStartDateTime(), eventData.getEventEndDateTime(), eventData.getEventAllDay()));
        }
        if (eventData.getEventReminderDateTime() == 0) {
            reminderText.setText("Reminder not set");
        } else {
            reminderHr = (int) ((eventData.getEventReminderDateTime() % 86400000) / 3600000);
            reminderMin = (int) (((eventData.getEventReminderDateTime() % 86400000) % 3600000) / 60000);
            reminderDate = eventData.getEventReminderDate();
            reminderDateTime = eventData.getEventReminderDateTime();

            LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(eventData.getEventReminderDateTime() - 28800000), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
            String formattedTime = formatter.format(dateTime1);
            reminderText.setText(eventData.getEventReminderDate() + ", " + formattedTime);
        }
        eventLocationText.setText(eventData.getEventLocationName());
        eventCategoryCard.setText(eventData.getEventCategory().getCategoryTitle());
        eventCategoryCard.setChipBackgroundColor(ColorStateList.valueOf(eventData.getEventCategory().getCategoryColor()));
        Glide.with(view.getContext())
                .load(storageRef.child(eventData.getEventTextId()))
                .into(eventImageView);
        eventDescText.setText(eventData.getEventDesc());


        dateChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();

                // now define the properties of the
                // materialDateBuilder that is title text as SELECT A DATE
                materialDateBuilder.setTitleText("Choose event dates");

                // now create the instance of the material date
                // picker
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = materialDateBuilder.build();

                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");


                materialDatePicker.addOnPositiveButtonClickListener(
                        new MaterialPickerOnPositiveButtonClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onPositiveButtonClick(Object selection) {

                                eventStartDateStartTime = (long) Pair.class.cast(selection).first;
                                eventEndDateTime = (long) Pair.class.cast(selection).second;
                                eventStartDateEpoch = (long) Pair.class.cast(selection).first;
                                eventEndDateEpoch = (long) Pair.class.cast(selection).second;
                                eventStartDate = materialDatePicker.getHeaderText();
                                dateText.setText(eventStartDate);
                                mTimePicker1();

                            }
                        });
            }
        });
        timeChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker1();
            }
        });
        allDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (allDay.isChecked()) {
                    timeText.setText("All day");
                    eventStartHr = 0;
                    eventStartMin = 0;
                    eventEndHr = 23;
                    eventEndMin = 59;
                    isAllDay = true;
                    eventData.setEventAllDay(isAllDay);
                } else {
                    isAllDay = false;
                    eventData.setEventAllDay(isAllDay);
                    timeText.setText(convertTimestampToTimeRange(eventStartDateStartTime, eventEndDateTime, isAllDay));
                }
            }
        });
        reminderDateChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

                // now define the properties of the
                // materialDateBuilder that is title text as SELECT A DATE
                materialDateBuilder.setTitleText("Reminder Date");

                // now create the instance of the material date
                // picker
                MaterialDatePicker materialDatePicker = materialDateBuilder.build();

                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");


                materialDatePicker.addOnPositiveButtonClickListener(
                        new MaterialPickerOnPositiveButtonClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onPositiveButtonClick(Object selection) {
                                reminderDate = materialDatePicker.getHeaderText();
                                reminderDateTime = (long) selection;
                                mTimePicker3();

                            }
                        });
            }
        });
        eventLocationChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GetMap.class);
                if (eventData.getEventLatitude() != 0 && eventData.getEventLongitude() != 0) {
                    intent.putExtra("lat", eventData.getEventLatitude());
                    intent.putExtra("lon", eventData.getEventLongitude());
                }
                fragment.startActivityForResult(intent, get_map_request_code);
            }
        });
        eventCategoryChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryPopUp categoryPopUp = new CategoryPopUp(categoryData -> EventShowAndEditPopUp.this.sendCategory(categoryData));
                categoryPopUp.showPopupWindow(v);
            }
        });
        eventDescImageChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageChooser.class);
                fragment.startActivityForResult(intent, image_chooser_request_code);
            }
        });
        eventSaveChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                if (user == null) {
                    Snackbar.make(v, "Not signed in!", Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    if (eventTitle.getText().toString().isEmpty()) {
                        Snackbar.make(v, "Please enter a title!", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    } else {
                        eventData.setEventTitle(eventTitle.getText().toString());
                    }
                    if (eventType.getText().toString().isEmpty()) {
                        eventData.setEventType("Event");
                    } else {
                        eventData.setEventType(eventType.getText().toString());
                    }
                    if (eventStartDateStartTime != 0) {
                        eventData.setEventStartDateEpoch(eventStartDateEpoch);
                        eventData.setEventEndDateEpoch(eventEndDateEpoch);
                        eventData.setEventStartDateTime(eventStartDateStartTime);
                        eventData.setEventEndDateTime(eventEndDateTime);
                    }
                    if (isAllDay == false && eventData.getEventStartDateTime() == eventData.getEventStartDateEpoch() && eventData.getEventEndDateTime() == eventData.getEventEndDateEpoch()) {
                        isAllDay = true;
                    }
                    eventData.setEventAllDay(isAllDay);
                    if (reminderDateTime != 0) {
                        eventData.setEventReminderDate(reminderDate);
                        eventData.setEventReminderDateTime(reminderDateTime);
                    }
                    if (eventData.getEventLatitude() != 0 && eventData.getEventLocationName() == null) {
                        Snackbar.make(v, "Please enter a location name!", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    eventData.setEventLocationName(eventLocationText.getText().toString());
                    if (eventLatitude != 0){
                        eventData.setEventLatitude(eventLatitude);
                        eventData.setEventLongitude(eventLongitude);
                    }

                    if (categoryData == null) {
                        categoryData = new CategoryData();
                        categoryData.setCategoryTitle("Others");
                    }

                    if (eventData.getImageUri() == null && eventImageUri != null) {
                        File file = new File(eventImageUri.getPath());
                        uploadImage(file, eventImageUri, eventData.getEventTextId(), v);
                    }
                    else if (eventData.getImageUri() == null && eventImageUri == null) {

                    }
                    else if (eventData.getImageUri() != null && eventImageUri != null) {
                        eventData.setImageUri(eventImageUri);
                    }
                    else if (eventData.getImageUri() != null && eventImageUri == null) {
                        eventData.setImageUri(eventData.getImageUri());
                    }
                    else {
                    }
                    eventData.setEventCategory(categoryData);
                    eventData.setEventDesc(eventDescText.getText().toString());
                    try {
                        databaseEventListReference.child(eventData.getEventTextId()).setValue(eventData);
                    }
                    catch (Exception e) {
                        Log.e("Error", e.toString(), e.getCause().getCause());
                    }
                    Snackbar.make(view, "Event saved", Snackbar.LENGTH_SHORT)
                            .show();
                    popupWindow.dismiss();
                }
            }
        });


    }
    public void mTimePicker1() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("Event start time").setTimeFormat(TimeFormat.CLOCK_24H);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        eventStartHr = materialTimePicker.getHour();
                        eventStartMin = materialTimePicker.getMinute();
                        eventStartDateStartTime = eventStartDateStartTime + (eventStartHr * 3600000) + (eventStartMin * 60000);
                        mTimePicker2();
                    }
                }
        );
        materialTimePicker.show(fragmentManager, "MATERIAL_TIME_PICKER");
    }

    public void mTimePicker2() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("Event end time").setTimeFormat(TimeFormat.CLOCK_24H);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        eventEndHr = materialTimePicker.getHour();
                        eventEndMin = materialTimePicker.getMinute();
                        eventEndDateTime = eventEndDateTime + (eventEndHr * 3600000) + (eventEndMin * 60000);
                        timeText.setText(String.format("%02d:%02d", eventStartHr, eventStartMin) + " - " + String.format("%02d:%02d", eventEndHr, eventEndMin));
                    }
                }
        );
        materialTimePicker.show(fragmentManager, "MATERIAL_TIME_PICKER");
    }

    public void mTimePicker3() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("Reminder time").setTimeFormat(TimeFormat.CLOCK_24H);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reminderHr = materialTimePicker.getHour();
                        reminderMin = materialTimePicker.getMinute();
                        reminderDateTime = reminderDateTime + (reminderHr * 3600000) + (reminderMin * 60000);
                        reminderText.setText(reminderDate + ", " + String.format("%02d:%02d", reminderHr, reminderMin));
                    }
                }
        );
        materialTimePicker.show(fragmentManager, "MATERIAL_TIME_PICKER");
    }
    private void uploadImage(File filePath, Uri uri, String eventTextId, View view)
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            storageRef
                    .child(
                            eventTextId
                    ).putFile(uri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Snackbar.make(view, "Image Uploaded!", Snackbar.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Snackbar.make(view, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == image_chooser_request_code && resultCode == RESULT_OK && data != null) {
            eventImageUri = (Uri) data.getExtras().get("image");
            eventImageView.setImageURI(eventImageUri);
        }
        if (requestCode == get_map_request_code && resultCode == RESULT_OK && data != null) {
            eventLatitude = (double) data.getExtras().get("lat");
            eventLongitude = (double) data.getExtras().get("lon");
            eventLocationChip.setText("Location set");
        }



    }

    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }
    public String convertTimestampToTimeRange(long timestamp1, long timestamp2, boolean isAllDay) {
        timestamp1 = timestamp1 - 28800000;
        timestamp2 = timestamp2 - 28800000;
        LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp1), ZoneId.systemDefault());
        LocalDateTime dateTime2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp2), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedTime = formatter.format(dateTime1);
        String formattedTime2 = formatter.format(dateTime2);

        // Calculate the number of hours since the current time


        // Get the hour of the timestamp
        /*
        int timestampHour = dateTime.getHour();

        // Determine the time of day
        String timeOfDay = "Morning";
        if (timestampHour >= 12) {
            timeOfDay = "Afternoon";
        } else if (timestampHour >= 18) {
            timeOfDay = "Evening";
        } else if (timestampHour >= 21) {
            timeOfDay = "Night";
        }

        if (hoursSinceNow == 0) {
            return minutesSinceNow + "min, " + timeOfDay;
        } else {
            return hoursSinceNow + "hr, " + timeOfDay;
        }
        */
        if (isAllDay) {
            return "All Day";
        } else {
            return formattedTime + " - " + formattedTime2;
        }
    }

}
