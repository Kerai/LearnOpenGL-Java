package t04_advanced.c06_2_envmap;

import static common.GLM.*;
import static org.lwjgl.opengl.GL30C.*;

import java.util.*;

import org.joml.Matrix4f;

import common.*;
import common.model.Model;
import common.opengl.*;

public class CubemapsEnvMap extends TutorialAppLegacy {

	private Shader shader1;
	private Shader shader2;
	private Shader skyboxShader;

	private int cubeVAO;
	private int cubeVBO;

	private int skyboxVAO;
	private int skyboxVBO;

	private Cubemap cubemapTexture;
	private Model backpack;

	protected void init() {
	    // configure global opengl state
	    // -----------------------------
	    glEnable(GL_DEPTH_TEST);
	    glEnable(GL_BLEND);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    
	    glCullFace(GL_FRONT);

	    AssimpLoader assimp = new AssimpLoader();

		shader1 = Shaders.load("6.2.cubemaps.vs", "6.2.cubemaps.fs");
		shader2 = Shaders.load("6.2.cubemaps.vs", "6.2.cubemaps_refract.fs");
		skyboxShader = Shaders.load("6.2.skybox.vs", "6.2.skybox.fs");
		
	    // set up vertex data (and buffer(s)) and configure vertex attributes
	    // ------------------------------------------------------------------
	    float cubeVertices[] = {
	            // positions       //texcoord // normals
	            -0.5f, -0.5f, -0.5f, 0, 0,  0.0f,  0.0f, -1.0f,
	             0.5f, -0.5f, -0.5f, 0, 0,  0.0f,  0.0f, -1.0f,
	             0.5f,  0.5f, -0.5f, 0, 0,  0.0f,  0.0f, -1.0f,
	             0.5f,  0.5f, -0.5f, 0, 0,  0.0f,  0.0f, -1.0f,
	            -0.5f,  0.5f, -0.5f, 0, 0,  0.0f,  0.0f, -1.0f,
	            -0.5f, -0.5f, -0.5f, 0, 0,  0.0f,  0.0f, -1.0f,

	            -0.5f, -0.5f,  0.5f, 0, 0,  0.0f,  0.0f, 1.0f,
	             0.5f, -0.5f,  0.5f, 0, 0,  0.0f,  0.0f, 1.0f,
	             0.5f,  0.5f,  0.5f, 0, 0,  0.0f,  0.0f, 1.0f,
	             0.5f,  0.5f,  0.5f, 0, 0,  0.0f,  0.0f, 1.0f,
	            -0.5f,  0.5f,  0.5f, 0, 0,  0.0f,  0.0f, 1.0f,
	            -0.5f, -0.5f,  0.5f, 0, 0,  0.0f,  0.0f, 1.0f,

	            -0.5f,  0.5f,  0.5f, 0, 0, -1.0f,  0.0f,  0.0f,
	            -0.5f,  0.5f, -0.5f, 0, 0, -1.0f,  0.0f,  0.0f,
	            -0.5f, -0.5f, -0.5f, 0, 0, -1.0f,  0.0f,  0.0f,
	            -0.5f, -0.5f, -0.5f, 0, 0, -1.0f,  0.0f,  0.0f,
	            -0.5f, -0.5f,  0.5f, 0, 0, -1.0f,  0.0f,  0.0f,
	            -0.5f,  0.5f,  0.5f, 0, 0, -1.0f,  0.0f,  0.0f,

	             0.5f,  0.5f,  0.5f, 0, 0,  1.0f,  0.0f,  0.0f,
	             0.5f,  0.5f, -0.5f, 0, 0,  1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f, -0.5f, 0, 0,  1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f, -0.5f, 0, 0,  1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f,  0.5f, 0, 0,  1.0f,  0.0f,  0.0f,
	             0.5f,  0.5f,  0.5f, 0, 0,  1.0f,  0.0f,  0.0f,

	            -0.5f, -0.5f, -0.5f, 0, 0,  0.0f, -1.0f,  0.0f,
	             0.5f, -0.5f, -0.5f, 0, 0,  0.0f, -1.0f,  0.0f,
	             0.5f, -0.5f,  0.5f, 0, 0,  0.0f, -1.0f,  0.0f,
	             0.5f, -0.5f,  0.5f, 0, 0,  0.0f, -1.0f,  0.0f,
	            -0.5f, -0.5f,  0.5f, 0, 0,  0.0f, -1.0f,  0.0f,
	            -0.5f, -0.5f, -0.5f, 0, 0,  0.0f, -1.0f,  0.0f,

	            -0.5f,  0.5f, -0.5f, 0, 0,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f, -0.5f, 0, 0,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f,  0.5f, 0, 0,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f,  0.5f, 0, 0,  0.0f,  1.0f,  0.0f,
	            -0.5f,  0.5f,  0.5f, 0, 0,  0.0f,  1.0f,  0.0f,
	            -0.5f,  0.5f, -0.5f, 0, 0,  0.0f,  1.0f,  0.0f
	        };
	    float skyboxVertices[] = {
	            // positions          
	            -1.0f,  1.0f, -1.0f,
	            -1.0f, -1.0f, -1.0f,
	             1.0f, -1.0f, -1.0f,
	             1.0f, -1.0f, -1.0f,
	             1.0f,  1.0f, -1.0f,
	            -1.0f,  1.0f, -1.0f,

	            -1.0f, -1.0f,  1.0f,
	            -1.0f, -1.0f, -1.0f,
	            -1.0f,  1.0f, -1.0f,
	            -1.0f,  1.0f, -1.0f,
	            -1.0f,  1.0f,  1.0f,
	            -1.0f, -1.0f,  1.0f,

	             1.0f, -1.0f, -1.0f,
	             1.0f, -1.0f,  1.0f,
	             1.0f,  1.0f,  1.0f,
	             1.0f,  1.0f,  1.0f,
	             1.0f,  1.0f, -1.0f,
	             1.0f, -1.0f, -1.0f,

	            -1.0f, -1.0f,  1.0f,
	            -1.0f,  1.0f,  1.0f,
	             1.0f,  1.0f,  1.0f,
	             1.0f,  1.0f,  1.0f,
	             1.0f, -1.0f,  1.0f,
	            -1.0f, -1.0f,  1.0f,

	            -1.0f,  1.0f, -1.0f,
	             1.0f,  1.0f, -1.0f,
	             1.0f,  1.0f,  1.0f,
	             1.0f,  1.0f,  1.0f,
	            -1.0f,  1.0f,  1.0f,
	            -1.0f,  1.0f, -1.0f,

	            -1.0f, -1.0f, -1.0f,
	            -1.0f, -1.0f,  1.0f,
	             1.0f, -1.0f, -1.0f,
	             1.0f, -1.0f, -1.0f,
	            -1.0f, -1.0f,  1.0f,
	             1.0f, -1.0f,  1.0f
	        };
	    
	    // cube VAO
        cubeVAO = glGenVertexArrays();
        cubeVBO = glGenBuffers();
        glBindVertexArray(cubeVAO);
        glBindBuffer(GL_ARRAY_BUFFER, cubeVBO);
        glBufferData(GL_ARRAY_BUFFER, cubeVertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8 * 4, (3 * 4));
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 8 * 4, (5 * 4));
        glBindVertexArray(0);
        
