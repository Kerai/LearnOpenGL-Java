package t02_lighting.c06_multi_lights;

import static common.GLM.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.*;

import org.joml.*;
import org.joml.Math;

import common.*;
import common.opengl.*;

public class TutorialMultipleLights extends TutorialAppLegacy {

	private Shader lightShader;
	private Shader lampShader;
	private int cubeVAO;
	private int VBO;
	private int lightVAO;
	
	
	Vector3f lightPos = new Vector3f(1.2f, 1.0f, 2.0f);
	private Texture specularMap;
	private Texture diffuseMap;
	
    Vector3f[] cubePositions = {
        vec3( 0.0f,  0.0f,  0.0f),
        vec3( 2.0f,  5.0f, -15.0f),
        vec3(-1.5f, -2.2f, -2.5f),
        vec3(-3.8f, -2.0f, -12.3f),
        vec3( 2.4f, -0.4f, -3.5f),
        vec3(-1.7f,  3.0f, -7.5f),
        vec3( 1.3f, -2.0f, -2.5f),
        vec3( 1.5f,  2.0f, -2.5f),
        vec3( 1.5f,  0.2f, -1.5f),
        vec3(-1.3f,  1.0f, -1.5f)
    };

    Vector3f[] pointLightPositions = {
		vec3( 0.7f,  0.2f,  2.0f),
		vec3( 2.3f, -3.3f, -4.0f),
		vec3(-4.0f,  2.0f, -12.0f),
		vec3( 0.0f,  0.0f, -3.0f)
    };

