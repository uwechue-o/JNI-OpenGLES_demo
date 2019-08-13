package com.mydemo.jnidemo.openGL;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

/**
 * written by Oke Uwechue - 08/05/2019
 */
public class OpenGLFragment extends Fragment
{
    private GLSurfaceView mGLView;

    public OpenGLFragment()
    {
        super();
    }

    /**
     * The onCreateView method is called when Fragment should create its View object hierarchy,
     * either dynamically or via XML layout inflation.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mGLView = new DemoGLSurfaceView(this.getActivity());
        return mGLView;
    }

    @Override
    public void onPause()
    {
        mGLView.onPause();      // pause rendering
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // exit the app if OpenGL3.x is not supported ...
        if(!detectOpenGLES_3_0()) {
            new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("! Unsupported Graphics!")
                    .setMessage("Sorry, This device does not support OpenGL 3.x")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }

                    })
                    .show();
        }
        else
            mGLView.onResume();     // resume rendering
    }

    private boolean detectOpenGLES_3_0() {

        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();

        return (info.reqGlEsVersion >= 0x30000);
    }
}