        // plane VAO
        skyboxVAO = glGenVertexArrays();
        skyboxVBO = glGenBuffers();
        glBindVertexArray(skyboxVAO);
        glBindBuffer(GL_ARRAY_BUFFER, skyboxVBO);
        glBufferData(GL_ARRAY_BUFFER, skyboxVertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        glBindVertexArray(0);
        
	    String[] faces = {
		        "resources/textures/skybox/right.jpg",
		        "resources/textures/skybox/left.jpg",
		        "resources/textures/skybox/top.jpg",
		        "resources/textures/skybox/bottom.jpg",
		        "resources/textures/skybox/front.jpg",
		        "resources/textures/skybox/back.jpg"
		    };
	    cubemapTexture = Assets.loadTextureCube(faces, false, false);
	    backpack = assimp.loadModel("resources/objects/backpack/backpack.obj");

	    shader1.use();
	    shader1.setInt("texture1", 0);
	    
	    shader2.use();
	    shader2.setInt("texture1", 0);
	    
	    skyboxShader.use();
	    skyboxShader.setInt("skybox", 0);
	    
	    camera.recalculatePerspective();
	}

	int frames = 0;
	
	static Set<String> bejbis = new HashSet<String>();
	private static void DEBUG(String bejbi) {
		if(bejbis.add(bejbi)) {
			System.out.println(bejbi);
		}
	}

	protected void loop() {
		frames++;

        // make sure we clear the framebuffer's content
        glClearColor(0.1f, 0.1f, 0.4f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader1.use();
        shader1.setMat4("view", camera.view);
        shader1.setMat4("projection", camera.projection);
        shader1.setVec3("cameraPos", camera.position);
        // cubes
        shader1.setMat4("model", new Matrix4f().translate(1, 0, 0));
        glBindVertexArray(cubeVAO);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        
        shader1.setMat4("model", new Matrix4f().translate(5, 0, 0));
        backpack.drawMeshes(shader1);
        
        
        shader2.use();
        shader2.setMat4("view", camera.view);
        shader2.setMat4("projection", camera.projection);
        shader2.setVec3("cameraPos", camera.position);

        shader2.setMat4("model", new Matrix4f().translate(-1, 0, 0));
        glBindVertexArray(cubeVAO);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        
        shader2.setMat4("model", new Matrix4f().translate(-5, 0, 0));
        backpack.drawMeshes(shader2);
        
        
        // draw skybox as last
        glDepthFunc(GL_LEQUAL);  // change depth function so depth test passes when values are equal to depth buffer's content
        skyboxShader.use();
        Matrix4f view = new Matrix4f(camera.view).setTranslation(0, 0, 0); // remove translation from the view matrix
        skyboxShader.setMat4("view", view);
        skyboxShader.setMat4("projection", camera.projection);
        // skybox cube
        glBindVertexArray(skyboxVAO);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapTexture.id);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glBindVertexArray(0);
        glDepthFunc(GL_LESS); // set depth function back to default
	}
	
	public static void main(String[] args) {
		new CubemapsEnvMap().start();
	}

}
