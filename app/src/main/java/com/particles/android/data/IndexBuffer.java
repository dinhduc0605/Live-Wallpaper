package com.particles.android.data;

import com.particles.android.Constants;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;

/**
 * Created by Nguyen Dinh Duc on 9/23/2015.
 */
public class IndexBuffer {
    private final int bufferId;

    public IndexBuffer(short[] vertexData) {
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new vertex buffer object");
        }
        bufferId = buffers[0];
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        ShortBuffer vertexArray = ByteBuffer
                .allocateDirect(vertexData.length * Constants.BYTE_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(vertexData);
        vertexArray.position(0);

        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * Constants.BYTE_PER_SHORT, vertexArray, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int getBufferId() {
        return bufferId;
    }
}
