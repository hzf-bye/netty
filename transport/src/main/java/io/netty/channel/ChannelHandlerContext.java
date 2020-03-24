/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.concurrent.EventExecutor;

import java.nio.channels.Channels;

/**
 * Enables a {@link ChannelHandler} to interact with its {@link ChannelPipeline}
 * and other handlers. Among other things a handler can notify the next {@link ChannelHandler} in the
 * {@link ChannelPipeline} as well as modify the {@link ChannelPipeline} it belongs to dynamically.
 *
 * <h3>Notify</h3>
 *
 * You can notify the closest handler in the same {@link ChannelPipeline} by calling one of the various methods
 * provided here.
 *
 * Please refer to {@link ChannelPipeline} to understand how an event flows.
 *
 * <h3>Modifying a pipeline</h3>
 *
 * You can get the {@link ChannelPipeline} your handler belongs to by calling
 * {@link #pipeline()}.  A non-trivial application could insert, remove, or
 * replace handlers in the pipeline dynamically at runtime.
 *
 * <h3>Retrieving for later use</h3>
 *
 * You can keep the {@link ChannelHandlerContext} for later use, such as
 * triggering an event outside the handler methods, even from a different thread.
 * <pre>
 * public class MyHandler extends {@link ChannelDuplexHandler} {
 *
 *     <b>private {@link ChannelHandlerContext} ctx;</b>
 *
 *     public void beforeAdd({@link ChannelHandlerContext} ctx) {
 *         <b>this.ctx = ctx;</b>
 *     }
 *
 *     public void login(String username, password) {
 *         ctx.write(new LoginMessage(username, password));
 *     }
 *     ...
 * }
 * </pre>
 *
 * <h3>Storing stateful information</h3>
 *
 * {@link #attr(AttributeKey)} allow you to
 * store and access stateful information that is related with a handler and its
 * context.  Please refer to {@link ChannelHandler} to learn various recommended
 * ways to manage stateful information.
 *
 * <h3>A handler can have more than one context</h3>
 *
 * Please note that a {@link ChannelHandler} instance can be added to more than
 * one {@link ChannelPipeline}.  It means a single {@link ChannelHandler}
 * instance can have more than one {@link ChannelHandlerContext} and therefore
 * the single instance can be invoked with different
 * {@link ChannelHandlerContext}s if it is added to one or more
 * {@link ChannelPipeline}s more than once.
 * <p>
 * For example, the following handler will have as many independent {@link AttributeKey}s
 * as how many times it is added to pipelines, regardless if it is added to the
 * same pipeline multiple times or added to different pipelines multiple times:
 * <pre>
 * public class FactorialHandler extends {@link ChannelInboundHandlerAdapter} {
 *
 *   private final {@link AttributeKey}&lt;{@link Integer}&gt; counter = {@link AttributeKey}.valueOf("counter");
 *
 *   // This handler will receive a sequence of increasing integers starting
 *   // from 1.
 *   {@code @Override}
 *   public void channelRead({@link ChannelHandlerContext} ctx, Object msg) {
 *     Integer a = ctx.attr(counter).get();
 *
 *     if (a == null) {
 *       a = 1;
 *     }
 *
 *     attr.set(a * (Integer) msg);
 *   }
 * }
 *
 * // Different context objects are given to "f1", "f2", "f3", and "f4" even if
 * // they refer to the same handler instance.  Because the FactorialHandler
 * // stores its state in a context object (using an {@link AttributeKey}), the factorial is
 * // calculated correctly 4 times once the two pipelines (p1 and p2) are active.
 * FactorialHandler fh = new FactorialHandler();
 *
 * {@link ChannelPipeline} p1 = {@link Channels}.pipeline();
 * p1.addLast("f1", fh);
 * p1.addLast("f2", fh);
 *
 * {@link ChannelPipeline} p2 = {@link Channels}.pipeline();
 * p2.addLast("f3", fh);
 * p2.addLast("f4", fh);
 * </pre>
 *
 * <h3>Additional resources worth reading</h3>
 * <p>
 * Please refer to the {@link ChannelHandler}, and
 * {@link ChannelPipeline} to find out more about inbound and outbound operations,
 * what fundamental differences they have, how they flow in a  pipeline,  and how to handle
 * the operation in your application.
 *
 * ChannelHandlerContext代表了ChannelHandler和ChannelPipeline之间的关联，每当有ChannelHandler添加到ChannelPipeline中时，
 * 都会创建ChannelHandlerContext。ChannelHandlerContext的主要功能是管理它所关联的ChannelHandler和在同一个ChannelPipeline
 * 中的其它ChannelHandler之间的交互。
 * ChannelHandlerContext有很多方法，其中一些方法也存在与Channel和ChannelPipeline中，但是有一点重要的不同。
 * 如果调用Channel或者ChannelPipeline上的这些方法，它们将沿着整个ChannelPipeline进行传播。
 * 而调用ChannelHandlerContext上的相同方法，则将从当前关联的ChannelHandler开始，并且只会传播给位于该ChannelPipeline
 * 中的下一个 能够处理该事件的ChannelHandler。
 */
