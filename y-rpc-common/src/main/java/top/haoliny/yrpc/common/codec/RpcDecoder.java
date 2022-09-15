package top.haoliny.yrpc.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.haoliny.yrpc.common.serialize.Serialization;

import java.util.List;

/**
 * @author yhl
 * @date 2022/9/14
 * @description
 * <pre>
 *       +---------------+-----------------------+
 *       |  Data Length  |          数据          |
 *       +---------------+-----------------------+
 *       |    4 Byte     |  "Data Length" Bytes  |
 *       +----------------------------------------
 * </pre>
 */

@Slf4j
@RequiredArgsConstructor
public class RpcDecoder<T> extends ByteToMessageDecoder {
  private final Serialization serialization;
  private final Class<?> serialClz;

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (in.readableBytes() < 4) {
      return;
    }
    in.markReaderIndex();

    // 读取数据包长度
    int dataLen = in.readInt();
    // 校验数据包长度是否正确
    if (in.readableBytes() < dataLen) {
      in.resetReaderIndex();
      log.warn("RpcDecoder.decode get error dataLength: {}, readable: {}", dataLen, in.readableBytes());
      return;
    }
    // 读取数据包
    byte[] data = new byte[dataLen];
    in.readBytes(data);
    // 反序列化
    Object obj = serialization.deserialize(data, serialClz);
    out.add(obj);
  }
}
