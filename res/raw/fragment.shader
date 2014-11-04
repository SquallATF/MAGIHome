precision mediump float;
varying vec2 vTextureCoords;
uniform sampler2D sTexture;

void main() {
	vec4 color = texture2D(sTexture, vTextureCoords);
	if (color.a <= 0.2) {
		discard;
	}
	gl_FragColor = color;
}
