package common.model;

import org.joml.*;

import common.opengl.*;

public class Model {
	
	public Mesh[] meshes;
	public Material[] materials;
	public Node nodes;
//	public Map<String, Node> nodeMap = new HashMap<String, Node>();

	public Model() {
		
	}
	
	private void drawMesh(Shader shader, Mesh mesh) {
		if(mesh.materialIndex >=0) {
			var mat = materials[mesh.materialIndex];
			
			if(mat.diffuseTexture != null)
				Texture.bind(0, mat.diffuseTexture);
			if(mat.specularTexture != null)
				Texture.bind(1, mat.specularTexture);
			if(mat.emissiveTexture != null)
				Texture.bind(2, mat.emissiveTexture);
			if(mat.normalTexture != null)
				Texture.bind(3, mat.normalTexture);

			if(mat.ambientColor != null)
				shader.setVec3("material.ambientColor", mat.ambientColor);
			if(mat.diffuseColor != null)
				shader.setVec3("material.diffuseColor", mat.diffuseColor);
			if(mat.specularColor != null)
				shader.setVec3("material.specularColor", mat.specularColor);

	        shader.setFloat("material.opacity", mat.opacity);
	        shader.setFloat("material.shininess", mat.shininess < 1 ? 30 : mat.shininess);
		}
		mesh.draw();
	}

	public void drawMeshes(Shader shader) {
		shader.use();
		for(Mesh mesh : meshes) {
			if(mesh == null)
				continue;
			drawMesh(shader, mesh);
		}
	}
	
	public void drawNodes(Shader shader, Matrix4fc transform) {
		render(shader, transform, nodes);
	}
	
	private void render(Shader shader, Matrix4fc transform, Node node) {
		if(node.meshes != null && node.meshes.length>0)
			shader.setMat4("model", new Matrix4f(transform).mul(node.absoluteTransform));
		
		for(int meshIndex : node.meshes) {
			Mesh mesh = meshIndex > 0 ? meshes[meshIndex] : null;
			if(mesh == null)
				continue;
			drawMesh(shader, mesh);
		}
		
		for(Node n : node.children) {
			render(shader, transform, n);
		}
	}

	public int countTriangles() {
		int count = 0;
		for(Mesh mesh : meshes) {
			count += mesh.countTriangles();
		}
		return count;
	}

//	public Node findNode(String id) {
//		return nodeMap.get(id);
//	}

	public static void create(Mesh mesh) {
		Model m = new Model();
		m.meshes = new Mesh[] {mesh};
	}
}
