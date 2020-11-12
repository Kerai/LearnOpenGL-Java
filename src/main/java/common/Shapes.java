package common;

import static common.GLM.*;
import static org.lwjgl.opengl.GL45C.*;

import java.nio.*;
import java.util.ArrayList;

import org.joml.*;
import org.joml.Math;
import org.lwjgl.system.MathUtil;

import common.model.Mesh;


public class Shapes {
	public static Mesh createSkybox() {
	    float vertices[] = {
	            // positions         // texCoords  //normals
	            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f,  0.0f,  0.0f, -1.0f,
	             0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, -1.0f,
	             0.5f,  0.5f, -0.5f,  1.0f,  1.0f,  0.0f,  0.0f, -1.0f,
	             0.5f,  0.5f, -0.5f,  1.0f,  1.0f,  0.0f,  0.0f, -1.0f,
	            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, -1.0f,
	            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f,  0.0f,  0.0f, -1.0f,

	            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  0.0f,  0.0f,  1.0f,
	             0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
	             0.5f,  0.5f,  0.5f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,
	             0.5f,  0.5f,  0.5f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,
	            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
	            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  0.0f,  0.0f,  1.0f,

	            -0.5f,  0.5f,  0.5f,  1.0f,  0.0f, -1.0f,  0.0f,  0.0f,
	            -0.5f,  0.5f, -0.5f,  1.0f,  1.0f, -1.0f,  0.0f,  0.0f,
	            -0.5f, -0.5f, -0.5f,  0.0f,  1.0f, -1.0f,  0.0f,  0.0f,
	            -0.5f, -0.5f, -0.5f,  0.0f,  1.0f, -1.0f,  0.0f,  0.0f,
	            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
	            -0.5f,  0.5f,  0.5f,  1.0f,  0.0f, -1.0f,  0.0f,  0.0f,

	             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f,
	             0.5f,  0.5f, -0.5f,  1.0f,  1.0f,  1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f, -0.5f,  0.0f,  1.0f,  1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f, -0.5f,  0.0f,  1.0f,  1.0f,  0.0f,  0.0f,
	             0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
	             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f,

	            -0.5f, -0.5f, -0.5f,  0.0f,  1.0f,  0.0f, -1.0f,  0.0f,
	             0.5f, -0.5f, -0.5f,  1.0f,  1.0f,  0.0f, -1.0f,  0.0f,
	             0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f, -1.0f,  0.0f,
	             0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f, -1.0f,  0.0f,
	            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  0.0f, -1.0f,  0.0f,
	            -0.5f, -0.5f, -0.5f,  0.0f,  1.0f,  0.0f, -1.0f,  0.0f,

	            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f, -0.5f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
	             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
	            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  0.0f,  1.0f,  0.0f,
	            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f
	        };
	    Mesh mesh = new Mesh();
	    mesh.name = "Skybox";
	    mesh.setupMesh(vertices, null, true, false);
	    return mesh;
	}
	
