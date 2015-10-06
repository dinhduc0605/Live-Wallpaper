package com.particles.android.wallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.particles.android.ParticlesRenderer;

/**
 * Created by Nguyen Dinh Duc on 9/30/2015.
 */
public class GLWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }

    private GLEngine.WallpaperGLSurfaceView glSurfaceView;
    private boolean renderSet;
    ParticlesRenderer particlesRenderer;

    public class GLEngine extends Engine {
        @Override
        public void onOffsetsChanged(final float xOffset, final float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            glSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    particlesRenderer.handleOffsetsChanged(xOffset);
                }
            });
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportEs2 = configurationInfo.reqGlEsVersion >= 0x20000
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                    && (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")));
            particlesRenderer = new ParticlesRenderer(GLWallpaperService.this);
            if (supportEs2) {
                glSurfaceView.setEGLContextClientVersion(2);
                glSurfaceView.setRenderer(particlesRenderer);
                renderSet = true;
            } else {
                Toast.makeText(GLWallpaperService.this, "Not suppport OpenGL ES 2.0", Toast.LENGTH_LONG).show();
                return;
            }

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (renderSet) {
                if (visible) {
                    glSurfaceView.onResume();
                } else {
                    glSurfaceView.onPause();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            glSurfaceView.onWallpaperDestroy();
        }

        class WallpaperGLSurfaceView extends GLSurfaceView {

            public WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            public void onWallpaperDestroy() {
                super.onDetachedFromWindow();
            }
        }
    }
}
