package com.example.beadand;

import android.content.Context;
import android.provider.ContactsContract;

import androidx.core.app.ActivityCompat;

import com.example.beadand.Note;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;



import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;


public class DataHandler {
    private Context context;
    private static final String FILENAME = "notes.json";
    private String filePath;

    //File file = context.getFilesDir() + FILENAME;
    DataHandler(Context context)
    {
      this.context = context;
      filePath = context.getFilesDir() + "/" + FILENAME;

    }

    // Function to save a Note object to JSON file
    public void saveNoteToFile(Note note)
    {
        List<Note> notes = loadNotesFromFile();

        if (notes == null) {
            notes = new ArrayList<>();
        }

        notes.add(note);
        save(notes);
    }
    public void deleteNote(int position)
    {
        //this is the default that NewNoteActiviry gives if it is a new not and not an edit
        if(position == -1)
        {
            return;
        }
        List<Note> notes = loadNotesFromFile();

        if (notes == null) {
            notes = new ArrayList<>();
        }
        else
        {
            notes.remove(position);
            save(notes);
        }

    }
    private void save(List<Note> notes) {

        FileOutputStream writer = null;

        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(notes);
            writer = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            String output = gson.toJson(notes);
            writer.write(output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print(e.getMessage());
        }
        finally
        {
            try
            {
                writer.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    public List<Note> loadNotesFromFile()
    {
        List<Note> notes = null;
        File file = new File(context.getFilesDir(), FILENAME);
        FileInputStream reader = null;

        if (file.exists())
        {
            try{
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Note>>() {}.getType();
                reader = context.openFileInput(FILENAME);

                InputStreamReader inputStreamReader = new InputStreamReader(reader);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String input = stringBuilder.toString();

                notes = gson.fromJson(input, listType);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.print(e.getMessage());
            }
            finally
            {
                try
                {
                    reader.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        if(notes != null)
        {
            Collections.sort(notes, new Comparator<Note>()
            {
                @Override
                public int compare(Note o1, Note o2)
                {
                    return Long.compare(o2.id, o1.id);
                }
            });
        }
        return notes;
    }

}