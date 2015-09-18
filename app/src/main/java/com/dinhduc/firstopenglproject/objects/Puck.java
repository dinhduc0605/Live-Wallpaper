package com.dinhduc.firstopenglproject.objects;

import com.dinhduc.firstopenglproject.data.VertextArray;
import com.dinhduc.firstopenglproject.programs.ColorShaderProgram;
import com.dinhduc.firstopenglproject.util.Geometry.*;

import java.util.ArrayList;

import static com.dinhduc.firstopenglproject.objects.ObjectBuilder.*;

/**
 * Created by Nguyen Dinh Duc on 9/9/2015.
 */
public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius, height;
    private final VertextArray vertextArray;
    private final ArrayList<DrawCommand> drawCommands;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        GeneratedData generatedData = ObjectBuilder.createPuck(new Cylinder(new Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);
        this.height = height;
        this.radius = radius;
        vertextArray = new VertextArray(generatedData.vertexData);
        this.drawCommands = generatedData.drawCommands;
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertextArray.setVertexAttriPointer(0, colorShaderProgram.getaPositionLocation(), POSITION_COMPONENT_COUNT, 0);
    }
    public void draw(){
        for (DrawCommand drawCommand : drawCommands){
            drawCommand.draw();
        }
    }
}
