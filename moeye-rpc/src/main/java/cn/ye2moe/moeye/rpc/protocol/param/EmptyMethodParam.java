package cn.ye2moe.moeye.rpc.protocol.param;

public final class EmptyMethodParam implements MethodParam {

	private static final EmptyMethodParam EMPTY = new EmptyMethodParam();

	public static final EmptyMethodParam empty() {
		return EMPTY;
	}
}
