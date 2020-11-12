package common;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11C.*;

import java.io.File;
import java.lang.Math;
import java.lang.ref.Cleaner;
import java.nio.*;
import java.nio.charset.StandardCharsets;

import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.assimp.AIFace.Buffer;

import common.model.*;
import common.opengl.Texture;

public class AssimpLoader {
	private static final boolean DEBUG_MATERIALS = true;
	private static final boolean DEBUG_MESHES = false;
	private static final boolean DEBUG_NODES = false;
	private static final boolean DEBUG_METADATA = false;
	private static final boolean RENAME_MESHES_TO_NODES = true;
	
	private AIScene scene;
	private Model model;
	private String modelPath;
	
	private AIColor4D aiColor = AIColor4D.calloc();
	private AIString aiString = AIString.calloc();
	static Cleaner cleaner = Cleaner.create();
	
	public AssimpLoader() {
		cleaner.register(this, this::clear);
	}
	
	private Texture loadTexture(String name) {
		System.out.println("LOAD TEXTURE NAME " + name);
		String parent = new File(modelPath).getParent();
		File textureFile = new File(parent, name);
		if(textureFile.exists()) {
			Texture tex = Assets.loadTexture(textureFile.getAbsolutePath(), true, false);
			tex.name = name;
			return tex;
		}
		return null;
	}
	
	public Model loadModel(String path) {
		this.modelPath = path;
		AIPropertyStore store = aiCreatePropertyStore();
		aiSetImportPropertyInteger(store, AI_CONFIG_PP_LBW_MAX_WEIGHTS, 4);
		aiSetImportPropertyInteger(store, AI_CONFIG_FBX_CONVERT_TO_M, 1);
		aiSetImportPropertyInteger(store, AI_CONFIG_IMPORT_FBX_PRESERVE_PIVOTS, 1);
		aiSetImportPropertyInteger(store, AI_CONFIG_IMPORT_FBX_OPTIMIZE_EMPTY_ANIMATION_CURVES, 0);
		
		int pFlags = 0
		        | aiProcess_FlipUVs
		        | aiProcess_JoinIdenticalVertices
		        | aiProcess_RemoveRedundantMaterials
		        | aiProcess_Triangulate
				| aiProcess_GenNormals
		        | aiProcess_GenUVCoords
				| aiProcess_CalcTangentSpace
		        | aiProcess_FindDegenerates
		        | aiProcess_SortByPType
		        | aiProcess_FindInvalidData
				| aiProcess_OptimizeMeshes
				| aiProcess_LimitBoneWeights
				| aiProcess_ImproveCacheLocality
				| aiProcess_PopulateArmatureData;
		
		scene = aiImportFileExWithProperties(path, pFlags, null, store);
		if(scene == null) {
			String message = aiGetErrorString();
			throw new RuntimeException("Failed to load " + message);
		}

		
		if(scene == null) {
			throw new RuntimeException("Postprocess failed: " + path);
		}

		model = new Model();
		
		// debug metadata
		if(DEBUG_METADATA) {
			AIMetaData mMetaData = scene.mMetaData();
			AIString.Buffer mKeys = mMetaData.mKeys();
			AIMetaDataEntry.Buffer mValues = mMetaData.mValues();
			
			while(mKeys.hasRemaining()) {
				String key = mKeys.get().dataString();
				AIMetaDataEntry aiMetaDataEntry = mValues.get();
				int type = aiMetaDataEntry.mType();
				ByteBuffer value = aiMetaDataEntry.mData(2048);
				System.out.println("[" + typeToName(type) + "] " + key + " = " + readType(type, value));
			}
		}

		if ((scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) != 0) {
			System.err.println("Model is INCOMPLETE!");
//			throw new RuntimeException("Failed loading model <" + path + "> flags: " + scene.mFlags());
		}

		loadMaterials();
		loadMeshes();
		loadNodes();
		
		aiReleasePropertyStore(store);
		aiReleaseImport(scene);
		
		Model mdl = model;
		
		model = null;
		scene = null;
		
		return mdl;
	}

	private Vector3f toVector(AIVector3D vec) {
		return new Vector3f(vec.x(), vec.y(), vec.z());
	}

	private Quaternionf toQuaternion(AIQuaternion aiQuat) {
        return new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
	}

	private void loadTextures() {
		int mNumTextures = scene.mNumTextures();
		if(mNumTextures > 0)
			System.err.println("Weird, this file has embedded textures: " + mNumTextures);
		// we don't need to load embedded textures
	}
	
