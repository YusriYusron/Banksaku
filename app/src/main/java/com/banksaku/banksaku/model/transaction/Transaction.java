package com.banksaku.banksaku.model.transaction;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {
    private String id;
    private String category;
    private String note;
    private String time;
    private long price;
    private int image;
    private String uId;
    private String reminderDate;
    private String reminderTime;

    public Transaction() {
    }

    public Transaction(String id, String category, String note, String time, long price, int image, String uId) {
        this.id = id;
        this.category = category;
        this.note = note;
        this.time = time;
        this.price = price;
        this.image = image;
        this.uId = uId;
    }

    public Transaction(String id, String category, String note, String time, long price, int image, String uId, String reminderDate, String reminderTime) {
        this.id = id;
        this.category = category;
        this.note = note;
        this.time = time;
        this.price = price;
        this.image = image;
        this.uId = uId;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
    }

    protected Transaction(Parcel in) {
        id = in.readString();
        category = in.readString();
        note = in.readString();
        time = in.readString();
        price = in.readLong();
        image = in.readInt();
        uId = in.readString();
        reminderDate = in.readString();
        reminderTime = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public String getTime() {
        return time;
    }

    public long getPrice() {
        return price;
    }

    public int getImage() {
        return image;
    }

    public String getuId() {
        return uId;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(category);
        parcel.writeString(note);
        parcel.writeString(time);
        parcel.writeLong(price);
        parcel.writeInt(image);
        parcel.writeString(uId);
        parcel.writeString(reminderDate);
        parcel.writeString(reminderTime);
    }
}
