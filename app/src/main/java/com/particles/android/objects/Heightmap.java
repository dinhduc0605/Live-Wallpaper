package com.particles.android.objects;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import com.particles.android.Constants;
import com.particles.android.data.IndexBuffer;
import com.particles.android.data.VertexBuffer;
import com.particles.android.programs.HeightmapShaderProgram;
import com.particles.android.util.Geometry;
import com.particles.android.util.Geometry.Point;

import java.nio.channels.Pipe;

import static android.opengl.GLES20.*;

/**
 * Created by Nguyen Dinh Duc on 9/23/2015.
 */
public class Heightmap {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTE_PER_FLOAT;

    private final int width;
    private final int height;
    private final int numElements;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;


    public Heightmap(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        if (width * height > 65536) {
            throw new RuntimeException("Heightmap is too large for the index buffer");
        }
        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
        indexBuffer = new IndexBuffer(createIndexData());

    }

    private int calculateNumElements() {
        return (width - 1) * (height - 1) * 2 * 3;
    }

    public float[] loadBitmapData(Bitmap bitmap) {

        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();
        final float[] heightmapVertices = new float[width * height * TOTAL_COMPONENT_COUNT];
        int offset = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                final Point point = getPoint(pixels, row, col);
                heightmapVertices[offset++] = point.x;
                heightmapVertices[offset++] = point.y;
                heightmapVertices[offset++] = point.z;

                final Point top = getPoint(pixels, row - 1, col);
                final Point left = getPoint(pixels, row, col - 1);
                final Point right = getPoint(pixels, row, col + 1);
                final Point bottom = getPoint(pixels, row + 1, col);

                final Geometry.Vector rightToLeft = Geometry.vectorBetween(right, left);
                final Geometry.Vector topToBottom = Geometry.vectorBetween(top, bottom);
                final Geometry.Vector normal = rightToLeft.crossProduct(topToBottom).normalize();

                heightmapVertices[offset++] = normal.x;
                heightmapVertices[offset++] = normal.y;
                heightmapVertices[offset++] = normal.z;
            }
        }
        return heightmapVertices;
    }

    private Point getPoint(int[] pixels, int row, int col) {
        float x = ((float) col / (float) (width - 1)) - 0.5f;
        float z = ((float) row / (float) (height - 1)) - 0.5f;
        row = clamp(row, 0, width - 1);
        col = clamp(col, 0, height - 1);
        float y = (float) Color.red(pixels[(row * height) + col]) / (float) 255;
        return new Point(x, y, z);
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private short[] createIndexData() {
        final short[] indexData = new short[numElements];
        int offset = 0;
        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width - 1; col++) {
                short topLeftIndexNum = (short) (row * width + col);
                short topRightIndexNum = (short) (row * width + col + 1);
                short bottomLeftIndexNum = (short) ((row + 1) * width + col);
                short bottomRightIndexNum = (short) ((row + 1) * width + col + 1);
                indexData[offset++] = topLeftIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = topRightIndexNum;

                indexData[offset++] = topRightIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = bottomRightIndexNum;
            }
        }
        return indexData;
    }

    public void bindData(HeightmapShaderProgram heightmapShaderProgram) {
        vertexBuffer.setVertexAttribPointer(0, heightmapShaderProgram.getaPositionLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexBuffer.setVertexAttribPointer(POSITION_COMPONENT_COUNT * Constants.BYTE_PER_FLOAT, heightmapShaderProgram.getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