	private void loadMaterials() {
		int mNumMaterials = scene.mNumMaterials();
		if(mNumMaterials==0)
			return;
		model.materials = new Material[mNumMaterials];
		PointerBuffer mMaterials = scene.mMaterials();

		int index = 0;
		while(mMaterials.hasRemaining()) {
			AIMaterial aiMaterial = AIMaterial.create(mMaterials.get());
			model.materials[index++] = processMaterial(aiMaterial);
		}
	}
	
	private void printProperties(AIMaterial aiMaterial) {
		int mNumProperties = aiMaterial.mNumProperties();
		PointerBuffer mProperties = aiMaterial.mProperties();
		System.out.println("Material keys:");
		for(int i=0; i<mNumProperties; i++) {
			AIMaterialProperty aiProperty = AIMaterialProperty.create(mProperties.get());
			String key = aiProperty.mKey().dataString();
			int b = aiProperty.mData().get() & 0xFF;
			System.out.println("\t" + key + " - type: " + aiProperty.mType() + " length: " + aiProperty.mDataLength() + " byte: " + String.format("0x%02X", b));
			
		}
	}
	
	private Material processMaterial(AIMaterial aiMaterial) {
		Material mat = new Material();
		
		//printProperties(aiMaterial);
		mat.name = "";
		
		mat.ambientColor  = loadMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT);
		mat.diffuseColor  = loadMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE);
		mat.specularColor = loadMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR);
		
		mat.opacity   = loadMaterialFloat(aiMaterial, AI_MATKEY_OPACITY, 1);
		mat.shininess = loadMaterialFloat(aiMaterial, AI_MATKEY_SHININESS, 30);
		
		mat.diffuseTexture  = loadMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, "texture_diffuse", true);
		mat.specularTexture = loadMaterialTexture(aiMaterial, aiTextureType_SPECULAR, "texture_specular", false);
        mat.normalTexture   = loadMaterialTexture(aiMaterial, aiTextureType_HEIGHT, "texture_normal", false);
        mat.emissiveTexture = loadMaterialTexture(aiMaterial, aiTextureType_EMISSIVE, "texture_emissive", false);
        
        System.out.println("Diffuse loaded texture " + mat.diffuseTexture);
        
		return mat;
	}
	
	
	private float loadMaterialFloat(AIMaterial aiMaterial, String key, float defaultValue) {
		float[] pOut = {0};
		int[] pMax = {1};
		int result = aiGetMaterialFloatArray(aiMaterial, key, 0, 0, pOut, pMax);
		if(DEBUG_MATERIALS) System.out.println("Property " + key + " = " + pOut[0]);
		return pOut[0];
	}

	private String loadMaterialString(AIMaterial aiMaterial, String key) {
		int result = aiGetMaterialString(aiMaterial, key, 0, 0, aiString);
		if(DEBUG_MATERIALS) System.out.println("Property " + key + " = " + aiString.dataString());
		return aiString.dataString();
	}
	
	private Vector3f loadMaterialColor(AIMaterial aiMaterial, String key) {
		int result = aiGetMaterialColor(aiMaterial, key, 0, 0, aiColor);
		if(result != 0) {
			if(DEBUG_MATERIALS) System.out.println("Not found property: " + key); 
			return null;
		}
		var color = new Vector3f(aiColor.r(), aiColor.g(), aiColor.g());
		if(DEBUG_MATERIALS) System.out.println("Property " + key + " = " + color);
		return color;
	}
	private Texture loadMaterialTexture(AIMaterial aiMaterial, int type, String typeName, boolean sRGB) {
		Texture[] arr = loadMaterialTextures(aiMaterial, type, typeName, sRGB);
		
		if(arr != null && arr.length>0)
			return arr[0];
		return null;
	}

	private Texture[] loadMaterialTextures(AIMaterial aiMaterial, int type, String typeName, boolean sRGB) {
		int textureCount = aiGetMaterialTextureCount(aiMaterial, type);
		Texture[] textures = new Texture[textureCount];
		
		if(DEBUG_MATERIALS && textureCount > 1)
			System.out.println("texture count: " + textureCount + " for type " + getTextureTypeName(type));

		AIString aiPath = AIString.calloc();
		
		for(int i=0; i<textureCount; i++) {
			int[] mapping = {0}, uvindex = {0}, op = {0}, mapmode = {0}, flags = {0};
			float[] blend = {1};
			
			aiGetMaterialTexture(aiMaterial, type, i, aiPath, mapping, uvindex, blend, op, mapmode, flags);
			
			textures[i] = loadTexture(aiPath.dataString());
			
			if(DEBUG_MATERIALS)
				System.out.println(textures[i]);
		}
		aiPath.close();
		return textures;
	}
	
	private void loadMeshes() {
		int mNumMeshes = scene.mNumMeshes();
		if(mNumMeshes==0)
			return;
		
		model.meshes = new Mesh[mNumMeshes];
		
		PointerBuffer mMeshes = scene.mMeshes();
		int index = 0;
		while(mMeshes.hasRemaining()) {
			AIMesh aiMesh = AIMesh.create(mMeshes.get());
			model.meshes[index] = processMesh(aiMesh, index);
			index++;
		}

		if(DEBUG_MESHES) {
			System.out.println("Loaded meshes: " + mNumMeshes);
		}
	}
	
	private boolean hasMeshIndex(AINode node, int index) {
		IntBuffer mMeshes = node.mMeshes();
		
		if(mMeshes == null)
			return false;
		
		while(mMeshes.hasRemaining()) {
			int idx = mMeshes.get();
			if(idx == index)
				return true;
		}
		return false;
	}
	
	private AINode findNodeForMesh(AINode parent, int index) {
		if(hasMeshIndex(parent,index)) {
			return parent;
		}
		
		PointerBuffer mChildren = parent.mChildren();
		if(mChildren == null)
			return null;
		
		while(mChildren.hasRemaining()) {
			AINode child = AINode.create(mChildren.get());
			AINode found = findNodeForMesh(child, index);
			if(found != null) {
				return found;
			}
		}
		return null;
	}
	
	private Mesh processMesh(AIMesh aiMesh, final int index) {
		String name = aiMesh.mName().dataString();
		var mVertices = aiMesh.mVertices();
		var mNormals = aiMesh.mNormals();
		var mNumTexCoords = aiMesh.mTextureCoords().remaining();
		var mTangents = aiMesh.mTangents();
		var mBitangents = aiMesh.mBitangents();
		
		int mPrimitiveTypes = aiMesh.mPrimitiveTypes();
		int glPrimitives = getGLPrimitiveType(mPrimitiveTypes);
		
		if(glPrimitives == -1) {
			throw new RuntimeException("Unsupported primitive type " + mPrimitiveTypes);
		}
		
		
		if(DEBUG_MESHES) System.out.println("num tex coords: " + mNumTexCoords);
		
		
		
//		aiMesh.
//		PointerBuffer mBones = aiMesh.mBones();
//		while(mBones.hasRemaining()) {
//			AIBone bone = AIBone.create(mBones.get());
//			AIVertexWeight.Buffer mWeights = bone.mWeights();
//		}
//		
//		int[] offsetBones = new int[aiMesh.mBones()];
		
		int mNumVertices = aiMesh.mNumVertices();
		int vertexSizeInFloats = (3+2+3+3+3);
		
		float[] vertices = new float[vertexSizeInFloats * mNumVertices];
		
		var mTextureCoords = aiMesh.mTextureCoords(0);
		
		for(int i=0; i<mNumVertices; i++) {
			int vertexIndex = i * vertexSizeInFloats;
			
			AIVector3D position = mVertices.get(i);
			vertices[vertexIndex + 0] = position.x();
			vertices[vertexIndex + 1] = position.y();
			vertices[vertexIndex + 2] = position.z();
			
			if(mTextureCoords != null) {
				AIVector3D texCoords = mTextureCoords.get(i);
				vertices[vertexIndex + 3] = texCoords.x();
				vertices[vertexIndex + 4] = texCoords.y();
			}
			if(mNormals != null) {
				var normal = mNormals.get(i);
				vertices[vertexIndex + 5] = normal.x();
				vertices[vertexIndex + 6] = normal.y();
				vertices[vertexIndex + 7] = normal.z();
			}
			if(mTangents != null) {
				var normal = mTangents.get(i);
				vertices[vertexIndex + 8] = normal.x();
				vertices[vertexIndex + 9] = normal.y();
				vertices[vertexIndex +10] = normal.z();
			}
			if(mBitangents != null) {
				var normal = mBitangents.get(i);
				vertices[vertexIndex +11] = normal.x();
				vertices[vertexIndex +12] = normal.y();
				vertices[vertexIndex +13] = normal.z();
			}
		}

		Buffer mFaces = aiMesh.mFaces();
		int mNumFaces = aiMesh.mNumFaces();
		int[] indices = new int[mNumFaces * 3];
		
		for(int i=0; i<mNumFaces; i++) {
			AIFace aiFace = mFaces.get(i);
			IntBuffer mIndices = aiFace.mIndices();
			
			indices[i * 3 + 0] = mIndices.get(0);
			indices[i * 3 + 1] = mIndices.get(1);
			indices[i * 3 + 2] = mIndices.get(2);
		}
		
		int mMaterialIndex = aiMesh.mMaterialIndex();
		
		if(DEBUG_MESHES) {
			System.out.print(name);
			System.out.print("[mat=" + mMaterialIndex+"]");
			System.out.print(" ");
		}

		Mesh mesh = new Mesh();
		mesh.name = name;
		mesh.materialIndex = mMaterialIndex;
		mesh.setupMesh(vertices, indices, true, true);
		return mesh;
	}

	private int getGLPrimitiveType(int mPrimitiveTypes) {
		switch(mPrimitiveTypes) {
		case Assimp.aiPrimitiveType_LINE:
			return GL_LINES;
		case Assimp.aiPrimitiveType_POINT:
			return GL_POINTS;
		case Assimp.aiPrimitiveType_TRIANGLE:
			return GL_TRIANGLES;
		case Assimp.aiPrimitiveType_POLYGON:
			return -1;
		}
		return -1;
	}

	private void loadNodes() {
		AINode mRootNode = scene.mRootNode();
		model.nodes = processNode(mRootNode);
//		if(DEBUG_NODES) {
//			model.nodes.print(0);
//		}
	}
	
	private Node processNode(AINode aiNode) {
		int mNumChildren = aiNode.mNumChildren();
		int mNumMeshes = aiNode.mNumMeshes();
		
		PointerBuffer mChildren = aiNode.mChildren();
		IntBuffer mMeshes = aiNode.mMeshes();
		AIMatrix4x4 mTransform = aiNode.mTransformation();
		
		Node node = new Node();
		node.name = aiNode.mName().dataString();
		node.transform.set(toMatrix(mTransform));
		
		if(DEBUG_NODES) {
			System.out.println(node.name);
			System.out.println(node.transform);
		}

		node.meshes = new int[mNumMeshes];
		for(int i=0; i<mNumMeshes; i++) {
			node.meshes[i] = mMeshes.get();
		}
		
		if(node.meshes.length == 1 && RENAME_MESHES_TO_NODES) {
			model.meshes[node.meshes[0]].name = node.name;
		}

		node.children = new Node[mNumChildren];
		for(int i=0; i<mNumChildren; i++) {
			node.children[i] = processNode(AINode.create(mChildren.get()));
		}
		
		return node;
	}
	
	public static Matrix4f toMatrix(AIMatrix4x4 am) {
		Matrix4f mat = new Matrix4f();
		mat.m00(am.a1());
		mat.m01(am.b1());
		mat.m02(am.c1());
		mat.m03(am.d1());

		mat.m10(am.a2());
		mat.m11(am.b2());
		mat.m12(am.c2());
		mat.m13(am.d2());

		mat.m20(am.a3());
		mat.m21(am.b3());
		mat.m22(am.c3());
		mat.m23(am.d3());

		mat.m30(am.a4());
		mat.m31(am.b4());
		mat.m32(am.c4());
		mat.m33(am.d4());
		return mat;
	}

	private String getTextureTypeName(int type) {
		switch(type) {
		case aiTextureType_AMBIENT:
			return "aiTextureType_AMBIENT";
		case aiTextureType_SPECULAR:
			return "aiTextureType_SPECULAR";
		case aiTextureType_HEIGHT:
			return "aiTextureType_HEIGHT";
		case aiTextureType_DIFFUSE:
			return "aiTextureType_DIFFUSE";
		}
		return "" + type;
	}
	
	private void clear() {
		aiString.close();
		aiColor.close();
	}
	
	private String typeToName(int type) {
		switch(type) {
		case AI_FLOAT:
			return "AI_FLOAT";
		case AI_INT32:
			return "AI_INT32";
		case AI_UINT64:
			return "AI_UINT64";
		case AI_AISTRING:
			return "AI_AISTRING";
		case AI_AIVECTOR3D:
			return "AI_AIVECTOR3D";
		case AI_DOUBLE:
			return "AI_DOUBLE";
		}
		return "["+type+"_UNKNOWN]";
	}
	private Object readType(int type, ByteBuffer buff) {
		switch(type) {
		case AI_FLOAT:
			return buff.getFloat();
		case AI_INT32:
			return buff.getInt();
		case AI_UINT64:
			return buff.getLong();
		case AI_AISTRING:
		{
			int len = buff.getInt();
			byte[] cbuf = new byte[Math.min(1020, len)];
			buff.get(cbuf);
			return new String(cbuf, StandardCharsets.UTF_8);
		}
		case AI_AIVECTOR3D:
			return "AI_AIVECTOR3D";
		case AI_DOUBLE:
			return buff.getDouble();
		}
		return "???";
	}
}
