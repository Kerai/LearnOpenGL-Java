package common.model;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.*;

public class Mesh {
	public String name;
	
	public int VAO;
	public int VBO;
	public int EBO;
	
	public int numVertices;
	public int numIndices;
	
	public int renderMode = GL_TRIANGLES;

	public int materialIndex;
	
	public Mesh() {
		
	}
	
	public void setupMesh(float[] vertices, int[] indices, boolean hasNormals, boolean hasTangents) {
        // create buffers/arrays
        VAO = glGenVertexArrays();
        VBO = glGenBuffers();
        
        // load data into vertex buffers
        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        // A great thing about structs is that their memory layout is sequential for all its items.
        // The effect is that we can simply pass a pointer to the struct and it translates perfectly to a glm::vec3/2 array which
        // again translates to 3/2 floats which translates to a byte array.
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);  

        if(indices != null) {
	        EBO = glGenBuffers();
	        numIndices = indices.length;
	        
	        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
	        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        int stride = (3+2);
        if(hasNormals) {
        	stride += 3;
	        if(hasTangents)
	        	stride += 6;
        }
        
        numVertices = vertices.length / stride;
        
        stride *= 4; // count in bytes per float

        // set the vertex attribute pointers
        // vertex Positions
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        
        // vertex texture coords
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, (3) *4);
        
        if(hasNormals) {
	        // vertex normals
	        glEnableVertexAttribArray(2);
	        glVertexAttribPointer(2, 3, GL_FLOAT, false, stride, (3+2) *4);
	        
	        if(hasTangents) {
		        // vertex tangent
		        glEnableVertexAttribArray(3);
		        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, (3+2+3) *4);
		        // vertex bitangent
		        glEnableVertexAttribArray(4);
		        glVertexAttribPointer(4, 3, GL_FLOAT, false, stride, (3+2+3+3) *4);
	        }
        }
        glBindVertexArray(0);
	}
	
	public void setupMesh(FloatBuffer vertices, IntBuffer indices, boolean hasNormals, boolean hasTangents) {
        // create buffers/arrays
        VAO = glGenVertexArrays();
        VBO = glGenBuffers();
        
        // load data into vertex buffers
        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        // A great thing about structs is that their memory layout is sequential for all its items.
        // The effect is that we can simply pass a pointer to the struct and it translates perfectly to a glm::vec3/2 array which
        // again translates to 3/2 floats which translates to a byte array.
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);  

        if(indices != null) {
	        EBO = glGenBuffers();
	        numIndices = indices.remaining();
	        
	        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
	        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        int stride = (3+2);
        if(hasNormals) {
        	stride += 3;
	        if(hasTangents)
	        	stride += 6;
        }
        
        numVertices = vertices.remaining() / stride;
        
        stride *= 4; // count in bytes per float

        // set the vertex attribute pointers
        // vertex Positions
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        
        // vertex texture coords
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, (3) *4);
        
        if(hasNormals) {
	        // vertex normals
	        glEnableVertexAttribArray(2);
	        glVertexAttribPointer(2, 3, GL_FLOAT, false, stride, (3+2) *4);
	        
	        if(hasTangents) {
		        // vertex tangent
		        glEnableVertexAttribArray(3);
		        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, (3+2+3) *4);
		        // vertex bitangent
		        glEnableVertexAttribArray(4);
		        glVertexAttribPointer(4, 3, GL_FLOAT, false, stride, (3+2+3+3) *4);
	        }
        }
        glBindVertexArray(0);
	}
	
	public void setupMeshNoAttribs(FloatBuffer vertices, IntBuffer indices) {
        // create buffers/arrays
        VAO = glGenVertexArrays();
        VBO = glGenBuffers();
        EBO = glGenBuffers();

        glBindVertexArray(VAO);
        // load data into vertex buffers
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        // A great thing about structs is that their memory layout is sequential for all its items.
        // The effect is that we can simply pass a pointer to the struct and it translates perfectly to a glm::vec3/2 array which
        // again translates to 3/2 floats which translates to a byte array.
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);  

        if(indices != null) {
	        EBO = glGenBuffers();
	        numIndices = indices.remaining();
	        
	        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
	        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

	}

	public void bind() {
        glBindVertexArray(VAO);
	}
	
	public void draw() {
		bind();
		if(EBO != 0) {
			glDrawElements(renderMode, numIndices, GL_UNSIGNED_INT, 0);
		} else {
			glDrawArrays(renderMode, 0, numVertices);
		}
	}
	
	public void drawInstanced(int instances) {
		bind();
		if(EBO != 0) {
			glDrawElementsInstanced(renderMode, numIndices, GL_UNSIGNED_INT, 0, instances);
		} else {
			glDrawArraysInstanced(renderMode, 0, numVertices, instances);
		}
	}

	public void deleteObjects() {
		if(VAO != 0) {
			glDeleteVertexArrays(VAO);
		}
		if(VBO != 0) {
			glDeleteBuffers(VBO);
		}
		if(EBO != 0) {
			glDeleteBuffers(EBO);
		}
	}

	public int countTriangles() {
		if(EBO != 0)
			return numIndices / 3;
		return numVertices / 3;
	}
}
