package com.elevenfifty.www.elevennote;

import java.util.Date;

/**
 * Created by bkeck on 3/9/15.
 */
public class Note implements Comparable<Note> {
    private String title;
    private String text;
    private Date date;
    private String key;

    public Note(String title, String text, Date date, String key) { //last problem
        this.title = title;
        this.text = text;
        this.date = date;
        this.key = key; //last problem
    }

    public String getKey() {  // last problem
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    //use control "i" to get this override
    @Override
    public int compareTo(Note another) {
//        return getDate().compareTo(another.getDate()); PUTS NEW NOTES AT THE BOTTOM OF THE LIST
        return another.getDate().compareTo(getDate());
        // RETURN ANOTHER.GETdATE
    }
}
