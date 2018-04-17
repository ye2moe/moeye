package cn.ye2moe.moeye.rpc.protocol.compress;


import cn.ye2moe.moeye.rpc.protocol.serialize.SerializeType;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Dempe on 2016/12/7.
 */
public enum CompressType {

    //  00 01 10 11
    None((byte) 0), GZIP((byte) (1 << 3)), Snappy((byte) (1 << 4));

    private byte value;

    CompressType(byte value) {
        this.value = value;
    }

    public static CompressType getCompressTypeByName(String name) {
        for(CompressType compressType: CompressType.values()){
            if(StringUtils.equals(compressType.name(), name))
                return compressType;
        }
        return None;
    }

    public static Compress getCompressTypeByValueByExtend(byte extend) {
        switch (extend & 24) {
            case 0x0:
                return new NoCompress();
            case 1 << 3:
                return new GZipCompress();
            case 1 << 4:
                return new SnappyCompress();
            default:
                return new NoCompress();
        }
    }

    public byte getValue() {
        return value;
    }

    public final static CompressType DEFAULT_COMPRESS_TYPE = CompressType.None;

}
