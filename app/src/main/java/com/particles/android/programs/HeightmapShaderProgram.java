package com.particles.android.programs;

import android.content.Context;

import com.particles.android.R;

import static android.opengl.GLES20.*;

/**
 * Created by Nguyen Dinh Duc on 9/25/2015.
 */
public class HeightmapShaderProgram extends ShaderProgram {
    private final int uMVMatrixLocation;
    private final int uIT_MVMatrixLocation;
    private final int uMVPMatrixLocation;
    private final int uPointLightPositionsLocation;
    private final int uPointLightColorsLocation;

    private final int aPositionLocation;
    private final int uVectorToLightLocation;
    private final int aNormalLocation;

    public HeightmapShaderProgram(Context context) {
        super(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uVectorToLightLocation = glGetUniformLocation(program, U_VECTOR_TO_LIGHT);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);

        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uIT_MVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
        uPointLightPositionsLocation = glGetUniformLocation(program, U_POINT_LIGHT_POSITIONS);
        uPointLightColorsLocation = glGetUniformLocation(program, U_POINT_LIGHT_COLORS);
    }

    public void setUniforms(float[] mvMatrix,
                            float[] it_mvMatrix,
                            float[] mvpMatrix,
                            float[] vectorToDirectionalLight,
                            float[] pointLightPositions,
                            float[] pointLightColors) {
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);

        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0);
        glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0);
        glUniform3fv(uPointLightColorsLocation, 3, pointLightColors, 0);
    }

    public int getaPositionLocation() {
        return aPositionLocation;
    }

    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }
}
