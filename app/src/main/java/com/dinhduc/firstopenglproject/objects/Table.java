package com.dinhduc.firstopenglproject.objects;

import com.dinhduc.firstopenglproject.data.VertextArray;

import com.dinhduc.firstopenglproject.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.dinhduc.firstopenglproject.Constants.BYTE_PER_FLOAT;

/**
 * Created by Nguyen Dinh Duc on 9/7/2015.
 */
public class Table {
    public static final int POSITION_COMPONENT_COUNT = 2;
    public static final int TEXTURE_COORDINATE_COMPONENT_COUNT = 2;
    public static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATE_COMPONENT_COUNT) * BYTE_PER_FLOAT;
    public static final float[] VERTEX_DATA = {
            0.0f, 0.0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,
            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f
    };
    private final VertextArray vertextArray;

    public Table() {
        vertextArray = new VertextArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureShaderProgram) {
        vertextArray.setVertexAttriPointer(0, textureShaderProgram.getaPositionLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertextArray.setVertexAttriPointer(POSITION_COMPONENT_COUNT, textureShaderProgram.getaTextureCoordinatesLocation(), TEXTURE_COORDINATE_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
