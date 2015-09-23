package com.particles.android;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.particles.android.objects.ParticleShooter;
import com.particles.android.objects.ParticleSystem;
import com.particles.android.objects.Skybox;
import com.particles.android.programs.ParticleShaderProgram;
import com.particles.android.programs.SkyboxShaderProgram;
import com.particles.android.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static com.particles.android.util.Geometry.*;

/**
 * Created by Nguyen Dinh Duc on 8/23/2015.
 */
public class ParticlesRenderer implements Renderer {


    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    private ParticleShaderProgram particleShaderProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    private long globalStartTime;
    private int particleTexture;

    private SkyboxShaderProgram skyboxShaderProgram;
    private Skybox skybox;
    private int skyboxTexture;

    public ParticlesRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0, 0, 0, 0);
        particleShaderProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();

        final float angleVarianceInDegrees = 5f;
        final float speedVariance = 1f;

        final Vector particleDirection = new Vector(0f, 0.5f, 0f);

        redParticleShooter = new ParticleShooter(new Point(-1f, 0f, 0f), particleDirection, Color.rgb(255, 50, 5), angleVarianceInDegrees, speedVariance);

        greenParticleShooter = new ParticleShooter(new Point(0f, 0f, 0f), particleDirection, Color.rgb(25, 255, 25), angleVarianceInDegrees, speedVariance);

        blueParticleShooter = new ParticleShooter(new Point(1f, 0f, 0f), particleDirection, Color.rgb(5, 50, 255), angleVarianceInDegrees, speedVariance);

        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle);

        skyboxShaderProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox();
        skyboxTexture = TextureHelper.loadCubeMap(context, new int[]{R.drawable.left, R.drawable.right, R.drawable.bottom, R.drawable.top, R.drawable.front, R.drawable.back});
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        Matrix.perspectiveM(projectionMatrix, 0, 90, (float) width / (float) height, 1f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        drawSkybox();
        drawParticles();


    }

    private void drawSkybox() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        skyboxShaderProgram.useProgram();
        skyboxShaderProgram.setUniforms(viewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxShaderProgram);
        skybox.draw();
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        translateM(viewMatrix, 0, 0f, -1.5f, -2f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particleShaderProgram.useProgram();
        particleShaderProgram.setUniforms(viewProjectionMatrix, currentTime, particleTexture);
        particleSystem.bindData(particleShaderProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
    }

    private float xRotation, yRotation;

    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 15f;
        yRotation += deltaY / 15f;
        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }
    }
}
