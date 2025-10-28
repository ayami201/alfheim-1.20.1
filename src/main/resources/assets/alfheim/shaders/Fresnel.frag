#version 120

uniform bool useFog;
uniform sampler2D bgl_RenderedTexture;
varying vec3 vertexCamPos;
varying vec3 fNormal;
varying vec2 texcoord;

vec4 addFog(vec4 oldColor) {
    float fogFactor = 1.0;

    if (useFog) {
        if (gl_Fog.density > 0.0)
            fogFactor = exp(-gl_Fog.density * gl_FogFragCoord);
        else
            fogFactor = (gl_Fog.end - gl_FogFragCoord) * gl_Fog.scale;
    }
    
    return mix(gl_Fog.color, oldColor, clamp(fogFactor, 0.0, 1.0));
}

void main() {
    vec4 texture = texture2D(bgl_RenderedTexture, texcoord);
    
    vec3 normal = normalize(gl_FrontFacing ? fNormal : fNormal * -1.0);
    vec3 eye = normalize(-vertexCamPos.xyz);
    float x = dot(normal, eye);
    float y = 1 - pow(1 - x, 8);
    float rim = smoothstep(0, 1, 1.0 - y);
    float value = clamp(rim, 0.0, 1.0);
    vec3 color = vec3(0.75, 1.0, 1.0);
    gl_FragColor = vec4((value * 1.25) * color, 1.0) + texture;
    
    gl_FragColor = addFog(gl_FragColor);
}