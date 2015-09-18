package com.dinhduc.firstopenglproject.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import static android.opengl.GLES20.*;

/**
 * Created by Nguyen Dinh Duc on 9/4/2015.
 */
public class TextureHelper {
    public static final String TAG = "TextHelper";

    public static int loadTexture(Context context, int resourceId) {
        final int textObjectIds[] = new int[1];
        glGenTextures(1, textObjectIds, 0);
        if (textObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate texture object");
            }
            return 0;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not decode image");
            }
            glDeleteTextures(1, textObjectIds, 0);
            return 0;
        }
        glBindTexture(GL_TEXTURE_2D, textObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        return textObjectIds[0];
    }
}
