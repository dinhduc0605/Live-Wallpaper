package com.particles.android.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.particles.android.R;

import static android.opengl.GLES20.*;

/**
 * Created by Nguyen Dinh Duc on 9/18/2015.
 */
public class ParticleShaderProgram extends ShaderProgram {
    private final int uTextureUnitLocation;
    private final int uMatrixLocation;
    private final int uTimeLocation;

    private final int aPositionLocation;
    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;

    public ParticleShaderProgram(Context context) {
        super(context, R.raw.particle_vertex_shader, R.raw.particle_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTimeLocation = glGetUniformLocation(program, U_TIME);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = glGetAttribLocation(program, A_PARTICLE_START_TIME);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
    }

    public void setUniforms(float[] matrix, float elapsedTime, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getaPositionLocation() {
        return aPositionLocation;
    }

    public int getaColorLocation() {
        return aColorLocation;
    }

    public int getaDirectionVectorLocation() {
        return aDirectionVectorLocation;
    }

    public int getaParticleStartTimeLocation() {
        return aParticleStartTimeLocation;
    }
}