	public static Mesh createCube() {
		
	    float vertices[] = {
	            // back face
	            -1.0f, -1.0f, -1.0f, 0.0f, 0.0f,  0.0f,  0.0f, -1.0f,  0,0,0,    0,0,0, // bottom-left
	             1.0f,  1.0f, -1.0f, 1.0f, 1.0f,  0.0f,  0.0f, -1.0f,  0,0,0,    0,0,0, // top-right
	             1.0f, -1.0f, -1.0f, 1.0f, 0.0f,  0.0f,  0.0f, -1.0f,  0,0,0,    0,0,0, // bottom-right         
	             1.0f,  1.0f, -1.0f, 1.0f, 1.0f,  0.0f,  0.0f, -1.0f,  0,0,0,    0,0,0, // top-right
	            -1.0f, -1.0f, -1.0f, 0.0f, 0.0f,  0.0f,  0.0f, -1.0f,  0,0,0,    0,0,0, // bottom-left
	            -1.0f,  1.0f, -1.0f, 0.0f, 1.0f,  0.0f,  0.0f, -1.0f,  0,0,0,    0,0,0, // top-left
	            // front face
	            -1.0f, -1.0f,  1.0f, 0.0f, 0.0f,  0.0f,  0.0f,  1.0f,  0,0,0,    0,0,0, // bottom-left
	             1.0f, -1.0f,  1.0f, 1.0f, 0.0f,  0.0f,  0.0f,  1.0f,  0,0,0,    0,0,0, // bottom-right
	             1.0f,  1.0f,  1.0f, 1.0f, 1.0f,  0.0f,  0.0f,  1.0f,  0,0,0,    0,0,0, // top-right
	             1.0f,  1.0f,  1.0f, 1.0f, 1.0f,  0.0f,  0.0f,  1.0f,  0,0,0,    0,0,0, // top-right
	            -1.0f,  1.0f,  1.0f, 0.0f, 1.0f,  0.0f,  0.0f,  1.0f,  0,0,0,    0,0,0, // top-left
	            -1.0f, -1.0f,  1.0f, 0.0f, 0.0f,  0.0f,  0.0f,  1.0f,  0,0,0,    0,0,0, // bottom-left
	            // left face
	            -1.0f,  1.0f,  1.0f, 1.0f, 0.0f, -1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // top-right
	            -1.0f,  1.0f, -1.0f, 1.0f, 1.0f, -1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // top-left
	            -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, -1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // bottom-left
	            -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, -1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // bottom-left
	            -1.0f, -1.0f,  1.0f, 0.0f, 0.0f, -1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // bottom-right
	            -1.0f,  1.0f,  1.0f, 1.0f, 0.0f, -1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // top-right
	            // right face
	             1.0f,  1.0f,  1.0f, 1.0f, 0.0f,  1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // top-left
	             1.0f, -1.0f, -1.0f, 0.0f, 1.0f,  1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // bottom-right
	             1.0f,  1.0f, -1.0f, 1.0f, 1.0f,  1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // top-right         
	             1.0f, -1.0f, -1.0f, 0.0f, 1.0f,  1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // bottom-right
	             1.0f,  1.0f,  1.0f, 1.0f, 0.0f,  1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // top-left
	             1.0f, -1.0f,  1.0f, 0.0f, 0.0f,  1.0f,  0.0f,  0.0f,  0,0,0,    0,0,0, // bottom-left     
	            // bottom face
	            -1.0f, -1.0f, -1.0f, 0.0f, 1.0f,  0.0f, -1.0f,  0.0f,  0,0,0,    0,0,0, // top-right
	             1.0f, -1.0f, -1.0f, 1.0f, 1.0f,  0.0f, -1.0f,  0.0f,  0,0,0,    0,0,0, // top-left
	             1.0f, -1.0f,  1.0f, 1.0f, 0.0f,  0.0f, -1.0f,  0.0f,  0,0,0,    0,0,0, // bottom-left
	             1.0f, -1.0f,  1.0f, 1.0f, 0.0f,  0.0f, -1.0f,  0.0f,  0,0,0,    0,0,0, // bottom-left
	            -1.0f, -1.0f,  1.0f, 0.0f, 0.0f,  0.0f, -1.0f,  0.0f,  0,0,0,    0,0,0, // bottom-right
	            -1.0f, -1.0f, -1.0f, 0.0f, 1.0f,  0.0f, -1.0f,  0.0f,  0,0,0,    0,0,0, // top-right
	            // top face
	            -1.0f,  1.0f, -1.0f, 0.0f, 1.0f,  0.0f,  1.0f,  0.0f,  0,0,0,    0,0,0, // top-left
	             1.0f,  1.0f , 1.0f, 1.0f, 0.0f,  0.0f,  1.0f,  0.0f,  0,0,0,    0,0,0, // bottom-right
	             1.0f,  1.0f, -1.0f, 1.0f, 1.0f,  0.0f,  1.0f,  0.0f,  0,0,0,    0,0,0, // top-right     
	             1.0f,  1.0f,  1.0f, 1.0f, 0.0f,  0.0f,  1.0f,  0.0f,  0,0,0,    0,0,0, // bottom-right
	            -1.0f,  1.0f, -1.0f, 0.0f, 1.0f,  0.0f,  1.0f,  0.0f,  0,0,0,    0,0,0, // top-left
	            -1.0f,  1.0f,  1.0f, 0.0f, 0.0f,  0.0f,  1.0f,  0.0f,  0,0,0,    0,0,0,  // bottom-left   

	        };
	    calcTangents(vertices);
	    Mesh mesh = new Mesh();
	    mesh.name = "Cube";
	    mesh.setupMesh(vertices, null, true, true);
	    return mesh;
	}
	

	public static Mesh createQuad() {
	    float vertices[] = {
	            // positions         // texCoords
	            -1.0f,  1.0f, 0.0f,  0.0f, 1.0f,
	            -1.0f, -1.0f, 0.0f,  0.0f, 0.0f,
	             1.0f, -1.0f, 0.0f,  1.0f, 0.0f,

	            -1.0f,  1.0f, 0.0f,  0.0f, 1.0f,
	             1.0f, -1.0f, 0.0f,  1.0f, 0.0f,
	             1.0f,  1.0f, 0.0f,  1.0f, 1.0f
	        };
	    Mesh mesh = new Mesh();
	    mesh.name = "Quad";
	    mesh.setupMesh(vertices, null, false, false);
	    return mesh;
	}
	

