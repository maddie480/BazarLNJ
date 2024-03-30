// transformations
float4x4 World : register(c0);
float4x4 View : register(c4);
float4x4 Proj : register(c8);

float AlphaFader;
float FlameTimer;

struct VS_OUTPUT{
    float4 Pos				: POSITION;
    float4 Diffuse			: COLOR0;
	float2 Tex              : TEXCOORD0;
};

struct VS_INPUT{
    float3 Pos              : POSITION;
    float2 Tex              : TEXCOORD0;
};

VS_OUTPUT main(const VS_INPUT Input){
	float3 Pos = mul(float4(Input.Pos, 1), World);
	
	float Alpha = AlphaFader;
	if (FlameTimer != 0){
		float Wave = sin(FlameTimer * Input.Pos.z / 2);

		Pos.y += Wave * 2;
		Alpha *= 1.0f - (Wave * Wave);
	}

	Pos = mul(float4(Pos, 1), (float4x3)View);

	VS_OUTPUT Out;
    Out.Pos = mul(float4(Pos, 1), Proj);
	Out.Diffuse = float4(Alpha, Alpha, Alpha, 1);
	Out.Tex = Input.Tex;
    return Out;
}