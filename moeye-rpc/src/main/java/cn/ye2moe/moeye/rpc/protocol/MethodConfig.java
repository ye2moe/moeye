package cn.ye2moe.moeye.rpc.protocol;


import cn.ye2moe.moeye.core.protocol.Header;
import cn.ye2moe.moeye.core.protocol.ProtoVersion;
import cn.ye2moe.moeye.rpc.protocol.compress.CompressType;
import cn.ye2moe.moeye.rpc.protocol.serialize.SerializeType;

/**
 * Created by Dempe on 2016/12/7.
 */
public class MethodConfig {

    private CompressType compressType;

    private SerializeType serializeType;

    private long timeout;

    private String serviceName;

    private String methodName;

    public CompressType getCompressType() {
        return compressType;
    }

    public void setCompressType(CompressType compressType) {
        this.compressType = compressType;
    }

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void loadDefaultValue() {
        setSerializeType(SerializeType.DEFAULT_SERIALIZE_TYPE);
        setCompressType(CompressType.DEFAULT_COMPRESS_TYPE);
        setTimeout(ProtocolConstants.DEFAULT_TIMEOUT);
    }


    public static class RpcHeaderMaker {
        RpcHeaderMaker() {
        }

        Header header;

        public static RpcHeaderMaker newMaker() {
            RpcHeaderMaker maker = new RpcHeaderMaker();
            maker.header = new Header(ProtocolConstants.MAGIC, ProtoVersion.VERSION_1.getVersion());
            return maker;
        }

        public Header make() {
            return header;
        }

        public RpcHeaderMaker loadWithMethodConfig(MethodConfig config) {
            header.setExtend(
                    (byte) (config.getSerializeType().getValue() | config.getCompressType().getValue())
                    );
            return this;
        }

        public RpcHeaderMaker withMessageId(long messageID) {
            header.setMessageID(messageID);
            return this;
        }
    }

    public static class Builder {

        private MethodConfig config;

        Builder() {
        }

        public static Builder newBuilder() {
            Builder builder = new Builder();
            builder.config = new MethodConfig();
            builder.config.loadDefaultValue();
            return builder;
        }

        public MethodConfig build() {
            return config;
        }

        public Builder withDefaultValue() {
            config.loadDefaultValue();
            return this;
        }

        public Builder withSerializeType(SerializeType serializeType) {
            if (serializeType != null) {
                config.setSerializeType(serializeType);
            }
            return this;
        }

        public Builder withCompressType(CompressType compressType) {
            if (compressType != null) {
                config.setCompressType(compressType);
            }
            return this;
        }

        public Builder withTimeout(int timeout) {
            if (timeout > 0) {
                config.setTimeout(timeout);
            }
            return this;
        }


    }
}
