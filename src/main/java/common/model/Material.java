package common.model;

import org.joml.Vector3f;

import common.opengl.Texture;

public class Material {
	public int index;
	public String name;

	public float shininess = 30f;
	public float opacity = 1f;
	
	public Vector3f diffuseColor;
	public Vector3f ambientColor;
	public Vector3f specularColor;
	
	public Texture diffuseTexture;
	public Texture specularTexture;
	public Texture emissiveTexture;
	public Texture normalTexture;
	
	@Override
	public String toString() {
		return "Material [index=" + index + ", name=" + name + ", shininess=" + shininess + ", opacity=" + opacity
				+ ", diffuseColor=" + diffuseColor + ", ambientColor=" + ambientColor + ", specularColor="
				+ specularColor + ", diffuseTexture=" + diffuseTexture + ", specularTexture=" + specularTexture
				+ ", emissiveTexture=" + emissiveTexture + ", normalTexture=" + normalTexture + "]";
	}
	
	
}
