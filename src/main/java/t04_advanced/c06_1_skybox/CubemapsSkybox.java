package t04_advanced.c06_1_skybox;

import static common.GLM.*;
import static org.lwjgl.opengl.GL30C.*;

import org.joml.Matrix4f;

import common.*;
import common.opengl.*;

public class CubemapsSkybox extends TutorialAppLegacy {

	private Shader shader;
	private Shader skyboxShader;

	private int cubeVAO;
	private int cubeVBO;

	private int skyboxVAO;
	private int skyboxVBO;

	private Texture cubeTexture;
	private Cubemap cubemapTexture;

	protected void init() {
	    // configure global opengl state
	    // -----------------------------
	    glEnable(GL_DEPTH_TEST);
	    glEnable(GL_BLEND);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    
	    glCullFace(GL_FRONT);
	    
		shader = Shaders.load("6.1.cubemaps.vs", "6.1.cubemaps.fs");
		skyboxShader = Shaders.load("6.1.skybox.vs", "6.1.skybox.fs");
		
	    // set up vertex data (and buffer(s)) and configure vertex attributes
	    // ------------------------------------------------------------------
	    float cubeVertices[] = {
	        // positions          // texture Coords
	        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
	         0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
	         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
	         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
	        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
	        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

	        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
	         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
	         0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
	         0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
	        -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
	        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

	        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
	        -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
	        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
	        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
	        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
	        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

	         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
	         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
	         0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
	         0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
	         0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
	         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

	        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
	         0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
	         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
	         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
	        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
	        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

	        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
	         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
	         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
	         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
	        -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
	        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
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
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, (3 * 4));
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
        
	    cubeTexture = loadTexture("resources/textures/container.jpg");
	    String[] faces = {
		        "resources/textures/skybox/right.jpg",
		        "resources/textures/skybox/left.jpg",
		        "resources/textures/skybox/top.jpg",
		        "resources/textures/skybox/bottom.jpg",
		        "resources/textures/skybox/front.jpg",
		        "resources/textures/skybox/back.jpg"
		    };
	    
	    cubemapTexture = Assets.loadTextureCube(faces, false, false);

	    shader.use();
	    shader.setInt("texture1", 1);

	    skyboxShader.use();
	    skyboxShader.setInt("skybox", 0);
	    
	    camera.recalculatePerspective();
	}

	int frames = 0;

	protected void loop() {
		frames++;
		camera.calulateView();

        // make sure we clear the framebuffer's content
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.use();
        Matrix4f model = mat4(1.0f);
        shader.setMat4("view", camera.view);
        shader.setMat4("projection", camera.projection);
        // cubes
        glBindVertexArray(cubeVAO);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, cubeTexture.id);
        model.translate(vec3(-1.0f, 0.0f, -1.0f));
        shader.setMat4("model", model);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        model.identity();
        model.translate(vec3(2.0f, 0.0f, 0.0f));
        shader.setMat4("model", model);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        
        // draw skybox as last
        glDepthFunc(GL_LEQUAL);  // change depth function so depth test passes when values are equal to depth buffer's content
        skyboxShader.use();
        Matrix4f view = new Matrix4f().set(camera.view).setTranslation(0, 0, 0); // remove translation from the view matrix
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
		new CubemapsSkybox().start();
	}

}
