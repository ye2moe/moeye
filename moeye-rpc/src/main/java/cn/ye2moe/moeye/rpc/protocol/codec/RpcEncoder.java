package cn.ye2moe.moeye.rpc.protocol.codec;

import cn.ye2moe.moeye.core.protocol.Header;
import cn.ye2moe.moeye.core.protocol.Message;
import cn.ye2moe.moeye.rpc.protocol.compress.Compress;
import cn.ye2moe.moeye.rpc.protocol.compress.CompressType;
import cn.ye2moe.moeye.rpc.protocol.serialize.Serialization;
import cn.ye2moe.moeye.rpc.protocol.serialize.SerializeType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC Encoder
 * @author ye2moe
 */
public class RpcEncoder extends MessageToByteEncoder<Message> {

    @Override
    public void encode(ChannelHandlerContext ctx, Message in, ByteBuf out) throws Exception {
        Header header = in.getHeader();
        out.writeShort(header.getMagic());
        out.writeByte(header.getVersion());
        out.writeByte(header.getExtend());
        out.writeLong(header.getMessageID());
        Object content = in.getContent();

        if(content == null){
            out.writeInt(0);
            return;
        }

        Serialization serialization = SerializeType.getSerializationByExtend(header.getExtend());

        Compress compress = CompressType.getCompressTypeByValueByExtend(header.getExtend());

        byte[] payload = compress.compress(serialization.serialize(content));

        out.writeInt(payload.length);
        out.writeBytes(payload);


        /*
        if (genericClass.isInstance(in)) {
            byte[] data = null;//SerializationUtil.serialize(in);
            //byte[] data = JsonUtil.serialize(in); // Not use this, have some bugs
            out.writeInt(data.length);
            out.writeBytes(data);
        }*/
    }
}
