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
