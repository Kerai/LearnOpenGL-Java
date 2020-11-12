package t04_advanced.c05_framebuffers;

import static common.GLM.*;
import static org.lwjgl.opengl.GL30C.*;

import org.joml.Matrix4f;

import common.*;
import common.opengl.*;

public class Framebuffers extends TutorialAppLegacy {

	private Shader shader;
	private Shader screenShader;

	private int cubeVAO;
	private int cubeVBO;

	private int planeVAO;
	private int planeVBO;

	private Texture cubeTexture;
	private Texture floorTexture;

	private int quadVAO;
	private int quadVBO;

	private int framebuffer;
	private Texture textureColorbuffer;
	private int rbo;
	
	protected void init() {
	    // configure global opengl state
	    // -----------------------------
	    glEnable(GL_DEPTH_TEST);
	    glEnable(GL_BLEND);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    
	    glCullFace(GL_FRONT);
	    
		shader = Shaders.load("5.2.framebuffers.vs", "5.2.framebuffers.fs");
		screenShader = Shaders.load("5.2.framebuffers_screen.vs", "5.2.framebuffers_screen.fs");
		
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
	    float planeVertices[] = {
	        // positions          // texture Coords 
	         5.0f, -0.5f,  5.0f,  2.0f, 0.0f,
	        -5.0f, -0.5f,  5.0f,  0.0f, 0.0f,
	        -5.0f, -0.5f, -5.0f,  0.0f, 2.0f,

	         5.0f, -0.5f,  5.0f,  2.0f, 0.0f,
	        -5.0f, -0.5f, -5.0f,  0.0f, 2.0f,
	         5.0f, -0.5f, -5.0f,  2.0f, 2.0f
	    };
	    float quadVertices[] = { // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
	        // positions   // texCoords
	        -1.0f,  1.0f,  0.0f, 1.0f,
	        -1.0f, -1.0f,  0.0f, 0.0f,
	         1.0f, -1.0f,  1.0f, 0.0f,

	        -1.0f,  1.0f,  0.0f, 1.0f,
	         1.0f, -1.0f,  1.0f, 0.0f,
	         1.0f,  1.0f,  1.0f, 1.0f
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
        planeVAO = glGenVertexArrays();
        planeVBO = glGenBuffers();
        glBindVertexArray(planeVAO);
        glBindBuffer(GL_ARRAY_BUFFER, planeVBO);
        glBufferData(GL_ARRAY_BUFFER, planeVertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);
        glBindVertexArray(0);
        
        // transparent VAO
        quadVAO = glGenVertexArrays();
        quadVBO = glGenBuffers();
        glBindVertexArray(quadVAO);
        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, (2 * 4));
        glBindVertexArray(0);

	    cubeTexture = loadTexture("resources/textures/container.jpg");
	    floorTexture  = loadTexture("resources/textures/metal.png");

	    shader.use();
	    shader.setInt("texture1", 0);

	    screenShader.use();
	    screenShader.setInt("screenTexture", 0);
	    
	    // framebuffer configuration
	    // -------------------------
	    framebuffer = glGenFramebuffers();
	    glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
	    // create a color attachment texture
	    textureColorbuffer = new Texture();
	    glBindTexture(GL_TEXTURE_2D, textureColorbuffer.id);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, scr_width, scr_height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureColorbuffer.id, 0);
	    // create a renderbuffer object for depth and stencil attachment (we won't be sampling these)
	    rbo = glGenRenderbuffers();
	    glBindRenderbuffer(GL_RENDERBUFFER, rbo);
	    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, scr_width, scr_height); // use a single renderbuffer object for both a depth AND stencil buffer.
	    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo); // now actually attach it
	    // now that we actually created the framebuffer and added all attachments we want to check if it is actually complete now
	    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
	        System.err.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
	    glBindFramebuffer(GL_FRAMEBUFFER, 0);
	    
	    camera.recalculatePerspective();
	}

	int frames = 0;

	protected void loop() {
		frames++;

        // render
        // ------
        // bind to framebuffer and draw scene as we normally would to color texture 
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        glEnable(GL_DEPTH_TEST); // enable depth testing (is disabled for rendering screen-space quad)

        // make sure we clear the framebuffer's content
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.use();
        Matrix4f model = mat4(1.0f);
        shader.setMat4("view", camera.view);
        shader.setMat4("projection", camera.projection);
        // cubes
        glBindVertexArray(cubeVAO);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, cubeTexture.id);
        model.translate(vec3(-1.0f, 0.0f, -1.0f));
        shader.setMat4("model", model);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        model.identity();
        model.translate(vec3(2.0f, 0.0f, 0.0f));
        shader.setMat4("model", model);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        // floor
        glBindVertexArray(planeVAO);
        glBindTexture(GL_TEXTURE_2D, floorTexture.id);
        shader.setMat4("model", mat4(1.0f));
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);

        // now bind back to default framebuffer and draw a quad plane with the attached framebuffer color texture
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDisable(GL_DEPTH_TEST); // disable depth test so screen-space quad isn't discarded due to depth test.
        // clear all relevant buffers
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // set clear color to white (not really necessery actually, since we won't be able to see behind the quad anyways)
        glClear(GL_COLOR_BUFFER_BIT);

        glViewport(0, 0, scr_width, scr_height);
        screenShader.use();
        glBindVertexArray(quadVAO);
        glBindTexture(GL_TEXTURE_2D, textureColorbuffer.id);	// use the color attachment texture as the texture of the quad plane
        glDrawArrays(GL_TRIANGLES, 0, 6);
	}
	
	public void framebufferSizeCallback(long window, int width, int height)
	{
		super.framebufferSizeCallback(window, width, height);
        glBindTexture(GL_TEXTURE_2D, textureColorbuffer.id);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

	    glBindRenderbuffer(GL_RENDERBUFFER, rbo);
	    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height); // use a single renderbuffer object for both a depth AND stencil buffer.
	    glBindRenderbuffer(GL_RENDERBUFFER, 0);
	}

	public static void main(String[] args) {
		new Framebuffers().start();
	}

}
