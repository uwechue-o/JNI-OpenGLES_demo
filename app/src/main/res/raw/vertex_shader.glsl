uniform mat4 uMVPMatrix;
attribute vec4 aColor;
attribute vec4 vPosition;
varying vec4 vColor;

void main() {
  vColor = aColor;
  gl_Position = uMVPMatrix * vPosition;
}