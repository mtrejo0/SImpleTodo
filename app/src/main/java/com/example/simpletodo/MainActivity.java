package com.example.simpletodo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        readItems();
        itemsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        lvItems = (ListView) findViewById(R.id.lvItems);

        lvItems.setAdapter((itemsAdapter));

//        MOCK DATA
//        items.add("Moises");
//        items.add("Trejo");

        setupViewListener();



    }


    public void onAddItem(View v)
    {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);

        String itemText = etNewItem.getText().toString();

        itemsAdapter.add(itemText);

        etNewItem.setText("");
        writeItems();

        Toast.makeText(getApplicationContext(), "Item Added to List", Toast.LENGTH_SHORT).show();


    }


    private void setupViewListener()
    {
        Log.d("MainActivity","Setting up listener");
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("MainActivity","Removing item at position: "+ i);
                items.remove(i);
                itemsAdapter.notifyDataSetChanged();
                writeItems();




                return true;
            }
        });
    }


    private File getDataFile()
    {
        return new File(getFilesDir(),"todo.txt");

    }

    private void readItems(){


        try {
            items = new ArrayList<>(org.apache.commons.io.FileUtils.readLines(getDataFile(),Charset.defaultCharset()));
        } catch (IOException e) {
            Log.d("MainActivity","Error reading file",e);
            items = new ArrayList<>();
        }



    }

    private void writeItems()
    {
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.d("MainActivity","Error writing file",e);

        }

    }

}
