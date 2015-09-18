package com.dinhduc.firstopenglproject.objects;

import android.opengl.GLES20;

import com.dinhduc.firstopenglproject.util.Geometry;
import com.dinhduc.firstopenglproject.util.Geometry.Cylinder;

import java.util.ArrayList;

import static android.opengl.GLES20.*;
import static com.dinhduc.firstopenglproject.util.Geometry.*;

/**
 * Created by Nguyen Dinh Duc on 9/8/2015.
 */
public class ObjectBuilder {
    private final static int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;
    private final ArrayList<DrawCommand> drawCommands = new ArrayList<>();
    private int offset = 0;

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    static interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexData;
        final ArrayList<DrawCommand> drawCommands;

        public GeneratedData(float[] vertexData, ArrayList<DrawCommand> drawCommands) {
            this.vertexData = vertexData;
            this.drawCommands = drawCommands;
        }
    }

    private static int sizeOfCicleInVertices(int numPoint) {
        return 1 + (numPoint + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoint) {
        return (1 + numPoint) * 2;
    }

    static GeneratedData createPuck(Cylinder puck, int numPoints) {
        int size = sizeOfCicleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);
        Circle puckTop = new Circle(puck.center.translateY(puck.height / 2), puck.radius);
        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);
        return builder.build();
    }

    static GeneratedData createMallet(Point center, float radius, float height, int numPoints) {
        int size = sizeOfCicleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2;
        ObjectBuilder builder = new ObjectBuilder(size);
        float baseHeight = height * 0.25f;
        Circle baseCircle = new Circle(center.translateY(-baseHeight), radius);
        Cylinder baseCylinder = new Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight);
        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);

        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3;
        Circle handleCircle = new Circle(center.translateY(height / 2), handleRadius);
        Cylinder handleCylinder = new Cylinder(handleCircle.center.translateY(-handleHeight / 2), handleRadius, handleHeight);
        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);
        return builder.build();
    }

    private void appendCircle(Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCicleInVertices(numPoints);
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadian = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            vertexData[offset++] = circle.center.x + circle.radius * (float) Math.cos(angleInRadian);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z + circle.radius * (float) Math.sin(angleInRadian);
        }
        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendOpenCylinder(Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height / 2);
        final float yEnd = cylinder.center.y + (cylinder.height / 2);
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadian = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            float xPosition = cylinder.center.x + cylinder.radius * (float) Math.cos(angleInRadian);
            float zPosition = cylinder.center.z + cylinder.radius * (float) Math.sin(angleInRadian);
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }
        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });

    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawCommands);
    }

}
