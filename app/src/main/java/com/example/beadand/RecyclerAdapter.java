package com.example.beadand;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kotlin.reflect.KVisibility;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>
{
    Context context;
    List<Note> notes;

    public RecyclerAdapter(Context context, List<Note> noteList)
    {
        this.context = context;
        this.notes = noteList;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position)
    {
        Note note = notes.get(position);
        Calendar created = Calendar.getInstance();
        created.setTimeInMillis(note.id);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("MM.dd HH:mm", Locale.getDefault());

        holder.titleText.setText(note.title);
        holder.contentText.setText(note.content);
        holder.dateText.setText(dateFormat.format(created.getTime()));
        if(note.reminder != null)
        {
            holder.reminderText.setText(timeFormat.format(note.reminder.getTime()));
        }
        if(note.picture != null && note.picture != "")
        {
            Uri uri = Uri.parse(note.picture);
            Glide.with(context)
                    .load(uri)
                    .into(holder.imageView);
            holder.imageView.getLayoutParams().height = 500;
            holder.imageView.setVisibility(VISIBLE);
            holder.imageView.requestLayout();

            holder.imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent viewImage = new Intent( context, ViewImage.class);
                    viewImage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    viewImage.putExtra("note",note);
                    try {
                        context.startActivity(viewImage);
                    } catch (Exception e) {
                        Log.e("StartActivityError", "Error starting activity", e);
                    }
                }
            });
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu menu = new PopupMenu(context,v);
                menu.getMenu().add("DELETE");
                menu.getMenu().add("EDIT");
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        if(item.getTitle().equals("DELETE"))
                        {
                            //delete the note
                            DataHandler dataHandler = new DataHandler(context);
                            notes.remove(holder.getAdapterPosition());
                            dataHandler.deleteNote(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            Toast.makeText(context,"Note deleted",Toast.LENGTH_SHORT).show();
                        }
                         else if (item.getTitle().equals("EDIT"))
                        {
                            Intent editNote = new Intent( context, NewNoteActivity.class);
                            editNote.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            editNote.putExtra("note",notes.get(holder.getAdapterPosition()));
                            editNote.putExtra("position",holder.getAdapterPosition());
                            try {
                                context.startActivity(editNote);
                            } catch (Exception e) {
                                Log.e("StartActivityError", "Error starting activity", e);
                            }

                        }
                        return true;
                    }
                });
                menu.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        if(notes != null)
        {
            return notes.size();
        }
        return 0;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView titleText;
        TextView contentText;
        TextView dateText;
        TextView reminderText;
        ImageView imageView;

        public RecyclerViewHolder(@NonNull View itemView)
        {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            contentText = itemView.findViewById(R.id.contentText);
            dateText = itemView.findViewById(R.id.dateText);
            reminderText = itemView.findViewById(R.id.reminderText);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
