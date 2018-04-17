package cn.ye2moe.moeye.rpc.protocol.serialize;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by Dempe on 2016/12/7.
 */
public enum SerializeType {

    Kryo((byte) 0), Protostuff((byte) 1);

    private byte value;

    SerializeType(byte value) {
        this.value = value;
    }

    public static SerializeType getSerializeTypeByName(String name) {
        for(SerializeType serializeType: SerializeType.values()){
            if(StringUtils.equals(serializeType.name(), name))
                return serializeType;
        }
        return Protostuff;
    }

    public static Serialization getSerializationByExtend(byte value) {
        switch (value & 0x3) {
            case 0x0:
                return new KryoSerialization();
            case 0x1:
                return new ProtostuffSerialization();
            case 0x2:
                //return new Hessian2Serialization();

            default:
                return new KryoSerialization();
        }
    }

    public byte getValue() {
        return value;
    }

    public final static SerializeType DEFAULT_SERIALIZE_TYPE = SerializeType.Kryo;
}
