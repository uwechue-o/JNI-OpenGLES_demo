/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mydemo.jnidemo.openGL;

import android.content.Context;
import android.opengl.GLES30;

import com.mydemo.jnidemo.R;
import com.mydemo.jnidemo.openGL.helpers.RawResourceReader;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Triangle object from AOSP codebase with some amendments and additional features
 *
 * modification by Oke Uwechue - 08/05/2019
 */
public class Triangle {

    private final FloatBuffer vertexBuffer;
    private FloatBuffer vertexColorBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;           // handles fragment colour
    private int mVertexColorHandle;     // handles vertex colour
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            // in counterclockwise order:
            0.0f,  0.88f, 0.0f,   // top
           -0.6f, -0.65f, 0.0f,   // bottom left
            0.6f, -0.65f, 0.0f    // bottom right
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.85f, 0.39f, 0.1f, 0.0f };
    float vertexColors[] = {
                                0.9f, 0.1f, 0.1f, 0.0f,     // a red vertex
                                0.1f, 0.9f, 0.1f, 0.0f,     // a green vertex
                                0.1f, 0.1f, 0.9f, 0.0f      // a blue vertex
                                };

    private WeakReference<Context> context_weakRef;

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Triangle(WeakReference<Context> weak_ctx)
    {
        context_weakRef = weak_ctx;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        final String vertexShaderCode = getVertexShader();
        final String fragmentShaderCode = getFragmentShader();

        // prepare shaders and OpenGL program
        int vertexShader = DemoRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = DemoRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES30.glCreateProgram();             // create empty OpenGL Program
        GLES30.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program

        /**
         * Bind my attributes.
         * No need to explicitly do this, as the very first call to "GLES30.glGetAttribLocation(...)"
         * will auto-generate and assign an index to the declared attribute
         */
        //   GLES30.glBindAttribLocation(mProgram, 0, "aColor");

        GLES30.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES30.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        mVertexColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, vertexStride, vertexBuffer);

        // set up vertex colors...
        GLES30.glEnableVertexAttribArray(mVertexColorHandle);
        // ...float has 4 bytes, colors (RGBA) * 4 bytes
        ByteBuffer cbb = ByteBuffer.allocateDirect(vertexColors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        vertexColorBuffer = cbb.asFloatBuffer();
        vertexColorBuffer.put(vertexColors);
        vertexColorBuffer.position(0);
        // ... set up the data for the vertex shader
        GLES30.glVertexAttribPointer(mVertexColorHandle, 4, GLES30.GL_FLOAT, false, 0, vertexColorBuffer);


        // get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES30.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        DemoRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        DemoRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mVertexColorHandle);
    }

    /**
     * Fetch shaders from external files.
     * Maintaining shader code in external files aids readability.
     *
     * @return
     */
    private String getVertexShader()
    {
        if(context_weakRef.get() != null)
            return fetchSanitizedCode(R.raw.vertex_shader);

        return "";
    }

    private String getFragmentShader()
    {
        if(context_weakRef.get() != null)
            return fetchSanitizedCode(R.raw.fragment_shader);

        return "";
    }

    /**
     * Deletes special chars and whitespace control chars from data
     *
     * @param fileID
     * @return
     */
    private String fetchSanitizedCode(int fileID) {

        // fetch the string and filter out control chars
        String code = RawResourceReader.readTextFileFromRawResource(context_weakRef.get(), fileID).replaceAll("\\r|\\n|\\f|\\t", "");

        // now compress multiple whitespace with single whitespace
        code = code.trim().replaceAll("\\s+", " ");

        return code;
    }

}
