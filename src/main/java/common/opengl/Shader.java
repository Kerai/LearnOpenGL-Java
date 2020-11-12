package common.opengl;
import static org.lwjgl.opengl.GL41C.*;

import java.nio.*;
import java.util.*;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

public class Shader {
	private static FloatBuffer temp_float_16 = BufferUtils.createFloatBuffer(16);
	
	public static boolean LOG_MISSING_UNIFORMS = true;
	public static boolean LOG_WARNING = true;

    Map<String, Integer> uniforms = new HashMap<>();
    public int id;
    boolean linked = false;
    
    int[] shaders = new int[4];
    int shadersIndex = 0;

    public Shader() {
        id = glCreateProgram();
    }
    
    public void compileShader(int type, CharSequence source) {
        int shader = glCreateShader(type);
        if(shader == 0)
            throw new RuntimeException("glCreateShader = 0");
        
        shaders[shadersIndex++] = shader;

//        for(int i=1; i<sources.length; i++) {
//        	String src = sources[i];
//        	if(src.startsWith("#version"))
//        		sources[i] = "//" + src;
//        }
        glShaderSource(shader, source);
        glCompileShader(shader);

        int comp = glGetShaderi(shader, GL_COMPILE_STATUS);
        
        if(comp == 0) {
	        String info = glGetShaderInfoLog(shader);
        	throw new RuntimeException("Shader compilation failed:\n" + info);
        }

        if(LOG_WARNING) {
	        String info = glGetShaderInfoLog(shader);
	        if(info != null && !info.isEmpty())
	        	System.err.println("Shader compilation warning:\n" + info);
        }

        glAttachShader(id, shader);
    }


    public void link() {
        glLinkProgram(id);
        
        int success = glGetProgrami(id, GL_LINK_STATUS);
        if(success == 0) {
            String infoLog = glGetProgramInfoLog(id);
            throw new RuntimeException("Shader linking failed:\n" + infoLog);
        }

        if(LOG_WARNING) {
	        String info = glGetProgramInfoLog(id);
	        if(info != null && !info.isEmpty())
	        	System.err.println("Shader compilation warning:\n" + info);
        }
        //fetchUniforms();
        
        for(int i=0; i<shadersIndex; i++) {
        	glDetachShader(id, shaders[i]);
        	glDeleteShader(shaders[i]);
        }
        shaders = null;
        shadersIndex = 0;
    }
    
    private void fetchUniforms() {
    	try(MemoryStack stack = MemoryStack.stackPush()) {
    		IntBuffer param1 = stack.mallocInt(1);
    		IntBuffer param2 = stack.mallocInt(1);
        	
        	glGetProgramiv(id, GL_ACTIVE_UNIFORMS, param1);
        	int len = param1.get(0);
        	
        	uniforms.clear();
    		
    		
        	for(int i=0; i<len; i++) {
        		
        		String original = glGetActiveUniform(id, i, param1, param2);
        		int size = param1.get(0);
        		int type = param2.get(0);
        		
        		String name = original;
        		if(size >1 && name.contains("[")) {
        			name = name.substring(0, name.indexOf('['));
        			for(int k=0; k<size; k++) {
        				String uname = name + "[" +k+ "]";
                		int location = glGetUniformLocation(id, uname);
                		uniforms.put(uname, location);
        			}
        		}
        		
        		int location = glGetUniformLocation(id, name);
        		uniforms.put(name, location);
        		
        		System.out.println(location+ ": " +name+ " [" +size+ "] " + findUniformTypeName(type));
        	}
    	}
    }
    
    
    public int fetchUniformLocation(String name) {
    	int location = glGetUniformLocation(id, name);
    	if(location >= 0) {
    		uniforms.putIfAbsent(name, location);
    	}
    	return location;
    }
    
    public int getUniformLocation(String name) {
    	Integer loc = uniforms.get(name);
    	if(loc == null) {
    		loc = fetchUniformLocation(name);
        	if(LOG_MISSING_UNIFORMS && loc == -1) {
    			System.out.println("Uniform missing: " + name );
        	}
        	uniforms.put(name, loc);
    	}
		return loc;
//    	return uniforms.getOrDefault(name, -1);
    }
    
    public void use() {
    	if(id <= 0)
    		throw new RuntimeException("shader not created");
        glUseProgram(id);
    }

    public void delete() {
        glDeleteProgram(id);
        id = 0;
        for(int i=0; i<shadersIndex; i++) {
        	glDeleteShader(shaders[i]);
        }
    }

    public void finalize() {
        if(id != 0) {
        	System.err.println("GC found undeleted shader " + id);
        }
    }
    
    // ----------- scalar
    
    
    public void setInt(String name, int value) {
    	glProgramUniform1i(this.id, getUniformLocation(name), value);
    }
    
    public void setInt(String name, boolean value) {
    	glProgramUniform1i(this.id, getUniformLocation(name), value?1:0);
    }
    
