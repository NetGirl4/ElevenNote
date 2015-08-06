package com.elevenfifty.www.elevennote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;


public class NotesActivity extends ActionBarActivity {
    public static final String NOTE_INDEX = "com.elevenfifty.www.elevennote.NOTE_INDEX";
    public static final String NOTE_TITLE = "com.elevenfifty.www.elevennote.NOTE_TITLE";
    public static final String NOTE_TEXT = "com.elevenfifty.www.elevennote.NOTE_TEXT";

    private ListView notesList;
//    private final String[] notes = new String[] {"note 1", "note 2", "note 3"};
    private ArrayList<Note> notesArray;
    private NotesArrayAdapter notesArrayAdapter;
    // ??

    private SharedPreferences notesPrefs;
    //private ListView notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        notesList = (ListView)findViewById(R.id.listView);
        //remove the comment on the line below
//        notesList.setAdapter(new ArrayAdapter<>(this, R.layout.notes_textview_list_item, notes));

//        command click on a function, it will take you to the function

        notesPrefs = getPreferences(Context.MODE_PRIVATE);
        //notesList = (ListVisew)findViewById(R.id.List_view);

        setupNotes();

        Collections.sort(notesArray);
        notesArrayAdapter = new NotesArrayAdapter(this, R.layout.notes_list_item, notesArray);
        notesList.setAdapter(notesArrayAdapter);

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = notesArray.get(position);
                Intent intent = new Intent(NotesActivity.this, NoteDetailActivity.class);

                intent.putExtra(NOTE_INDEX, position);
                intent.putExtra(NOTE_TITLE, note.getTitle());
                intent.putExtra(NOTE_TEXT, note.getText());

//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NotesActivity.this);
                alertBuilder.setTitle(getString(R.string.delete));
                alertBuilder.setMessage(getString(R.string.delete_confirm_text));
                alertBuilder.setNegativeButton(getString(R.string.cancel), null);
                alertBuilder.setPositiveButton(getString(R.string.delete),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Note note = notesArray.get(position);
                        //Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
                        deleteFile(note.getTitle());
                        notesArray.remove(position);
                        notesArrayAdapter.updateAdapter(notesArray); //needs to be sorted
                    }
                });
                alertBuilder.create().show();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int index = data.getIntExtra(NOTE_INDEX, -1);
            Note note = new Note(data.getStringExtra(NOTE_TITLE),
                                data.getStringExtra(NOTE_TEXT),
                                new Date(), UUID.randomUUID().toString());
            if (index < 0 || index > notesArray.size() - 1) {
                notesArray.add(note);
                writeFile(note);
            } else {
                Note oldNote = notesArray.get(index);
                note.setKey(oldNote.getKey());
                notesArray.set(index,note);
                notesArray.set(index, note);
                    if (!oldNote.getTitle().equals(note.getTitle())) {
                    File oldFile = new File(this.getFilesDir(), oldNote.getTitle());
                    File newFile = new File(this.getFilesDir(), note.getTitle());
                    oldFile.renameTo(newFile);
                }
                writeFile(note);
            }
            Collections.sort(notesArray);
            notesArrayAdapter.updateAdapter(notesArray); //sorts by comparing date & time on update
        }
    }

    private void setupNotes() {
        notesArray = new ArrayList<>();
        if (notesPrefs.getBoolean("firstRun", true)) {
            SharedPreferences.Editor editor = notesPrefs.edit();
            editor.putBoolean("firstRun", false); // will run once, after will be editable
            editor.apply();

            Note note1 = new Note("Note 1", "This is a note", new Date(), UUID.randomUUID().toString());
            notesArray.add(note1);
            notesArray.add(new Note("Note 2", "This is another note", new Date(), UUID.randomUUID().toString()));
            notesArray.add(new Note("Note 3", "This is another note", new Date(), UUID.randomUUID().toString()));

            for (Note note : notesArray) {
                writeFile(note);
            }
        } else {
            File[] filesDir = this.getFilesDir().listFiles();
            Gson gson = new Gson();
            for (File file : filesDir) {
                FileInputStream inputStream = null;
                String title = file.getName();
                Date date = new Date(file.lastModified());
                String text = "";
                try {
                    inputStream = openFileInput(title);
                    byte[] input = new byte[inputStream.available()];
                    while (inputStream.read(input) != -1) {}
                    text += new String(input);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (Exception e) { //Exception ignored
//alt/enter to suppress error, even it its warnetted
                    }
                }
                Note note = gson.fromJson(text, Note.class);
                note.setDate(date);
                notesArray.add(note);
            }
        }
    }

    private void writeFile(Note note) {
        FileOutputStream outputStream = null;

        try {
            outputStream = openFileOutput(note.getKey(), Context.MODE_PRIVATE);
            Gson gson = new Gson();
            outputStream.write(gson.toJson(note).getBytes());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException ioe) {

            } catch (NullPointerException npe) {

            } catch (Exception e) {

            }
        }
    }


    //is this where the gson code goes?  where else should it go, and what else should i be dealing wih
    //hurry and give us your solution please.  i don't know what the answer is


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(NotesActivity.this, NoteDetailActivity.class);

            intent.putExtra(NOTE_TITLE, "");
            intent.putExtra(NOTE_TEXT, "");

            startActivityForResult(intent, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
