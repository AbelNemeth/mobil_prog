package com.example.beadand;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

public class Note implements Parcelable
{
    long id;
    String title;
    String content;
    String category;
    Calendar reminder;
    String picture;

    public Note(long id, String title, String content, String category, Calendar reminder, String picture)
    {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.reminder = reminder;
        this.picture = picture;
    }

    public Note(long id, String title, String content)
    {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(category);
        dest.writeSerializable(reminder);
        dest.writeString(picture);
    }
    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
    protected Note(Parcel in) {
        id = in.readLong();
        title = in.readString();
        content = in.readString();
        category = in.readString();
        reminder = (Calendar) in.readSerializable();
        picture = in.readString();
    }
}

