package com.dinhduc.firstopenglproject.objects;

import com.dinhduc.firstopenglproject.data.VertextArray;
import com.dinhduc.firstopenglproject.objects.ObjectBuilder.DrawCommand;
import com.dinhduc.firstopenglproject.programs.ColorShaderProgram;

import java.util.ArrayList;

import static com.dinhduc.firstopenglproject.util.Geometry.Point;

/**
 * Created by Nguyen Dinh Duc on 9/8/2015.
 */
public class Mallet {
    private static final int POSITION_COMPONET_COUNT = 3;
    public final float radius;
    public final float height;
    private final VertextArray vertextArray;
    private final ArrayList<DrawCommand> drawCommands;

    public Mallet(float radius, float height, int numPointsAroundMallet) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(new Point(0f, 0f, 0f), radius, height, numPointsAroundMallet);
        this.radius = radius;
        this.height = height;
        vertextArray = new VertextArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertextArray.setVertexAttriPointer(0, colorShaderProgram.getaPositionLocation(), POSITION_COMPONET_COUNT, 0);
    }

    public void draw() {
        for (DrawCommand drawCommand : drawCommands) {
            drawCommand.draw();
        }
    }
}
