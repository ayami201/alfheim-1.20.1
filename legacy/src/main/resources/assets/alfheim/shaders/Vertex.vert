#version 120

varying vec3 fNormal;
varying vec3 vertexLocalPos;
varying vec3 vertexCamPos;
varying vec2 texcoord;

void main() {
    fNormal = normalize(gl_NormalMatrix * gl_Normal);
    vec4 pos = gl_ModelViewMatrix * vec4(gl_Vertex.xyz, 1.0);

    vertexCamPos = pos.xyz;

    vertexLocalPos = gl_Vertex.xyz;

    texcoord = vec2(gl_MultiTexCoord0);

    gl_Position = gl_ProjectionMatrix * pos;
    gl_FrontColor = gl_Color;
    gl_FogFragCoord = abs(pos.z);
}