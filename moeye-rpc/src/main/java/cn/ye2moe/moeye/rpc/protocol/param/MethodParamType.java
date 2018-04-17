package cn.ye2moe.moeye.rpc.protocol.param;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by Dempe on 2016/12/7.
 */
public enum MethodParamType {

    String((byte) 0), MethodParam((byte) 1);

    private byte value;

    MethodParamType(byte value) {
        this.value = value;
    }

    public static MethodParamType getMethodParamTypeByName(String name) {
        for(MethodParamType type: MethodParamType.values()){
            if(StringUtils.equals(type.name(), name))
                return type;
        }
        return String;
    }

    public static MethodParam getMethodParamByExtend(byte value) {
        switch (value & 0x4) {
            case 0x0:
                //return new KryoSerialization();
            case 0x1:
                //return new ProtostuffSerialization();
            default:
                //return new KryoSerialization();
        }
        return null;
    }

    public byte getValue() {
        return value;
    }

    public final static MethodParamType DEFAULT_METHODPARAM_TYPE = MethodParamType.String;
}
