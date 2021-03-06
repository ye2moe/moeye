package cn.ye2moe.moeye.core.protocol;


/**
 * 0-15	16-23	24-31	32-95	96-127
 * magic	version	extend flag	request id	body content length
 * 魔数	协议版本	24-28	29-30	31
 * 消息id
 * <p/>
 * body包长
 * 保留	event( 可支持4种event，
 * 如normal, exception等)
 * <p/>
 * @author : Dempe
 * : 2016/11/25
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
public class Header {

    private short magic;// 魔数
    private byte version; // 协议版本

    /**
     * 扩展字段[
     * 0-1 => 序列化方式，
     * 2 => 参数类型  0 标准String ， 1  使用MethodParam对参封装
     * 3-4 => 压缩方式: 0 不压缩, 1 GZIP，2 使用Snappy
     * 5-6 => (event(可支持4种event， 如normal,heartbeat, exception等))
     * 7 => 0:request,1:response]
     */
    private byte extend;
    private Long messageID;// 消息id
    private Integer size;// 消息payload长度


    public Header() {
    }

    public Header(short magic, byte version) {
        this.magic = magic;
        this.version = version;
    }

    public Header(short magic, byte version, byte extend) {
        this.magic = magic;
        this.version = version;
        this.extend = extend;
    }

    public Header(short magic, byte version, byte extend, Long messageID, Integer size) {
        this.magic = magic;
        this.version = version;
        this.extend = extend;
        this.messageID = messageID;
        this.size = size;
    }

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public Byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }

    public Byte getExtend() {
        return extend;
    }

    public void setExtend(byte extend) {
        this.extend = extend;
    }

    public void setExtend(Byte extend) {
        this.extend = extend;
    }

    public Long getMessageID() {
        return messageID;
    }

    public void setMessageID(Long messageID) {
        this.messageID = messageID;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }


    @Override
    public String toString() {
        return "Header{" +
                "extend=" + extend +
                ", messageID=" + messageID +
                ", size=" + size +
                '}';
    }
}