public interface ChannelHandlerContext extends AttributeMap, ChannelInboundInvoker, ChannelOutboundInvoker {

    /**
     * 返回绑定到这个实例的Channel
     * Return the {@link Channel} which is bound to the {@link ChannelHandlerContext}.
     */
    Channel channel();

    /**
     * Returns the {@link EventExecutor} which is used to execute an arbitrary task.
     */
    EventExecutor executor();

    /**
     * 返回这个实例的唯一名称
     * The unique name of the {@link ChannelHandlerContext}.The name was used when then {@link ChannelHandler}
     * was added to the {@link ChannelPipeline}. This name can also be used to access the registered
     * {@link ChannelHandler} from the {@link ChannelPipeline}.
     */
    String name();

    /**
     * 返回绑定到这个实例的ChannelHandler
     * The {@link ChannelHandler} that is bound this {@link ChannelHandlerContext}.
     */
    ChannelHandler handler();

    /**
     * 如果所关联的ChannelHandler已经从ChannelPipeline中移除，则返回true
     * Return {@code true} if the {@link ChannelHandler} which belongs to this context was removed
     * from the {@link ChannelPipeline}. Note that this method is only meant to be called from with in the
     * {@link EventLoop}.
     */
    boolean isRemoved();

    /**
     * ChannelPipeline的入站操作
     * 调用ChannelPipeline中下一个ChannelInboundHandler的
     * {@link ChannelInboundHandler#channelRegistered(io.netty.channel.ChannelHandlerContext)}方法
     */
    @Override
    ChannelHandlerContext fireChannelRegistered();

    /**
     * ChannelPipeline的入站操作
     * 调用ChannelPipeline中下一个ChannelInboundHandler的
     * {@link ChannelInboundHandler#channelUnregistered(io.netty.channel.ChannelHandlerContext)}方法
     */
    @Override
    ChannelHandlerContext fireChannelUnregistered();

    /**
     * ChannelPipeline的入站操作
     * 调用ChannelPipeline中下一个ChannelInboundHandler的
     * {@link ChannelInboundHandler#channelActive(io.netty.channel.ChannelHandlerContext)}方法
     */
    @Override
    ChannelHandlerContext fireChannelActive();

    /**
     * ChannelPipeline的入站操作
     * 调用ChannelPipeline中下一个ChannelInboundHandler的
     * {@link ChannelInboundHandler#channelInactive(io.netty.channel.ChannelHandlerContext)}方法
     */
    @Override
    ChannelHandlerContext fireChannelInactive();

    /**
     * ChannelPipeline的入站操作
     * 调用ChannelPipeline中下一个ChannelInboundHandler的
     * {@link ChannelInboundHandler#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)}方法
     */
    @Override
    ChannelHandlerContext fireExceptionCaught(Throwable cause);

    /**
     * ChannelPipeline的入站操作
     * 调用ChannelPipeline中下一个ChannelInboundHandler的
     * {@link ChannelInboundHandler#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)}方法
     */
    @Override
    ChannelHandlerContext fireUserEventTriggered(Object evt);

    /**
     * ChannelPipeline的入站操作
     * 调用ChannelPipeline中下一个ChannelInboundHandler的
     * {@link ChannelInboundHandler#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)}方法
     */
    @Override
    ChannelHandlerContext fireChannelRead(Object msg);

    /**
     * ChannelPipeline的入站操作
     * 调用ChannelPipeline中下一个ChannelInboundHandler的
     * {@link ChannelInboundHandler#channelReadComplete(io.netty.channel.ChannelHandlerContext)}方法
     */
    @Override
    ChannelHandlerContext fireChannelReadComplete();

    /**
     * ChannelPipeline的入站操作
     * 调用ChannelPipeline中下一个ChannelInboundHandler的
     * {@link ChannelInboundHandler#channelWritabilityChanged(io.netty.channel.ChannelHandlerContext)}方法
     */
    @Override
    ChannelHandlerContext fireChannelWritabilityChanged();

    @Override
    ChannelHandlerContext read();

    @Override
    ChannelHandlerContext flush();

    /**
     * 返回这个实例关联的ChannelPipeline
     * Return the assigned {@link ChannelPipeline}
     */
    ChannelPipeline pipeline();

    /**
     * 返回和这个实例相关联的Channel所配置的ByteBufAllocator
     * Return the assigned {@link ByteBufAllocator} which will be used to allocate {@link ByteBuf}s.
     */
    ByteBufAllocator alloc();

    /**
     * @deprecated Use {@link Channel#attr(AttributeKey)}
     */
    @Deprecated
    @Override
    <T> Attribute<T> attr(AttributeKey<T> key);

    /**
     * @deprecated Use {@link Channel#hasAttr(AttributeKey)}
     */
    @Deprecated
    @Override
    <T> boolean hasAttr(AttributeKey<T> key);
}
