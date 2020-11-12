package common.opengl;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.ByteBuffer;

public class Cubemap extends BaseTexture<Cubemap> {

	public Cubemap() {
		super(GL_TEXTURE_CUBE_MAP, true);
	}
	
	public Cubemap setWrap(int s, int t, int r) {
		bind(0);
	    glTexParameteri(target, GL_TEXTURE_WRAP_S, s);
	    glTexParameteri(target, GL_TEXTURE_WRAP_T, t);
	    glTexParameteri(target, GL_TEXTURE_WRAP_R, r);
		return this;
	}
	
	public Cubemap setWrap(int str) {
		bind(0);
	    glTexParameteri(target, GL_TEXTURE_WRAP_S, str);
	    glTexParameteri(target, GL_TEXTURE_WRAP_T, str);
	    glTexParameteri(target, GL_TEXTURE_WRAP_R, str);
		return this;
	}

	@Override
	protected void bind(int unit) {
		bind(unit, this);
	}

	
	public static void bind(int textureUnit, Cubemap cubemap) {
		glActiveTexture(GL_TEXTURE0 + textureUnit);
		if(cubemap != null) {
			glBindTexture(GL_TEXTURE_CUBE_MAP, cubemap.id);
		} else {
			glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		}
	}

	public void setSubImage3D(int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
		bind(0);
		glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels);
	}
}
