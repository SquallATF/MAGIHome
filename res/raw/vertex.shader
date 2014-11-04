uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uModel;
attribute vec4 aVertices;
attribute vec2 aTextureCoords;
varying vec2 vTextureCoords;

void main() {
	gl_Position = uProjection * uView * uModel * aVertices;
	vTextureCoords = aTextureCoords;
}
