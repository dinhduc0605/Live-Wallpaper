package com.particles.android.objects;

import android.graphics.Color;
import android.opengl.GLES20;

import com.particles.android.Constants;
import com.particles.android.data.VertextArray;
import com.particles.android.programs.ParticleShaderProgram;
import com.particles.android.util.Geometry;
import com.particles.android.util.Geometry.Point;
import com.particles.android.util.Geometry.Vector;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by Nguyen Dinh Duc on 9/18/2015.
 */
public class ParticleSystem {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;

    private static final int TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT + VECTOR_COMPONENT_COUNT + PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTE_PER_FLOAT;

    private final float[] particles;
    private final VertextArray vertextArray;
    private final int maxParticleCount;

    private int currentParticleCount;
    private int nextParticle;

    public ParticleSystem(int maxParticleCount) {
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        vertextArray = new VertextArray(particles);
        this.maxParticleCount = maxParticleCount;
    }

    public void addParticle(Point position, int color, Vector direction, float particleStartTime) {
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;
        int currentOffset = particleOffset;
        nextParticle++;

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++;
        }

        if (nextParticle == maxParticleCount) {
            nextParticle = 0;
        }
        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;

        particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;

        particles[currentOffset++] = direction.x;
        particles[currentOffset++] = direction.y;
        particles[currentOffset++] = direction.z;

        particles[currentOffset++] = particleStartTime;

        vertextArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(ParticleShaderProgram particleShaderProgram) {
        int dataOffset = 0;
        vertextArray.setVertexAttriPointer(dataOffset, particleShaderProgram.getaPositionLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;

        vertextArray.setVertexAttriPointer(dataOffset, particleShaderProgram.getaColorLocation(), COLOR_COMPONENT_COUNT, STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;

        vertextArray.setVertexAttriPointer(dataOffset, particleShaderProgram.getaDirectionVectorLocation(), VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;

        vertextArray.setVertexAttriPointer(dataOffset, particleShaderProgram.getaParticleStartTimeLocation(), PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0, currentParticleCount);
    }
}
