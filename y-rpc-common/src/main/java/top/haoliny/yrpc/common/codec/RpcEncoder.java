package top.haoliny.yrpc.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.haoliny.yrpc.common.serialize.Serialization;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author yhl
 * @date 2022/9/14
 * @description
 *
 * <h3>Message Structure</h3>
 * <pre>
 *  +----------------+-----------------------+
 *  |  Data Length   |          数据          |
 *  +----------------+-----------------------+
 *  |    4 Bytes     |  "Data Length" Bytes  |
 *  +----------------------------------------+
 * </pre>
 */

@Slf4j
@RequiredArgsConstructor
public class RpcEncoder extends MessageToByteEncoder {

  // 序列化器
  private final Serialization serialization;

  // 序列化对象类型
  private final Class<?> clz;

  @Override
  protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
    try {
      checkNotNull(clz, "clz == nil");

      // 序列化
      byte[] bytes = serialization.serialize(msg);
      if (bytes != null) {
        // 写入数据长度
        out.writeInt(bytes.length);
        // 写入数据
        out.writeBytes(bytes);
      }
    } catch (Exception e) {
      log.error("RpcEncoder.encode catch error", e);
    }
  }
}
