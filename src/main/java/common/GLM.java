package common;

import org.joml.*;


/** a class to make certain calls more GLM like */
public interface GLM {
	
	public static Vector2f vec2(float x, float y) {
		return new Vector2f(x, y);
	}
	
	public static Vector3f vec3(float x, float y, float z) {
		return new Vector3f(x, y, z);
	}
	
	public static Vector3f vec3(float v) {
		return new Vector3f(v);
	}
	
	public static Vector3f vec3() {
		return new Vector3f();
	}
	
	public static Vector3f subtract(Vector3f a, Vector3f b) {
		return a.sub(b, new Vector3f());
	}
	
	public static Vector2f subtract(Vector2f a, Vector2f b) {
		return a.sub(b, new Vector2f());
	}

	
	static Vector3f cross(Vector3f a, Vector3f b) {
		return a.cross(b, new Vector3f());
	}
	
	public static Vector3f normal(Vector3f p1, Vector3f p2, Vector3f p3) {
		var d1 = subtract(p2, p1);
		var d2 = subtract(p3, p1);
		return cross(d1, d2).normalize();
	}
	
	public static Matrix4f mul(Matrix4f a, Matrix4f b) {
		return a.mul(b, new Matrix4f());
	}
	
	static Matrix4f mat4(float scale) {
		return new Matrix4f().scale(scale);
	}

	public static void tangents(Vector3f pos1, Vector2f uv1, Vector3f pos2, Vector2f uv2, Vector3f pos3, Vector2f uv3, Vector3f tangent, Vector3f bitangent) {
        final var edge1 = subtract(pos2, pos1);
        final var edge2 = subtract(pos3, pos1);
        final var deltaUV1 = subtract(uv2, uv1);
        final var deltaUV2 = subtract(uv3, uv1);

        float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

        tangent.x = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x);
        tangent.y = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y);
        tangent.z = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z);
        tangent.normalize();

        bitangent.x = f * (-deltaUV2.x * edge1.x + deltaUV1.x * edge2.x);
        bitangent.y = f * (-deltaUV2.x * edge1.y + deltaUV1.x * edge2.y);
        bitangent.z = f * (-deltaUV2.x * edge1.z + deltaUV1.x * edge2.z);
        bitangent.normalize();
	}
}
