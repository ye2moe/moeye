package cn.ye2moe.moeye.rpc.protocol;



public enum MessageType {

    REQUEST((byte) 0), RESPONSE((byte) 1);

    private byte value;

    MessageType(byte value) {
        this.value = value;
    }

    public static Class getMessageTypeByExtend(byte value) {
        switch (value & RESPONSE_MESSAGE_TYPE) {
            case 0x0:
                return RpcRequest.class;
            case RESPONSE_MESSAGE_TYPE:
                return RpcResponse.class;
            default:
                return RpcRequest.class;
        }

    }

    public byte getValue() {
        return value;
    }

    public final static byte RESPONSE_MESSAGE_TYPE = (byte) (1 << 7);


}
