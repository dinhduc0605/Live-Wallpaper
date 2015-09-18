package com.dinhduc.firstopenglproject.programs;

import android.content.Context;

import com.dinhduc.firstopenglproject.R;

import static android.opengl.GLES20.*;

/**
 * Created by Nguyen Dinh Duc on 9/8/2015.
 */
public class TextureShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int uTextureUnintLocation;
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnintLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniForms(float[] matrix, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnintLocation, 0);

    }

    public int getaPositionLocation() {
        return aPositionLocation;
    }

    public int getaTextureCoordinatesLocation() {
        return aTextureCoordinatesLocation;
    }
}