    public void setInts(String name, int[] values) {
    	glProgramUniform1iv(this.id, getUniformLocation(name), values);
    }
    
    public void setFloat(String name, float value) {
    	glProgramUniform1f(this.id, getUniformLocation(name), value);
    }
    
    public void setFloat(String name, double value) {
    	glProgramUniform1f(this.id, getUniformLocation(name), (float) value);
    }
    
    public void setFloats(String name, float[] values) {
    	glProgramUniform1fv(this.id, getUniformLocation(name), values);
    }

    // ----------- Vector 2

	public void setVec2(String name, float x, float y) {
    	glProgramUniform2f(this.id, getUniformLocation(name), x, y);
	}

	public void setVec2(String name, Vector2f vec) {
    	glProgramUniform2f(this.id, getUniformLocation(name), vec.x, vec.y);
	}

	public void setVec2d(String name, double x, double y) {
    	glProgramUniform2d(this.id, getUniformLocation(name), x, y);
	}

	public void setVec2d(String name, Vector2d vec) {
    	glProgramUniform2d(this.id, getUniformLocation(name), vec.x, vec.y);
	}

    // ----------- Vector 3
    
    public void setVec3(String name, float x, float y, float z) {
    	glProgramUniform3f(this.id, getUniformLocation(name), x, y, z);
    }
    
    public void setVec3(String name, Vector3f vec) {
    	glProgramUniform3f(this.id, getUniformLocation(name), vec.x, vec.y, vec.z);
    }
    
    // ----------- Vector 4
    
    public void setVec4(String name, float x, float y, float z, float w) {
    	glProgramUniform4f(this.id, getUniformLocation(name), x, y, z, w);
    }
    
    public void setVec4(String name, Vector4f vec) {
    	glProgramUniform4f(this.id, getUniformLocation(name), vec.x, vec.y, vec.z, vec.w);
    }
    
    public void setVec4(String name, Quaternionf vec) {
    	glProgramUniform4f(this.id, getUniformLocation(name), vec.x, vec.y, vec.z, vec.w);
    }
    
    // ----------- Matrix 3x3
    
	public void setMat3(String name, Matrix3fc mat) {
		Objects.requireNonNull(mat);
		glProgramUniformMatrix4fv(this.id, getUniformLocation(name), false, mat.get(temp_float_16));
	}
    
    // ----------- Matrix 4x4
    
//	public void setMat4(String name, Matrix4 mat) {
//		Objects.requireNonNull(mat);
//		glProgramUniformMatrix4fv(this.id, getUniformLocation(name), false, mat.val);
//	}
    
	public void setMat4(String name, Matrix4fc mat) {
		Objects.requireNonNull(mat);
		glProgramUniformMatrix4fv(this.id, getUniformLocation(name), false, mat.get(temp_float_16));
	}
	public void setMat4(String name, Matrix4fc mat, boolean transpose) {
		Objects.requireNonNull(mat);
		glProgramUniformMatrix4fv(this.id, getUniformLocation(name), transpose, mat.get(temp_float_16));
	}
    
	public void setMat4(String name, FloatBuffer buffer) {
		Objects.requireNonNull(buffer);
		glProgramUniformMatrix4fv(this.id, getUniformLocation(name), false, buffer);
	}
	public void setMat4(String name, FloatBuffer buffer, boolean transpose) {
		Objects.requireNonNull(buffer);
		glProgramUniformMatrix4fv(this.id, getUniformLocation(name), transpose, buffer);
	}
    
	public int fetchBlockIndex(String blockName) {
		return glGetUniformBlockIndex(this.id, blockName);
	}
	public void setBlockBinding(String name, int index) {
	    glUniformBlockBinding(this.id, fetchBlockIndex(name), index);
	}
	
	
	
//	public static Shader load(String vs, String fs) {
//    	Class<?> caller = StackWalker.getInstance(java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
//		return loadResource(caller, null, vs, null, fs);
//	}
//    public static Shader load(String vs, String gs, String fs) {
//    	Class<?> caller = StackWalker.getInstance(java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
//		return loadResource(caller, null, vs, gs, fs);
//    }
	
    public static String findUniformTypeName(int type) {
    	switch(type) {
    	case GL_FLOAT:
    		return "GL_FLOAT";
    	case GL_FLOAT_VEC2:
    		return "GL_FLOAT_VEC2";
    	case GL_FLOAT_VEC3:
    		return "GL_FLOAT_VEC3";
    	case GL_FLOAT_VEC4:
    		return "GL_FLOAT_VEC4";
    	case GL_FLOAT_MAT4:
    		return "GL_FLOAT_MAT4";
    	case GL_BOOL:
    		return "GL_BOOL";
    	case GL_INT:
    		return "GL_INT";
    	}
    	return type+"?";
    }
}
