# learnopengl.com in Java
LWJGL examples of learnopengl.com

### Notes

I ommited some of the examples because of repetitiveness/redundancy. I usually focus on the last example/chapter that has all previous combined.

### Vertex Attribute Order

learnopengl.com uses this vetex order:

    Position, Normals, UV, Tangent, Bitangent

However I tend to use ordering where I group normals and tangents together:

    Position, UV, Normal, Tangent, Bitangent

This is reflected in model loaders and shapes builders and most shaders.