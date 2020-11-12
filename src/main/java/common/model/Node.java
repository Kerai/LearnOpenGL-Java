package common.model;

import static common.GLM.*;

import java.util.*;
import java.util.function.Consumer;

import org.joml.Matrix4f;

public class Node {
	public String name;
	public final Matrix4f absoluteTransform = new Matrix4f();
	public final Matrix4f transform = new Matrix4f();
	public Node parent;
	public Node[] children;
	public int[] meshes;
	
	public void calcTransforms(Matrix4f parent) {
		absoluteTransform.set(mul(parent, transform));
		parent.mul(transform, absoluteTransform);
		//transform.mul(parent, absoluteTransform);
		for(var node : children) {
			node.calcTransforms(absoluteTransform);
			node.parent = this;
		}
	}
	
	public void forEach(Consumer<Node> func) {
		func.accept(this);
		for(Node c : children) {
			c.forEach(func);
		}
	}

	public void print(int depth) {
		if(children!=null && depth < 25) {
			System.out.println("+ ".repeat(depth) + "NodeData [" + name + ", childs=" + (countChildren(false)) + ", meshes="+ meshes.length +"]");
//			System.out.println(transform);
			for(Node child : children ) {
				child.print(depth+1);
			}
		} else {
			if(children!=null) {
				System.out.println("+ ".repeat(depth) + "NodeData [" + name + ", childs=+" + (countChildren(true)) + ", meshes="+ meshes.length +"]");
			}
		}
	}
	
	public int countChildren(boolean recursive) {
		if(children!=null) {
			int count = children.length;
			if(recursive) {
				for(var node : children) {
					count += node.countChildren(true);
				}
			}
			return count;
		}
		return 0;
	}
	
	public Node clone() {
		Node n = new Node();
		n.name = name;
		n.absoluteTransform.set(absoluteTransform);
		n.transform.set(transform);
		if(children != null) {
			n.children = new Node[children.length];
			for (int i = 0; i < children.length; i++) {
				n.children[i] = children[i].clone();
				n.children[i].parent = n;
			}
		}
		if(meshes != null) {
			n.meshes = Arrays.copyOf(meshes, meshes.length);
		}
		return n;
	}
}
