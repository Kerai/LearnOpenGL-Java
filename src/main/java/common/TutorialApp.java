package common;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.*;

import org.joml.Math;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;

import common.opengl.Texture;

public abstract class TutorialApp implements GLM {

	public static final int SIZE_FLOAT = 4;
	
	protected static boolean MULTISAMPLING = false;

	public long window;
	public int scr_width = 1280;
	public int scr_height = 720;
	public Camera camera;
	protected float cameraSpeed = 1;
	protected float cameraBoost = 10;
	
	protected final boolean _fullscreen;

	float lastX = scr_width / 2.0f;
	float lastY = scr_height / 2.0f;
	boolean firstMouse = true;
	
	// timing
	protected float deltaTime = 0.0f;	// time between current frame and last frame
	protected float lastFrame = 0.0f;
	
	int fps = 0;
	
	static {
		//EclipseTools.fixConsole();
	}
	
	protected TutorialApp() {
		this._fullscreen = false;
	}
	
	protected TutorialApp(boolean fullscreen) {
		this._fullscreen = fullscreen;
	}

	protected boolean advanceNextFrame() {
        glfwSwapBuffers(window);
        glfwPollEvents();
        
        if(glfwWindowShouldClose(window))
        	return false;
        double currentFrame = glfwGetTime();
        deltaTime = (float) (currentFrame - lastFrame);
        lastFrame = (float) currentFrame;
        processInput(window);
		camera.calulateView();
        return true;
	}
	
	Set<AutoCloseable> managedResources = new HashSet<AutoCloseable>();
	
	public final void start() {

	    glfwInit();
	    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
	    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
	    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
//	    glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
	    glfwWindowHint(GLFW_DEPTH_BITS, 32);
	    
	    if(MULTISAMPLING)
	    	glfwWindowHint(GLFW_SAMPLES, 8);
	    
	    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
	    managedResources.add(GLFWErrorCallback.createPrint(System.err).set());

	    // glfw window creation
	    // --------------------
	    window = glfwCreateWindow(scr_width, scr_height, "LearnOpenGL", NULL, NULL);
	    if (window == NULL) {
	        System.err.println("Failed to create GLFW window");
	        glfwTerminate();
	        System.exit(1);
	    }

	    glfwMakeContextCurrent(window);
	    managedResources.add(glfwSetFramebufferSizeCallback(window, this::framebufferSizeCallback));
	    managedResources.add(glfwSetCursorPosCallback(window, this::mouseCallback));
	    managedResources.add(glfwSetScrollCallback(window, this::scrollCallback));
	    managedResources.add(glfwSetKeyCallback(window, this::keyCallback));

	    // tell GLFW to capture our mouse
	    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

	    {
		    GL.createCapabilities();
		    int[] x = new int[1];
		    int[] y = new int[1];
		    glfwGetWindowSize(window, x, y);
		    scr_width = x[0];
		    scr_height = y[0];
	    }
        
        GL46C.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL43C.GL_DEBUG_SEVERITY_NOTIFICATION , (IntBuffer) null, false);
        managedResources.add(GLUtil.setupDebugMessageCallback(System.out));
        
        {
	        String vendor = glGetString(GL_VENDOR);
	        String renderer = glGetString(GL_RENDERER);
	        String version = glGetString(GL_VERSION);
	        
	        System.out.println(version + " " + vendor + " " + renderer);
        }
        
        camera = new Camera(75, scr_width, scr_height);
	    camera.recalculatePerspective();
        glfwSwapInterval(1); // vsync on

        run();
        System.out.println("FINISH!");
        
	    managedResources.add(glfwSetFramebufferSizeCallback(window, null));
	    managedResources.add(glfwSetCursorPosCallback(window, null));
	    managedResources.add(glfwSetScrollCallback(window, null));
	    managedResources.add(glfwSetKeyCallback(window, null));

        for(var res : managedResources) {
			try {
				if(res != null)
					res.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        GL.setCapabilities(null);
        GL.destroy();
        glfwTerminate();
	}
	
	protected void processInput(long window)
	{
	    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
	        glfwSetWindowShouldClose(window, true);
	    
	    float speed = cameraSpeed;

	    if(glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
	    	speed = speed*cameraBoost;

	    if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
			camera.transform.translate(0, 0, -speed * deltaTime);
    	if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
			camera.transform.translate(0, 0, speed * deltaTime);
		if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
			camera.transform.translate(-speed * deltaTime, 0, 0);
	    if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
			camera.transform.translate(speed * deltaTime, 0, 0);
	}
	
	public void framebufferSizeCallback(long window, int width, int height) {
	    // make sure the viewport matches the new window dimensions; note that width and 
	    // height will be significantly larger than specified on retina displays.
	    glViewport(0, 0, width, height);
	    camera.vWidth = width;
	    camera.vHeight = height;
	    
	    camera.recalculatePerspective();
	}


	// glfw: whenever the mouse moves, this callback is called
	// -------------------------------------------------------
	public void mouseCallback(long window, double xpos, double ypos) {
	    if (firstMouse)
	    {
	        lastX = (float) xpos;
	        lastY = (float) ypos;
	        firstMouse = false;
	    }

	    float xoffset = (float) (xpos - lastX);
	    float yoffset = (float) (lastY - ypos); // reversed since y-coordinates go from bottom to top

	    lastX = (float) xpos;
	    lastY = (float) ypos;

		camera.transform.rotate(Math.toRadians(xoffset*0.1f), 0, -1, 0);
		camera.transform.rotate(Math.toRadians(yoffset*0.1f), 1, 0, 0);
	}
	
	public void keyCallback(long window, int key, int scancode, int action, int mods) {
		
	}

	// glfw: whenever the mouse scroll wheel scrolls, this callback is called
	// ----------------------------------------------------------------------
	public void scrollCallback(long window, double xoffset, double yoffset) {
		camera.fov += yoffset > 0 ? -2 : 2;
		camera.recalculatePerspective();
	}
	
	protected abstract void run();
	
	protected Texture loadTexture(String path) {
		return Assets.loadTexture(path, true, false);
	}
	
	protected Texture loadTextureHDR(String path, boolean flip) {
		return Assets.loadTextureHDR(path, flip);
	}
	
	protected Texture loadTexture(String path, boolean flip, boolean sRGB) {
		return Assets.loadTexture(path, flip, sRGB);
	}
	
}
