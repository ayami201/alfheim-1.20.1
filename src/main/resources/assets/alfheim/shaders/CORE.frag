#version 130

precision highp float;

uniform sampler2D bgl_RenderedTexture;

varying vec3 vertexCamPos;
varying vec3 fNormal;
varying vec2 texcoord;

#define PI 3.1415926535897932384626433832795

// alternated "Transperent Freshnel FrontFacing" shader
// http://shaderfrog.com/app/view/5001
void main() {
    vec4 c = texture2D(bgl_RenderedTexture, texcoord);
    
    vec3 normal = normalize(fNormal);
    vec3 eye = normalize(-vertexCamPos.xyz);
    float rim = smoothstep(0.0, 1.0, dot(normal, eye));
    float value = clamp(rim, 0.0, 1.0);
    value = -cos(value * 6.0 * PI) * 0.1 + 0.85;
    gl_FragColor = vec4(c.r * value, c.g, c.b, c.a + 0.5);
}