package com.dinhduc.firstopenglproject.programs;

import android.content.Context;

import com.dinhduc.firstopenglproject.R;

import static android.opengl.GLES20.*;

/**
 * Created by Nguyen Dinh Duc on 9/8/2015.
 */
public class ColorShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int aPositionLocation;
    private final int uColorLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
    }

    public void setUniform(float[] matrix, float r, float g, float b) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getaPositionLocation() {
        return aPositionLocation;
    }

}
