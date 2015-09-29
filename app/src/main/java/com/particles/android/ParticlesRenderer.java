package com.particles.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.particles.android.objects.Heightmap;
import com.particles.android.objects.ParticleShooter;
import com.particles.android.objects.ParticleSystem;
import com.particles.android.objects.Skybox;
import com.particles.android.programs.HeightmapShaderProgram;
import com.particles.android.programs.ParticleShaderProgram;
import com.particles.android.programs.SkyboxShaderProgram;
import com.particles.android.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.transposeM;
import static com.particles.android.util.Geometry.Point;
import static com.particles.android.util.Geometry.Vector;

/**
 * Created by Nguyen Dinh Duc on 8/23/2015.
 */
public class ParticlesRenderer implements Renderer {


    private final Context context;
    private final float[] modelMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] viewMatrixForSkybox = new float[16];

    private final float[] tempMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private HeightmapShaderProgram heightmapShaderProgram;
    private Heightmap heightmap;

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
    private final float[] vectorToLight = {0.5f, 0.2f, -0.86f, 0f};
    private final float[] pointLightPositions = new float[]{
            -1f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            1f, 1f, 0f, 1f
    };
    private final float[] pointLightColors = new float[]{
            1.00f, 0.20f, 0.02f,
            0.02f, 0.25f, 0.02f,
            0.02f, 0.20f, 2.00f
    };
    private final float[] modelViewMatrix = new float[16];
    private final float[] it_modelViewMatrix = new float[16];

    public ParticlesRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0, 0, 0, 0);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
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

        heightmapShaderProgram = new HeightmapShaderProgram(context);
        heightmap = new Heightmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.heightmap)).getBitmap());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        Matrix.perspectiveM(projectionMatrix, 0, 90, (float) width / (float) height, 1f, 100f);
        updateViewMatrices();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawHeightmap();
        drawSkybox();
        drawParticles();
    }

    private void drawHeightmap() {
        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();
        heightmapShaderProgram.useProgram();
        final float[] vectorToLightInEyeSpace = new float[4];
        final float[] pointPositionsInEyeSpace = new float[12];
        multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0);
        multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0);
        multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix, 0, pointLightPositions, 4);
        multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix, 0, pointLightPositions, 8);
        heightmapShaderProgram.setUniforms(modelViewMatrix, it_modelViewMatrix, modelViewProjectionMatrix, vectorToLightInEyeSpace, pointPositionsInEyeSpace, pointLightColors);
        heightmap.bindData(heightmapShaderProgram);
        heightmap.draw();
    }

    private void drawSkybox() {
        setIdentityM(modelMatrix, 0);
        updateMvpMatrixForSkybox();
        skyboxShaderProgram.useProgram();
        skyboxShaderProgram.setUniforms(modelViewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxShaderProgram);
        skybox.draw();
    }

    private void drawParticles() {
        glDepthMask(false);
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        setIdentityM(modelMatrix, 0);
        updateMvpMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particleShaderProgram.useProgram();
        particleShaderProgram.setUniforms(modelViewProjectionMatrix, currentTime, particleTexture);
        particleSystem.bindData(particleShaderProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
        glDepthMask(true);
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
        updateViewMatrices();
    }

    private void updateViewMatrices() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        System.arraycopy(viewMatrix, 0, viewMatrixForSkybox, 0, viewMatrix.length);
        translateM(viewMatrix, 0, 0, -1.5f, -2.5f);
    }

    private void updateMvpMatrix() {
//        multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0);
//        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(it_modelViewMatrix, 0, tempMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
    }

    private void updateMvpMatrixForSkybox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }
}
