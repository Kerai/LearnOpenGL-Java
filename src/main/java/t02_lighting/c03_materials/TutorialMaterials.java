package t02_lighting.c03_materials;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.*;

import org.joml.*;
import org.joml.Math;

import common.*;
import common.opengl.Shader;

public class TutorialMaterials extends TutorialAppLegacy {

	private Shader lightShader;
	private Shader lampShader;
	private int cubeVAO;
	private int VBO;
	private int lightVAO;
	
	
	Vector3f lightPos = new Vector3f(1.2f, 1.0f, 2.0f);

	public void run() {
		init();
		while(advanceNextFrame()) {
			loop();
		}
	}
	
	protected void init() {

	    glEnable(GL_DEPTH_TEST);
	    
		lightShader = Shaders.load("3.2.materials.vs", "3.2.materials.fs");
		lampShader = Shaders.load("3.2.light_cube.vs", "3.2.light_cube.fs");
		
	    float vertices[] = {
	            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
	             0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
	             0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
	             0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
	            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
	            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

	            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
	             0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
	             0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
	             0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
	            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
	            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,

	            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
	            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
	            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
	            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
	            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
	            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

	             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
	             0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
	             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

	            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
	             0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
	             0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
	             0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
	            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
	            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

	            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
	            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
	            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
	        };
	    
	    
	    // first, configure the cube's VAO (and VBO)
	    cubeVAO = glGenVertexArrays();
	    VBO = glGenBuffers();

	    glBindBuffer(GL_ARRAY_BUFFER, VBO);
	    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

	    glBindVertexArray(cubeVAO);

	    // position attribute
	    glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * SIZE_FLOAT, 0);
	    glEnableVertexAttribArray(0);
	    // normal attribute
	    glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * SIZE_FLOAT, 3 * SIZE_FLOAT);
	    glEnableVertexAttribArray(1);


	    // second, configure the light's VAO (VBO stays the same; the vertices are the same for the light object which is also a 3D cube)
	    lightVAO = glGenVertexArrays();
	    glBindVertexArray(lightVAO);

	    glBindBuffer(GL_ARRAY_BUFFER, VBO);
	    // note that we update the lamp's position attribute's stride to reflect the updated buffer data
	    glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * SIZE_FLOAT, 0);
	    glEnableVertexAttribArray(0);
		
	    
	    camera.recalculatePerspective();
	}

	protected void loop() {

        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // be sure to activate shader when setting uniforms/drawing objects
        lightShader.use();
        lightShader.setVec3("light.position", lightPos);
        lightShader.setVec3("viewPos", camera.position);

        // light properties
        Vector3f lightColor = new Vector3f();
        lightColor.x = Math.sin((float) glfwGetTime() * 2.0f);
        lightColor.y = Math.sin((float) glfwGetTime() * 0.7f);
        lightColor.z = Math.sin((float) glfwGetTime() * 1.3f);
        Vector3f diffuseColor = lightColor.mul(0.5f, new Vector3f()); // decrease the influence
        Vector3f ambientColor = diffuseColor.mul(0.2f, new Vector3f()); // low influence
        lightShader.setVec3("light.ambient", ambientColor);
        lightShader.setVec3("light.diffuse", diffuseColor);
        lightShader.setVec3("light.specular", 1.0f, 1.0f, 1.0f);

        // material properties
        lightShader.setVec3("material.ambient", 1.0f, 0.5f, 0.31f);
        lightShader.setVec3("material.diffuse", 1.0f, 0.5f, 0.31f);
        lightShader.setVec3("material.specular", 0.5f, 0.5f, 0.5f); // specular lighting doesn't have full effect on this object's material
        lightShader.setFloat("material.shininess", 32.0f);

        // view/projection transformations
        lightShader.setMat4("projection", camera.projection);
        lightShader.setMat4("view", camera.view);

        // world transformation
        Matrix4f model = new Matrix4f();
        lightShader.setMat4("model", model);

        // render the cube
        glBindVertexArray(cubeVAO);
        glDrawArrays(GL_TRIANGLES, 0, 36);


        // also draw the lamp object
        lampShader.use();
        lampShader.setMat4("projection", camera.projection);
        lampShader.setMat4("view", camera.view);
        model.identity();
        model.translate(lightPos);
        model.scale(0.2f); // a smaller cube
        lampShader.setMat4("model", model);

        glBindVertexArray(lightVAO);
        glDrawArrays(GL_TRIANGLES, 0, 36);
	}

	public static void main(String[] args) {
		new TutorialMaterials().start();
	}

}
