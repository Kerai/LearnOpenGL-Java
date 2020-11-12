package t03_models.c01_loading;

import static org.lwjgl.opengl.GL11C.*;

import org.joml.Matrix4f;

import common.*;
import common.model.Model;
import common.opengl.Shader;

public class TutorialModelLoading extends TutorialAppLegacy {

	private Shader shader;
	
	private Model backpack;
	private Model suz;

	protected void init() {
		AssimpLoader assimp = new AssimpLoader();
		
		shader = Shaders.load("1.model_loading.vs", "1.model_loading.fs");
		
		backpack = assimp.loadModel("resources/objects/backpack/backpack.obj");
		
	    System.out.println("GL ERROR = " + glGetError());
		
		shader.use();
	    
	    camera.recalculatePerspective();
	    
	    System.out.println("GL ERROR = " + glGetError());
		
		shader.setInt("texture_diffuse1", 0);
		
		
		System.out.println(backpack.materials[0]);
	}
	
	int frames = 0;

	protected void loop() {
		frames++;

        // render
        // ------
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    glEnable(GL_DEPTH_TEST);

        // be sure to activate shader when setting uniforms/drawing objects
        shader.use();
        // view/projection transformations
        shader.setMat4("projection", camera.projection);
        shader.setMat4("view", camera.view);
        
        shader.setMat4("model", new Matrix4f().scale(2f));
        backpack.drawMeshes(shader);
	}

	public static void main(String[] args) {
		new TutorialModelLoading().start();
	}

}
