package com.mydemo.jnidemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;
import com.mydemo.jnidemo.openGL.OpenGLFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * written by Oke Uwechue - 08/05/2019
 */
public class MainActivity extends AppCompatActivity
{
    // Load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private final int MAX_SIZE = 6;     // max #items in listvew
    private ListView listview;
    private /*List*/ArrayList<String> strings = new ArrayList<>();  // directly declare as subclass type instead of superclass type
                                                                    // as superclass is Serializable (easier for state restoration logic)

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String[] stringsFromJNI();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.strings);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                strings );

        listview.setAdapter(arrayAdapter);

        inject3DGraphics(this);
    }

    /**
     * Cache the current sentence list (in case of screen rotation)
     *
     * @param icicle
     */
    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        icicle.putStringArrayList("sentences", strings);
    }

    /**
     * Repopulate the UI with the cached sentence list
     *
     * @param icicle    the frozen data from cache
     */
    @Override
    protected void onRestoreInstanceState(Bundle icicle) {
        super.onRestoreInstanceState(icicle);
        if(icicle != null) {
            strings = icicle.getStringArrayList("sentences");

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    strings );

            listview.setAdapter(arrayAdapter);
        }
    }

    /**
     * initialize the OpenGL view
     *
     * @param ctx   the current activity context
     */
    private void inject3DGraphics(FragmentActivity ctx)
    {
        // Begin the transaction
        FragmentTransaction ft = ctx.getSupportFragmentManager().beginTransaction();

        // Replace the contents of the container with the new fragment
        ft.replace(R.id.openGLgraphics, new OpenGLFragment());

        // Complete the changes added above
        ft.commit();
    }

    /**
     * Fetch next random sentence and append it to current view.
     *
     * This method is directly invoked from a button onClick event
     *
     * @param v
     */
    public void fetchNextSentenceFromJNI(View v) {
        String str = Arrays.toString(stringsFromJNI());
        appendSentence(str.replaceAll("\\[|\\]|,", ""));    // filter out unnecessary delimiter chars from the sentence data
    }

    /**
     * Add new sentence to current view.
     * If view is already full, empty it out before appending new sentence
     *
     * @param sentence
     */
    private void appendSentence(String sentence) {

        if(strings.size() >= MAX_SIZE) {
            strings.clear();
        }

        strings.add(sentence);
        ((BaseAdapter)listview.getAdapter()).notifyDataSetChanged();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Do your thing
            Snackbar.make(getWindow().getDecorView().getRootView(),"Overriding AUDIO BUTTONS !", Snackbar.LENGTH_LONG).show();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
