package cn.ye2moe.moeye.rpc.protocol.codec;

import cn.ye2moe.moeye.core.protocol.Header;
import cn.ye2moe.moeye.core.protocol.Message;
import cn.ye2moe.moeye.rpc.protocol.MessageType;
import cn.ye2moe.moeye.rpc.protocol.ProtocolConstants;
import cn.ye2moe.moeye.rpc.protocol.RpcResponse;
import cn.ye2moe.moeye.rpc.protocol.compress.Compress;
import cn.ye2moe.moeye.rpc.protocol.compress.CompressType;
import cn.ye2moe.moeye.rpc.protocol.serialize.Serialization;
import cn.ye2moe.moeye.rpc.protocol.serialize.SerializeType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * RPC Decoder
 * @author ye2moe
 */
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < ProtocolConstants.HEADER_SIZE) {
            return;
        }
        in.markReaderIndex();

        short magic = in.readShort();
        byte version = in.readByte();
        byte extend = in.readByte();
        long messageId = in.readLong();
        int size = in.readInt();
        Object response = null;
        if(size != 0 ){
            if(in.readableBytes() <size){
                in.resetReaderIndex();

                return;
            }
            byte[] payload = new byte[size];
            in.readBytes(payload);

            Serialization serialization = SerializeType.getSerializationByExtend(extend);
            Compress compress = CompressType.getCompressTypeByValueByExtend(extend);
            byte [] unCompress = compress.unCompress(payload);
            response = serialization.deserialize(unCompress, MessageType.getMessageTypeByExtend(extend));
        }
        Header header = new Header(magic,version,extend,messageId,size);

        Message message = new Message(header,response);

        out.add(message);
/*
        int dataLength = in.readInt();
        /*if (dataLength <= 0) {
            ctx.close();
        }*//*
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = SerializationUtil.deserialize(data, genericClass);
        //Object obj = JsonUtil.deserialize(data,genericClass); // Not use this, have some bugs
        out.add(obj);
        */
    }

}
