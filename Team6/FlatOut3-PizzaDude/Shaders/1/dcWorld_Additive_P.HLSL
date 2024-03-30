sampler2D Tex0;

struct PS_INPUT{
	float4 Diff				: COLOR0;
	float2 BaseTex			: TEXCOORD0;
};

struct PS_OUTPUT{
    float4 Color			: COLOR;
};

PS_OUTPUT main(const PS_INPUT Input){
	PS_OUTPUT Out;
	Out.Color = tex2D(Tex0, Input.BaseTex) * Input.Diff;
	return Out;
}