package t01_start.c07_camera;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;

import org.joml.*;
import org.joml.Math;

import common.*;
import common.opengl.Shader;

public class TutorialCamera extends TutorialApp {
	
	@Override
	protected void run() {

	    // configure global opengl state
	    // -----------------------------
	    glEnable(GL_DEPTH_TEST);

	    // build and compile our shader zprogram
	    // ------------------------------------
	    Shader ourShader = Shaders.load("7.4.camera.vs", "7.4.camera.fs");

	    // set up vertex data (and buffer(s)) and configure vertex attributes
	    // ------------------------------------------------------------------
	    float vertices[] = {
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
	    // world space positions of our cubes
	    
	    Vector3f[] cubePositions = {
		        new Vector3f( 0.0f,  0.0f,  0.0f),
		        new Vector3f( 2.0f,  5.0f, -15.0f),
		        new Vector3f(-1.5f, -2.2f, -2.5f),
		        new Vector3f(-3.8f, -2.0f, -12.3f),
		        new Vector3f( 2.4f, -0.4f, -3.5f),
		        new Vector3f(-1.7f,  3.0f, -7.5f),
		        new Vector3f( 1.3f, -2.0f, -2.5f),
		        new Vector3f( 1.5f,  2.0f, -2.5f),
		        new Vector3f( 1.5f,  0.2f, -1.5f),
		        new Vector3f(-1.3f,  1.0f, -1.5f)
	    };
	    
	    int VBO, VAO;
	    VAO = glGenVertexArrays();
	    VBO = glGenBuffers();

	    glBindVertexArray(VAO);

	    glBindBuffer(GL_ARRAY_BUFFER, VBO);
	    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

	    // position attribute
	    glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0);
	    glEnableVertexAttribArray(0);
	    // texture coord attribute
	    glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);
	    glEnableVertexAttribArray(1);


	    // load and create a texture 
	    // -------------------------
	    int texture1, texture2;
	    // texture 1
	    // ---------
	    texture1 = glGenTextures();
	    glBindTexture(GL_TEXTURE_2D, texture1);
	    // set the texture wrapping parameters
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	    // set texture filtering parameters
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	    // load image, create texture and generate mipmaps
	    int width[] = new int[1];
	    int height[] = new int[1];
	    int nrChannels[] = new int[1];
	    
	    stbi_set_flip_vertically_on_load(true); // tell stb_image.h to flip loaded texture's on the y-axis.
	    ByteBuffer data = stbi_load("resources/textures/container.jpg", width, height, nrChannels, 0);
	    if (data != null)
	    {
	        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width[0], height[0], 0, GL_RGB, GL_UNSIGNED_BYTE, data);
	        glGenerateMipmap(GL_TEXTURE_2D);
	    }
	    else
	    {
	        System.err.println("Failed to load texture");
	    }
	    stbi_image_free(data);
	    // texture 2
	    // ---------
	    texture2 = glGenTextures();
	    glBindTexture(GL_TEXTURE_2D, texture2);
	    // set the texture wrapping parameters
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	    // set texture filtering parameters
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	    // load image, create texture and generate mipmaps
	    data = stbi_load("resources/textures/awesomeface.png", width, height, nrChannels, 0);
	    if (data != null)
	    {
	        // note that the awesomeface.png has transparency and thus an alpha channel, so make sure to tell OpenGL the data type is of GL_RGBA
	        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width[0], height[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	        glGenerateMipmap(GL_TEXTURE_2D);
	    }
	    else
	    {
	        System.err.println("Failed to load texture");
	    }
	    stbi_image_free(data);

	    // tell opengl for each sampler to which texture unit it belongs to (only has to be done once)
	    // -------------------------------------------------------------------------------------------
	    ourShader.use();
	    ourShader.setInt("texture1", 0);
	    ourShader.setInt("texture2", 1);

	    camera.transform.setTranslation(0, 0, -3);
	    
	    while(advanceNextFrame()) {

	        // render
	        // ------
	        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
	        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 

	        // bind textures on corresponding texture units
	        glActiveTexture(GL_TEXTURE0);
	        glBindTexture(GL_TEXTURE_2D, texture1);
	        glActiveTexture(GL_TEXTURE1);
	        glBindTexture(GL_TEXTURE_2D, texture2);

	        // activate shader
	        ourShader.use();

	        // pass projection matrix to shader (note that in this case it could change every frame)
	        ourShader.setMat4("projection", camera.projection);

	        // camera/view transformation
	        ourShader.setMat4("view", camera.view);

	        // render boxes
	        glBindVertexArray(VAO);
	        for (int i = 0; i < 10; i++)
	        {
	            // calculate the model matrix for each object and pass it to shader before drawing
	            Matrix4f model = new Matrix4f(); // make sure to initialize matrix to identity matrix first
	            model.translate(cubePositions[i]);
	            float angle = 20.0f * i;
	            
	            model.rotate(Math.toRadians(angle), new Vector3f(1.0f, 0.3f, 0.5f));
	            ourShader.setMat4("model", model);

	            glDrawArrays(GL_TRIANGLES, 0, 36);
	        }
	    }

	    // optional: de-allocate all resources once they've outlived their purpose:
	    // ------------------------------------------------------------------------
	    glDeleteVertexArrays(VAO);
	    glDeleteBuffers(VBO);
	}
	
	public static void main(String[] argz) {
		new TutorialCamera().start();
	}
}
