package com.example.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    // numeric code to identify the edit activity
    public final static int EDIT_REQUEST_CODE = 20;

    // keys used for passing data between activities
    public final static String ITEM_TEXT = "itemText";
    public final static String ITEM_POSITION = "itemPosition";


    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // read items that already exist in memory
        readItems();

        // define lists of items to display
        itemsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        lvItems = (ListView) findViewById(R.id.lvItems);

        lvItems.setAdapter((itemsAdapter));


        // listen for different gestures on list
        setupViewListener();



    }

    // get value from text box and add to list structures
    public void onAddItem(View v)
    {


        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);

        // collect text
        String itemText = etNewItem.getText().toString();

        // add to list
        itemsAdapter.add(itemText);

        // clear text box
        etNewItem.setText("");

        // write vals in memory
        writeItems();

        // notify that item was added correctly
        Toast.makeText(getApplicationContext(), "Item Added to List", Toast.LENGTH_SHORT).show();


    }


    private void setupViewListener()
    {

        Log.d("MainActivity","Setting up listener");

        // listens for Long click on list item
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("MainActivity","Removing item at position: "+ i);

                // remove item that was tapped on
                items.remove(i);
                // update items adapter bc there was a change
                itemsAdapter.notifyDataSetChanged();

                // write vals in memory
                writeItems();

                //notify the user the operation completed ok
                Toast.makeText(MainActivity.this,"Item removed",Toast.LENGTH_SHORT).show();




                return true;
            }
        });


        // set up listener for edit (regular click)

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // create new activity

                Intent intent = new Intent(MainActivity.this,EditItemActivity.class);

                // pass the data being edited

                intent.putExtra(ITEM_TEXT,items.get(i));
                intent.putExtra(ITEM_POSITION,i);


                // display the activity

                startActivityForResult(intent,EDIT_REQUEST_CODE);



            }
        });


    }

    // handle the results from edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the edit activity completed is ok
        if(resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE)
        {
            // extract the updated item text from result intent extras
            String updatedItem = data.getExtras().getString(ITEM_TEXT);

            // extract original position of edited text
            int position = data.getExtras().getInt(ITEM_POSITION);

            // update the model with the new item text at the edited position
            items.set(position,updatedItem);
            // notify the adaptor that the data has changed
            itemsAdapter.notifyDataSetChanged();
            // persist the changed model
            writeItems();
            //notify the user the operation completed ok
            Toast.makeText(this,"Item updated succesfully",Toast.LENGTH_SHORT).show();




        }

    }

    private File getDataFile()
    {
        // get all values from todo.txt that stores past values
        return new File(getFilesDir(),"todo.txt");

    }

    private void readItems(){


        // try to read the values from the file if not initialize it to empty list
        try {
            items = new ArrayList<>(org.apache.commons.io.FileUtils.readLines(getDataFile(),Charset.defaultCharset()));
        } catch (IOException e) {
            Log.d("MainActivity","Error reading file",e);
            items = new ArrayList<>();
        }



    }

    private void writeItems()
    {
        // write all list items into memory
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.d("MainActivity","Error writing file",e);

        }

    }

}
