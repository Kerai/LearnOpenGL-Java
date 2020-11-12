package common.opengl;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.ARBTextureFilterAnisotropic;

public abstract class BaseTexture<T> {
	public String name;
	public int id;
	public final int target;
	
	protected BaseTexture(int target, boolean generate) {
	    this.target = target;
	    if(generate)
	    	id = glGenTextures();
	}

	public void setStorage2D(int levels, int internalFormat, int width, int height) {
		bind(0);
		setParameter(GL_TEXTURE_MAX_LEVEL, levels);
		nglTexImage2D(target, 0, internalFormat, width, height, 0, GL_RGB, GL_BYTE, 0);
	}

	public void setParameter(int param, int value) {
		bind(0);
	    glTexParameteri(target, param, value);
	}

	public void setParameter(int param, float value) {
		bind(0);
	    glTexParameterf(target, param, value);
	}

	public void setParameter(int param, int[] value) {
		bind(0);
	    glTexParameteriv(target, param, value);
	}

	public void setParameter(int param, float[] value) {
		bind(0);
	    glTexParameterfv(target, param, value);
	}
	
	public Texture setFilter(int min, int mag) {
		bind(0);
	    glTexParameteri(target, GL_TEXTURE_MIN_FILTER, min);
	    glTexParameteri(target, GL_TEXTURE_MAG_FILTER, mag);
		return (Texture) this;
	}
	
	public void setAnisotropy(float v) {
		bind(0);
	    glTexParameterf(target, ARBTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY, v);
	}
	
	protected abstract void bind(int unit);
	
	public static int selectFormat(int channels, boolean sRGB) {
		switch(channels) {
		case 1:
			return GL_RED;
		case 2:
			return GL_RG;
		case 3:
			return sRGB ? GL_SRGB : GL_RGB;
		case 4:
			return sRGB ? GL_SRGB_ALPHA : GL_RGBA;
		}
    	throw new RuntimeException("Channels = " + channels);
	}
	
	public static int selectSizedFormat(int channels, boolean sRGB) {
		switch(channels) {
		case 1:
			return GL_R8;
		case 2:
			return GL_RG8;
		case 3:
			return sRGB ? GL_SRGB8 : GL_RGB8;
		case 4:
			return sRGB ? GL_SRGB8_ALPHA8 : GL_RGBA8;
		}
    	throw new RuntimeException("Channels = " + channels);
	}

	public void setStorage2DMultisample(int samples, int internalFormat, int width, int height, boolean fixedSampleLocations) {
		bind(0);
	    glTexImage2DMultisample(target, samples, internalFormat, width, height, fixedSampleLocations);
	}
	
	public void setSubImage2D(int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels) {
		bind(0);
	    glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}
	
	public void delete() {
		glDeleteTextures(id);
	}
	
	public void finalize() {
		// TODO make sure the texture is deleted
		// do not try to delete teture from here
		// as this method can be called from different thread
	}

	public void genMipmap() {
		bind(0);
		glGenerateMipmap(target);
	}

}
