package com.mydemo.jnidemo.openGL;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;


/**
 * written by Oke Uwechue - 08/05/2019
 */
public class DemoGLSurfaceView extends GLSurfaceView
{
    DemoRenderer renderer;

    public DemoGLSurfaceView(Context context)
    {
        super(context);

        setEGLContextClientVersion(3);      // use OpenGL ES 3.x if available on this device

        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new DemoRenderer(context);
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public DemoGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setEGLContextClientVersion(3);      // use OpenGL ES 3.x if available on this device

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(new DemoRenderer(context));
    }

}
