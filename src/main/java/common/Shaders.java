package common;

import static org.lwjgl.opengl.GL32C.*;

import java.io.IOException;
import java.util.Scanner;

import common.opengl.Shader;

public class Shaders {
	@SuppressWarnings("resource")
	public static Shader load(String vsPath, String fsPath) {
    	Class<?> caller = StackWalker.getInstance(java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
    	try (
    			var vsIn = caller.getResourceAsStream(vsPath);
    			var fsIn = caller.getResourceAsStream(fsPath);
    			)
    	{
        	String vs = new Scanner(vsIn, "UTF8").useDelimiter("\\A").next();
        	String fs = new Scanner(fsIn, "UTF8").useDelimiter("\\A").next();
        	Shader shader = new Shader();
        	shader.compileShader(GL_VERTEX_SHADER, vs);
        	shader.compileShader(GL_FRAGMENT_SHADER, fs);
        	shader.link();
    		return shader;
    	} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("resource")
	public static Shader load(String vsPath, String gsPath, String fsPath) {
    	Class<?> caller = StackWalker.getInstance(java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
    	try (
    			var vsIn = caller.getResourceAsStream(vsPath);
    			var gsIn = caller.getResourceAsStream(gsPath);
    			var fsIn = caller.getResourceAsStream(fsPath);
    			)
    	{
        	String vs = new Scanner(vsIn, "UTF8").useDelimiter("\\A").next();
        	String gs = new Scanner(gsIn, "UTF8").useDelimiter("\\A").next();
        	String fs = new Scanner(fsIn, "UTF8").useDelimiter("\\A").next();
        	Shader shader = new Shader();
        	shader.compileShader(GL_VERTEX_SHADER, vs);
        	shader.compileShader(GL_GEOMETRY_SHADER, gs);
        	shader.compileShader(GL_FRAGMENT_SHADER, fs);
        	shader.link();
    		return shader;
    	} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
