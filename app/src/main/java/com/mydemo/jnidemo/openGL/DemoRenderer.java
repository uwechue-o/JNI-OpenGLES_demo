package com.mydemo.jnidemo.openGL;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * open src codebase modified by Oke Uwechue - 08/05/2019
 */
public class DemoRenderer implements GLSurfaceView.Renderer {

    public enum SpinType
    {
        Y_SPIN,
        X_SPIN,
        Z_SPIN,
        TUMBLE
    }

    private SpinType spinChoice = SpinType.Z_SPIN;

    private int mWidth;
    private int mHeight;

    private Triangle mTriangle;
    private float mAngle =0;
    private float mTransY=0;
    private float mTransX=0;
    private static final float Z_NEAR = 1f;
    private static final float Z_FAR = 10f;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private WeakReference<Context> context_weakRef;

    public DemoRenderer(Context context) {
        context_weakRef = new WeakReference<>(context);
    }

    /**
     * Called only when the surface is initially created or is recreated.
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES30.glClearColor(0.04f, 0.04f, 0.25f, 1.0f);     // deep blue background

        // initialize the triangle code for drawing
        mTriangle = new Triangle(context_weakRef);
    }

    /**
     * Called by OpenGL to draw the current frame.
     */
    @Override
    public void onDrawFrame(GL10 glUnused) {

        // Clear the color buffer  set above by glClearColor.
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        //need this otherwise, it will over right stuff and the cube will look wrong!
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        // Set the camera position (View matrix)  note Matrix is an include, not a declared method.
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Create a rotation and translation for the cube
        Matrix.setIdentityM(mRotationMatrix, 0);

        //move the cube up/down and left/right
        Matrix.translateM(mRotationMatrix, 0, mTransX, mTransY, 0);

        //mAngle is how fast, x,y,z which directions it rotates.
        switch(spinChoice) {
            case TUMBLE:
                Matrix.rotateM(mRotationMatrix, 0, mAngle, 1.5f, -0.85f, 0.9f);      // spin object about all 3 axes simultaneously
                mAngle += 0.7;
                break;
            case Y_SPIN:
                Matrix.rotateM(mRotationMatrix, 0, mAngle, 0.0f, 1.0f, 0.0f);       // spin object about the Y-axis only
                break;
            case X_SPIN:
                Matrix.rotateM(mRotationMatrix, 0, mAngle, 1.0f, 0.0f, 0.0f);       // spin object about the X-axis only
                break;
            case Z_SPIN:
                Matrix.rotateM(mRotationMatrix, 0, mAngle, 0.0f, 0.0f, 1.0f);       // spin object about the Z-axis only
        }

        // combine the model with the view matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mRotationMatrix, 0);

        // combine the model-view with the projection matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        mTriangle.draw(mMVPMatrix);

        //change the angle, so the cube will spin.
        mAngle += .8;

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mWidth = width;
        mHeight = height;
        // Set the viewport
        GLES30.glViewport(0, 0, mWidth, mHeight);
        float aspect = (float) width / height;

        // this projection matrix is applied to object coordinates
        //no idea why 53.13f, it was used in another example and it worked.
        Matrix.perspectiveM(mProjectionMatrix, 0, 50.00f, aspect, Z_NEAR, Z_FAR);
    }

    ///
    // Create a shader object, load the shader source, and
    // compile the shader.
    //
    public static int loadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES30.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        GLES30.glShaderSource(shader, shaderSrc);

        // Compile the shader
        GLES30.glCompileShader(shader);

        // Check the compile status
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e("DemoRenderer", "Erorr!!!!");
            Log.e("DemoRenderer", GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }


    /**
     * Utility method for debugging OpenGL calls.
     * Provide the name of the call just after making it:
     *
     * <pre>
     * mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e("DemoRenderer", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

}
