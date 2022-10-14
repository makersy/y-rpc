package top.haoliny.yrpc.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.haoliny.yrpc.common.constants.Constants;
import top.haoliny.yrpc.common.serialize.Serialization;

import java.util.List;

/**
 * @author yhl
 * @date 2022/9/14
 * @description See message structure in {@link RpcEncoder}
 */

@Slf4j
@RequiredArgsConstructor
public class RpcDecoder extends ByteToMessageDecoder {

  /**
   * 序列化器
   */
  private final Serialization serialization;

  /**
   * 序列化对象类型
   */
  private final Class<?> clz;

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (in.readableBytes() < Constants.LENGTH_FIELD_LENGTH) {
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
    out.add(serialization.deserialize(data, clz));
  }
}
