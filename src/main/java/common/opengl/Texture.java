package common.opengl;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;

public class Texture extends BaseTexture<Texture> {
	
	public Texture() {
		super(GL_TEXTURE_2D, true);
	}
	
	public static void bind(int textureUnit, Texture texture) {
		glActiveTexture(GL_TEXTURE0 + textureUnit);
		if(texture != null) {
			glBindTexture(GL_TEXTURE_2D, texture.id);
		} else {
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}

	@Override
	protected void bind(int unit) {
		bind(unit, this);
	}
	
	public Texture setWrap(int s, int t) {
		bind(0);
	    glTexParameteri(target, GL_TEXTURE_WRAP_S, s);
	    glTexParameteri(target, GL_TEXTURE_WRAP_T, t);
		return this;
	}
	
	public Texture setWrap(int str) {
		bind(0);
	    glTexParameteri(target, GL_TEXTURE_WRAP_S, str);
	    glTexParameteri(target, GL_TEXTURE_WRAP_T, str);
		return this;
	}

//	public static Texture loadFrom(String path) {
//		return Legacy.getLoader().loadTexture(new File(path), true, false);
//	}
//	public static Texture loadFrom(String path, boolean gammaCorrection) {
//		return Legacy.getLoader().loadTexture(new File(path), true, gammaCorrection);
//	}
	
	
	public static Texture load(String path, boolean flip, boolean sRGB) {
	    stbi_set_flip_vertically_on_load(flip); // tell stb_image.h to flip loaded texture's on the y-axis.
	    int[] width = new int[1];
	    int[] height = new int[1];
	    int[] channels = new int[1];
	    ByteBuffer data = stbi_load(path, width, height, channels, 0);
	    if (data == null) {
		    throw new RuntimeException("Unable to load " + path);
	    }
    	int internalFormat = Texture.selectSizedFormat(channels[0], sRGB);
    	int format = Texture.selectFormat(channels[0], false);
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

	@Override
	public String toString() {
		return "Texture [name=" + name + ", id=" + id + "]";
	}
	
	
	
}