	public static Mesh createQuadStrip() {
	    float vertices[] = {
	            // positions        // texture Coords
	            -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
	            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
	             1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
	             1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
	        };
	    Mesh mesh = new Mesh();
	    mesh.name = "QuadStrip";
	    mesh.setupMesh(vertices, null, false, false);
	    mesh.renderMode = GL_TRIANGLE_STRIP;
	    return mesh;
	}
	
	public static Mesh createPlane(float size) {
	    float vertices[] = {
	             // positions        // texcoord   // normals       //tangent //bitangent
	             size, -0.5f,  size,  size, 0.0f,  0.0f, 1.0f, 0.0f,  0,0,0,    0,0,0,
	            -size, -0.5f,  size,  0.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0,0,0,    0,0,0,
	            -size, -0.5f, -size,  0.0f, size,  0.0f, 1.0f, 0.0f,  0,0,0,    0,0,0,

	             size, -0.5f,  size,  size, 0.0f,  0.0f, 1.0f, 0.0f,  0,0,0,    0,0,0,
	            -size, -0.5f, -size,  0.0f, size,  0.0f, 1.0f, 0.0f,  0,0,0,    0,0,0,
	             size, -0.5f, -size,  size, size,  0.0f, 1.0f, 0.0f,  0,0,0,    0,0,0,

	        };
	    calcTangents(vertices);

	    Mesh mesh = new Mesh();
	    mesh.name = "Plane";
	    mesh.setupMesh(vertices, null, true, true);
	    return mesh;
	}
	

	public static void calcTangents(float[] vertices) {
		int vertexSize = 14;
		int numVertices = vertices.length / vertexSize;
				
		for(int i=0; i<numVertices; i+=3) {
			calcTangents(vertices, i*vertexSize, (i+1)*vertexSize, (i+2)*vertexSize);
		}
	}
	
	public static void calcTangents(float[] vertices, int idx1, int idx2, int idx3) {
		final var tangent = vec3();
		final var bitangent = vec3();
		final var pos1 = vec3(vertices[idx1 + 0], vertices[idx1 + 1], vertices[idx1 + 2]);
		final var uv1 = vec2(vertices[idx1 + 3], vertices[idx1 + 4]);
		final var pos2 = vec3(vertices[idx2 + 0], vertices[idx2 + 1], vertices[idx2 + 2]);
		final var uv2 = vec2(vertices[idx2 + 3], vertices[idx2 + 4]);
		final var pos3 = vec3(vertices[idx3 + 0], vertices[idx3 + 1], vertices[idx3 + 2]);
		final var uv3 = vec2(vertices[idx3 + 3], vertices[idx3 + 4]);

		//GeometryUtils.tangentBitangent(pos1, uv1, pos2, uv2, pos3, uv3, tangent, bitangent);
		tangents(pos1, uv1, pos2, uv2, pos3, uv3, tangent, bitangent);
		
		vertices[idx1 +  8] = tangent.x;
		vertices[idx1 +  9] = tangent.y;
		vertices[idx1 + 10] = tangent.z;
		vertices[idx1 + 11] = bitangent.x;
		vertices[idx1 + 12] = bitangent.y;
		vertices[idx1 + 13] = bitangent.z;
		
		vertices[idx2 +  8] = tangent.x;
		vertices[idx2 +  9] = tangent.y;
		vertices[idx2 + 10] = tangent.z;
		vertices[idx2 + 11] = bitangent.x;
		vertices[idx2 + 12] = bitangent.y;
		vertices[idx2 + 13] = bitangent.z;
		
		vertices[idx3 +  8] = tangent.x;
		vertices[idx3 +  9] = tangent.y;
		vertices[idx3 + 10] = tangent.z;
		vertices[idx3 + 11] = bitangent.x;
		vertices[idx3 + 12] = bitangent.y;
		vertices[idx3 + 13] = bitangent.z;
	}
	