	protected void init() {

	    glEnable(GL_DEPTH_TEST);
	    
		lightShader = Shaders.load("6.multiple_lights.vs", "6.multiple_lights.fs");
		lampShader = Shaders.load("6.light_cube.vs", "6.light_cube.fs");
		
	    float vertices[] = {
	            // positions          // normals           // texture coords
	            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  0.0f,
	             0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
	             0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
	            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,
	            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,

	            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,
	             0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
	             0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
	            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
	            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,

	            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
	            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
	            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
	            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
	            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
	            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,

	             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
	             0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
	             0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
	             0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
	             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,

	            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,
	             0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  1.0f,
	             0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
	             0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
	            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  0.0f,
	            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,

	            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
	             0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
	             0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
	            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  0.0f,
	            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f
	        };
        // first, configure the cube's VAO (and VBO)
        cubeVAO = glGenVertexArrays();
        VBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindVertexArray(cubeVAO);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
        glEnableVertexAttribArray(2);

        // second, configure the light's VAO (VBO stays the same; the vertices are the same for the light object which is also a 3D cube)
        lightVAO = glGenVertexArrays();
        glBindVertexArray(lightVAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        // note that we update the lamp's position attribute's stride to reflect the updated buffer data
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
        glEnableVertexAttribArray(0);

        // load textures (we now use a utility function to keep the code more organized)
        // -----------------------------------------------------------------------------
        diffuseMap = loadTexture("resources/textures/container2.png");
        specularMap = loadTexture("resources/textures/container2_specular.png");

        // shader configuration
        // --------------------
        lightShader.use();
        lightShader.setInt("material.diffuse", 0);
        lightShader.setInt("material.specular", 1);
	    
	    camera.recalculatePerspective();
	}
	
	int frames = 0;

	protected void loop() {
		frames++;

        // render
        // ------
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // be sure to activate shader when setting uniforms/drawing objects
        lightShader.use();
        lightShader.setVec3("viewPos", camera.position);
        lightShader.setFloat("material.shininess", 32.0f);

        // directional light
        lightShader.setVec3("dirLight.direction", -0.2f, -1.0f, -0.3f);
        lightShader.setVec3("dirLight.ambient", 0.05f, 0.05f, 0.05f);
        lightShader.setVec3("dirLight.diffuse", 0.4f, 0.4f, 0.4f);
        lightShader.setVec3("dirLight.specular", 0.5f, 0.5f, 0.5f);
        // point light 1
        lightShader.setVec3("pointLights[0].position", pointLightPositions[0]);
        lightShader.setVec3("pointLights[0].ambient", 0.05f, 0.05f, 0.05f);
        lightShader.setVec3("pointLights[0].diffuse", 0.8f, 0.8f, 0.8f);
        lightShader.setVec3("pointLights[0].specular", 1.0f, 1.0f, 1.0f);
        lightShader.setFloat("pointLights[0].constant", 1.0f);
        lightShader.setFloat("pointLights[0].linear", 0.09f);
        lightShader.setFloat("pointLights[0].quadratic", 0.032f);
        // point light 2
        lightShader.setVec3("pointLights[1].position", pointLightPositions[1]);
        lightShader.setVec3("pointLights[1].ambient", 0.05f, 0.05f, 0.05f);
        lightShader.setVec3("pointLights[1].diffuse", 0.8f, 0.8f, 0.8f);
        lightShader.setVec3("pointLights[1].specular", 1.0f, 1.0f, 1.0f);
        lightShader.setFloat("pointLights[1].constant", 1.0f);
        lightShader.setFloat("pointLights[1].linear", 0.09f);
        lightShader.setFloat("pointLights[1].quadratic", 0.032f);
        // point light 3
        lightShader.setVec3("pointLights[2].position", pointLightPositions[2]);
        lightShader.setVec3("pointLights[2].ambient", 0.05f, 0.05f, 0.05f);
        lightShader.setVec3("pointLights[2].diffuse", 0.8f, 0.8f, 0.8f);
        lightShader.setVec3("pointLights[2].specular", 1.0f, 1.0f, 1.0f);
        lightShader.setFloat("pointLights[2].constant", 1.0f);
        lightShader.setFloat("pointLights[2].linear", 0.09f);
        lightShader.setFloat("pointLights[2].quadratic", 0.032f);
        // point light 4
        lightShader.setVec3("pointLights[3].position", pointLightPositions[3]);
        lightShader.setVec3("pointLights[3].ambient", 0.05f, 0.05f, 0.05f);
        lightShader.setVec3("pointLights[3].diffuse", 0.8f, 0.8f, 0.8f);
        lightShader.setVec3("pointLights[3].specular", 1.0f, 1.0f, 1.0f);
        lightShader.setFloat("pointLights[3].constant", 1.0f);
        lightShader.setFloat("pointLights[3].linear", 0.09f);
        lightShader.setFloat("pointLights[3].quadratic", 0.032f);
        // spotLight
        lightShader.setVec3("spotLight.position", camera.position);
        lightShader.setVec3("spotLight.direction", camera.forward);
        lightShader.setVec3("spotLight.ambient", 0.0f, 0.0f, 0.0f);
        lightShader.setVec3("spotLight.diffuse", 1.0f, 1.0f, 1.0f);
        lightShader.setVec3("spotLight.specular", 1.0f, 1.0f, 1.0f);
        lightShader.setFloat("spotLight.constant", 1.0f);
        lightShader.setFloat("spotLight.linear", 0.09f);
        lightShader.setFloat("spotLight.quadratic", 0.032f);
        lightShader.setFloat("spotLight.cutOff", Math.cos(Math.toRadians(12.5f)));
        lightShader.setFloat("spotLight.outerCutOff", Math.cos(Math.toRadians(15.0f)));   

        // view/projection transformations
        lightShader.setMat4("projection", camera.projection);
        lightShader.setMat4("view", camera.view);

        // world transformation
//        final Matrix4f model = new Matrix4f();
//        lightShader.setMat4("model", model);

        // bind textures
        Texture.bind(0, diffuseMap);
        Texture.bind(1, specularMap);

        // render the cube
        glBindVertexArray(cubeVAO);
        
        var axis = new Vector3f(1.0f, 0.3f, 0.5f).normalize();
        
        for (int i = 0; i < cubePositions.length; i++)
        {
            // calculate the model matrix for each object and pass it to shader before drawing
            var model = new Matrix4f();
            model.translation(cubePositions[i]);
            float angle = 20.0f * i + frames * 0.1f * i;
            model.rotate(Math.toRadians(angle), axis);
            lightShader.setMat4("model", model);

            glDrawArrays(GL_TRIANGLES, 0, 36);
        }

        // also draw the lamp object
        lampShader.use();
        lampShader.setMat4("projection", camera.projection);
        lampShader.setMat4("view", camera.view);
        
        var model = new Matrix4f();
        glBindVertexArray(lightVAO);
        for (int i = 0; i < 4; i++)
        {
            model.translation(pointLightPositions[i]);
            model.scale(vec3(0.2f)); // Make it a smaller cube
            lampShader.setMat4("model", model);
            glDrawArrays(GL_TRIANGLES, 0, 36);
        }
	}

	public static void main(String[] args) {
		new TutorialMultipleLights().start();
	}

}
