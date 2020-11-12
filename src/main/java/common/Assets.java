package common;

import static org.lwjgl.opengl.GL11C.glGetError;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.stb.STBImage.*;

import java.io.File;
import java.nio.*;

import common.opengl.*;

public class Assets {
	public static Texture loadTexture(String path, boolean flip, boolean sRGB) {
	    stbi_set_flip_vertically_on_load(flip); // tell stb_image.h to flip loaded texture's on the y-axis.
	    int[] width = new int[1];
	    int[] height = new int[1];
	    int[] channels = new int[1];
	    
	    ByteBuffer data = stbi_load(path, width, height, channels, 0);
	    if (data == null) {
		    throw new RuntimeException("Unable to load " + path);
	    }
	    
    	int internalFormat = BaseTexture.selectSizedFormat(channels[0], sRGB);
    	
    	int format = BaseTexture.selectFormat(channels[0], false);
		Texture tex = new Texture();
		
		System.out.println(internalFormat);
		tex.setStorage2D(8, internalFormat, width[0], height[0]);
    	tex.setWrap(GL_REPEAT, GL_REPEAT);
    	tex.setFilter(GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR);
    	tex.setSubImage2D(0, 0, 0, width[0], height[0], format, GL_UNSIGNED_BYTE, data);
    	tex.setAnisotropy(8);
    	tex.genMipmap();
	    stbi_image_free(data);
		return tex;
	}
	
	public static Cubemap loadTextureCube(String[] faces, boolean flip, boolean sRGB) {
	    stbi_set_flip_vertically_on_load(flip); // tell stb_image.h to flip loaded texture's on the y-axis.
	    int[] width = new int[1];
	    int[] height = new int[1];
	    int[] channels = new int[1];
	    
	    Cubemap tex = new Cubemap();
	    Cubemap.bind(0, tex);

	    for (int i = 0; i < faces.length; i++) {
	        ByteBuffer data = stbi_load(faces[i], width, height, channels, 3);
	        if (data == null) {
	            throw new RuntimeException("Cubemap failed: " + faces[i] );
	        }
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, sRGB ? GL_SRGB : GL_RGB, width[0], height[0], 0, GL_RGB, GL_UNSIGNED_BYTE, data);
            stbi_image_free(data);
	    }
	    
	    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
    	tex.genMipmap();
		return tex;
	}

	public static Texture loadTextureHDR(String path, boolean flip) {
	    stbi_set_flip_vertically_on_load(flip); // tell stb_image.h to flip loaded texture's on the y-axis.
	    int[] width = new int[1];
	    int[] height = new int[1];
	    int[] channels = new int[1];
        FloatBuffer data = stbi_loadf(path, width, height, channels, 0);
        if (data == null) {
		    throw new RuntimeException("Unable to load " + path);
        }
        
	    Texture tex = new Texture();
	    Texture.bind(0, tex);
	    
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, width[0], height[0], 0, GL_RGB, GL_FLOAT, data);
        stbi_image_free(data);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		return tex;
	}
	
	public static Cubemap loadTextureCube(File[] faces, boolean flip, boolean sRGB) {
		String[] strings = new String[faces.length];
		for(int i=0; i<strings.length; i++) {
			strings[i] = faces[i].getPath();
		}
		return loadTextureCube(strings, flip, sRGB);
	}
}