	private static float[] createTangentVerticesQuad() {
        final var pos1 = vec3(-1.0f,  1.0f, 0.0f);
        final var pos2 = vec3(-1.0f, -1.0f, 0.0f);
        final var pos3 = vec3( 1.0f, -1.0f, 0.0f);
        final var pos4 = vec3( 1.0f,  1.0f, 0.0f);
        // texture coordinates
        final var uv1 = vec2(0.0f, 1.0f);
        final var uv2 = vec2(0.0f, 0.0f);
        final var uv3 = vec2(1.0f, 0.0f);
        final var uv4 = vec2(1.0f, 1.0f);

        final var tangent1 = vec3(0);
        final var bitangent1 = vec3(0);
        final var tangent2 = vec3(0);
        final var bitangent2 = vec3(0);
        
        final var nm = vec3(0, 0, 1);

		tangents(pos1, uv1, pos2, uv2, pos3, uv3, tangent1, bitangent1);
		tangents(pos1, uv1, pos3, uv3, pos4, uv4, tangent2, bitangent2);
		
		// ---------- wrong results??
		//GeometryUtils.tangentBitangent(pos1, uv1, pos2, uv2, pos3, uv3, tangent1, bitangent1);
		//GeometryUtils.tangentBitangent(pos1, uv1, pos3, uv3, pos4, uv4, tangent2, bitangent2);
		
        final float vertices[] = {
            // positions            // normal         // texcoords  // tangent                          // bitangent
            pos1.x, pos1.y, pos1.z, nm.x, nm.y, nm.z, uv1.x, uv1.y, tangent1.x, tangent1.y, tangent1.z, bitangent1.x, bitangent1.y, bitangent1.z,
            pos2.x, pos2.y, pos2.z, nm.x, nm.y, nm.z, uv2.x, uv2.y, tangent1.x, tangent1.y, tangent1.z, bitangent1.x, bitangent1.y, bitangent1.z,
            pos3.x, pos3.y, pos3.z, nm.x, nm.y, nm.z, uv3.x, uv3.y, tangent1.x, tangent1.y, tangent1.z, bitangent1.x, bitangent1.y, bitangent1.z,

            pos1.x, pos1.y, pos1.z, nm.x, nm.y, nm.z, uv1.x, uv1.y, tangent2.x, tangent2.y, tangent2.z, bitangent2.x, bitangent2.y, bitangent2.z,
            pos3.x, pos3.y, pos3.z, nm.x, nm.y, nm.z, uv3.x, uv3.y, tangent2.x, tangent2.y, tangent2.z, bitangent2.x, bitangent2.y, bitangent2.z,
            pos4.x, pos4.y, pos4.z, nm.x, nm.y, nm.z, uv4.x, uv4.y, tangent2.x, tangent2.y, tangent2.z, bitangent2.x, bitangent2.y, bitangent2.z
        };
        
        return vertices;
	}
	
	public static Mesh createQuadTangents() {
		float[] vertices = createTangentVerticesQuad();
        
	    Mesh mesh = new Mesh();
	    mesh.name = "QuadNTB";
	    mesh.setupMesh(vertices, null, true, true);
	    return mesh;
	}

	public static Mesh createSphere() {
        final int X_SEGMENTS = 64;
        final int Y_SEGMENTS = 64;
        final float PI = 3.14159265359f;

		float[] vertices = new float[(Y_SEGMENTS+1) * (X_SEGMENTS+1) * (3 + 2 + 3 + 3 + 3)];
        
        int index = 0;
        
        for (int y = 0; y <= Y_SEGMENTS; ++y)
        {
            for (int x = 0; x <= X_SEGMENTS; ++x)
            {
                float xSegment = (float)x / (float)X_SEGMENTS;
                float ySegment = (float)y / (float)Y_SEGMENTS;
                float xPos = Math.cos(xSegment * 2.0f * PI) * Math.sin(ySegment * PI);
                float yPos = Math.cos(ySegment * PI);
                float zPos = Math.sin(xSegment * 2.0f * PI) * Math.sin(ySegment * PI);

                vertices[index++] = xPos;
                vertices[index++] = yPos;
                vertices[index++] = zPos;
                
                vertices[index++] = xSegment;
                vertices[index++] = ySegment;
                
                vertices[index++] = xPos;
                vertices[index++] = yPos;
                vertices[index++] = zPos;
                
                //tangents
                vertices[index++] = 0;
                vertices[index++] = 0;
                vertices[index++] = 0;
                
                //bitangents
                vertices[index++] = 0;
                vertices[index++] = 0;
                vertices[index++] = 0;
            }
        }

        int[] indices = new int[Y_SEGMENTS * (X_SEGMENTS+1) * 2];
        index = 0;
        
        
        boolean oddRow = false;
        for (int y = 0; y < Y_SEGMENTS; ++y)
        {
            if (!oddRow) // even rows: y == 0, y == 2; and so on
            {
                for (int x = 0; x <= X_SEGMENTS; ++x)
                {
                	indices[index++] = y       * (X_SEGMENTS + 1) + x;
                	indices[index++] = (y + 1) * (X_SEGMENTS + 1) + x;
                }
            }
            else
            {
                for (int x = X_SEGMENTS; x >= 0; --x)
                {
                	indices[index++] = (y + 1) * (X_SEGMENTS + 1) + x;
                	indices[index++] = y       * (X_SEGMENTS + 1) + x;
                }
            }
            oddRow = !oddRow;
        }
        
        System.out.println("index   = " + index);
        System.out.println("indices = " + indices.length);
        
        calcTangents(vertices);
        
	    Mesh mesh = new Mesh();
	    mesh.name = "Sphere";
	    mesh.renderMode = GL_TRIANGLE_STRIP;
	    mesh.setupMesh(vertices, indices, true, true);
		return mesh;
	}

}
