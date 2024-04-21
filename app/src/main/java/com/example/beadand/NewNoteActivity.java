package com.example.beadand;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Calendar;



public class NewNoteActivity extends AppCompatActivity
{
    private TextView title;
    private TextView content;
    private Button saveButton;
    private Button reminderPicker;
    private Button imagePicker;
    private ImageView imageView;
    private String picture;
    private Calendar calendar;
    private int ogPosition = -1;
    Note note;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        saveButton = findViewById(R.id.saveButton);



        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        Intent intent = getIntent();
        if (intent.hasExtra("note") && intent.hasExtra("position"))
        {
            note = intent.getParcelableExtra("note");
            int position = intent.getIntExtra("position", 0);

            title.setText(note.title);
            content.setText(note.content);
            if(note.reminder != null)
            {
                calendar = note.reminder;
            }
            if(note.picture != null)
            {
                imageView = findViewById(R.id.imageView);
                Uri uri = Uri.parse(note.picture);
                Glide.with(this)
                        .load(uri)
                        .into(imageView);
                imageView.requestLayout();
            }
            //remove original version
//            DataHandler dataHandler = new DataHandler(this);
//            dataHandler.deleteNote(position);
            ogPosition = position;
        }

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                title = findViewById(R.id.title);
                content = findViewById(R.id.content);
                //save
                if(!title.getText().toString().isEmpty())
                {
                    if(note == null)
                    {
                        Note note = new Note(System.currentTimeMillis(), title.getText().toString(), content.getText().toString());
                    }
                    else if(note.title == null)
                    {
                        Note note = new Note(System.currentTimeMillis(), title.getText().toString(), content.getText().toString());
                    }
                    if (calendar != null && calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
                    {
                        note.reminder = calendar;
                        createNotification(note);
                    }
                    if(picture != null && picture != "")
                    {
                        note.picture = picture;
                    }

                    DataHandler dataHandler = new DataHandler(getApplicationContext());
                    //remove original
                    dataHandler.deleteNote(ogPosition);
                    //add new
                    dataHandler.saveNoteToFile(note);
                    //go back to main
                    startActivity(new Intent(NewNoteActivity.this, MainActivity.class));
                }
                else
                {
                    Toast.makeText(NewNoteActivity.this, "You are missing a title",Toast.LENGTH_LONG).show();
                }

            }
        });

        reminderPicker = findViewById(R.id.reminderPicker);
        reminderPicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calendar = Calendar.getInstance();
                showDatePickerDialog();

            }
        });

        imagePicker = findViewById(R.id.imagePicker);
        imagePicker.setOnClickListener(v -> openImagePicker());

    }

    private void showDatePickerDialog()
    {
        try
        {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth) ->
            {
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);
                //Toast.makeText(NewNoteActivity.this, "Selected Date: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
                showTimePickerDialog();
            }, year, month, day);
            datePickerDialog.show();
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.e("Show datePicker error", "Error showing datepicker", e);
            Toast.makeText(this, "Error occurred while showing date picker dialog", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimePickerDialog()
    {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) ->
        {
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);

            //Toast.makeText(NewNoteActivity.this, "Selected Time: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void createNotification(Note note)
    {
        try
        {
            Intent notificationIntent = new Intent(getApplicationContext(), Receiver.class);
            notificationIntent.putExtra("note", note);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int)note.id, notificationIntent, PendingIntent.FLAG_MUTABLE);

            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

            long futureInMillis = note.reminder.getTimeInMillis();

            alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis-30000, pendingIntent);
        }catch(Exception e)
        {
            Log.e("Show datePicker error", "Error showing datepicker", e);
            Toast.makeText(this, "Error occurred while showing date picker dialog" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerActivityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> imagePickerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        // Get the URI of the selected image
                        Uri selectedImageUri = data.getData();

                        // Use the selectedImageUri to load or display the image
                        // For example, you can use Glide to load the image into an ImageView:
                        ImageView imageView = findViewById(R.id.imageView);
                        Glide.with(NewNoteActivity.this)
                                .load(selectedImageUri)
                                .into(imageView);
                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        imageView.setLayoutParams(layoutParams);
                        picture = data.getData().toString();
                    }
                }
            });
}

