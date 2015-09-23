package com.particles.android.objects;

import android.opengl.GLES20;

import com.particles.android.data.VertextArray;
import com.particles.android.programs.SkyboxShaderProgram;

import java.nio.ByteBuffer;

/**
 * Created by Nguyen Dinh Duc on 9/23/2015.
 */
public class Skybox {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private final VertextArray vertextArray;
    private final ByteBuffer indexArray;

    public Skybox() {
        vertextArray = new VertextArray(new float[]{
                -1, 1, 1,
                1, 1, 1,
                -1, -1, 1,
                1, -1, 1,
                -1, 1, -1,
                1, 1, -1,
                -1, -1, -1,
                1, -1, -1,
        });
        indexArray = ByteBuffer.allocateDirect(6 * 6).put(new byte[]{
                //front
                1, 3, 0,
                0, 3, 2,

                //back
                4, 6, 5,
                5, 6, 7,

                //left
                0, 2, 4,
                4, 2, 6,

                //right
                5, 7, 1,
                1, 7, 3,

                //top
                5, 1, 4,
                4, 1, 0,

                //bottom
                6, 2, 7,
                7, 2, 3,
        });
        indexArray.position(0);
    }

    public void bindData(SkyboxShaderProgram skyboxShaderProgram) {
        vertextArray.setVertexAttriPointer(0, skyboxShaderProgram.getaPositionLocation(), POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, indexArray);
    }
}
