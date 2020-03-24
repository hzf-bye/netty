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

import java.net.SocketAddress;

/**
 * {@link ChannelHandler} which will get notified for IO-outbound-operations.
 * 处理出站数据以及各种状态变化
 * 一个强大的功能是可以按需推迟操作或者事件，这使得可以通过一些复杂的方法来处理请求。
 * 例如，如果到远程节点的写入被暂停了，那么你可以推迟冲刷操作并在稍后继续。
 */
public interface ChannelOutboundHandler extends ChannelHandler {
    /**
     * Called once a bind operation is made.
     * 当请求将Channel绑定到本地地址时将被调用
     *
     * ChannelPipeline出站操作
     * 这将调用ChannelPipeline中的下一个
     * {@link ChannelOutboundHandler#bind(io.netty.channel.ChannelHandlerContext, java.net.SocketAddress, io.netty.channel.ChannelPromise)}方法
     *
     * @param ctx           the {@link ChannelHandlerContext} for which the bind operation is made
     * @param localAddress  the {@link SocketAddress} to which it should bound
     * @param promise       the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception    thrown if an error occurs
     */
    void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception;

    /**
     * Called once a connect operation is made.
     * 当请求将Channel连接到远程节点时将被调用
     *
     * ChannelPipeline出站操作
     * 这将调用ChannelPipeline中的下一个
     * {@link ChannelOutboundHandler#connect(io.netty.channel.ChannelHandlerContext, java.net.SocketAddress, java.net.SocketAddress, io.netty.channel.ChannelPromise)}方法
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the connect operation is made
     * @param remoteAddress     the {@link SocketAddress} to which it should connect
     * @param localAddress      the {@link SocketAddress} which is used as source on connect
     * @param promise           the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception        thrown if an error occurs
     */
    void connect(
            ChannelHandlerContext ctx, SocketAddress remoteAddress,
            SocketAddress localAddress, ChannelPromise promise) throws Exception;

    /**
     * Called once a disconnect operation is made.
     * 当请求将Channel从远程节点断开时时将被调用
     *
     * ChannelPipeline出站操作
     * 这将调用ChannelPipeline中的下一个
     * {@link ChannelOutboundHandler#disconnect(io.netty.channel.ChannelHandlerContext, io.netty.channel.ChannelPromise)}方法
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the disconnect operation is made
     * @param promise           the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception        thrown if an error occurs
     */
    void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;

    /**
     * Called once a close operation is made.
     * 当请求关闭Channel时将被调用
     *
     * ChannelPipeline出站操作
     * 这将调用ChannelPipeline中的下一个
     * {@link ChannelOutboundHandler#close(io.netty.channel.ChannelHandlerContext, io.netty.channel.ChannelPromise)}方法
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the close operation is made
     * @param promise           the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception        thrown if an error occurs
     */
    void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;

    /**
     * Called once a deregister operation is made from the current registered {@link EventLoop}.
     * 将Channel从之前分配的EventExecutor（即EventLoop）中注销
     *
     * ChannelPipeline出站操作
     * 这将调用ChannelPipeline中的下一个
     * {@link ChannelOutboundHandler#deregister(io.netty.channel.ChannelHandlerContext, io.netty.channel.ChannelPromise)}方法
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the close operation is made
     * @param promise           the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception        thrown if an error occurs
     */
    void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;

    /**
     * Intercepts {@link ChannelHandlerContext#read()}.
     * 当请求从Channel读取更多的数据时将被调用
     *
     * ChannelPipeline出站操作
     * 这将调用ChannelPipeline中的下一个
     * {@link ChannelOutboundHandler#read(io.netty.channel.ChannelHandlerContext)}方法
     *
     */
    void read(ChannelHandlerContext ctx) throws Exception;

    /**
    * Called once a write operation is made. The write operation will write the messages through the
     * {@link ChannelPipeline}. Those are then ready to be flushed to the actual {@link Channel} once
     * {@link Channel#flush()} is called
     * 当请求通过Channel将入队数据冲刷到远程节点时将被调用
     * 注意：这并不会将消息写入底层socket,，而只会将它写入队列中。
     * 要将它写入底层的socket，需要调用flush()或者writeAndFlush()
     *
     * ChannelPipeline出站操作
     * 这将调用ChannelPipeline中的下一个
     * {@link ChannelOutboundHandler#write(io.netty.channel.ChannelHandlerContext, java.lang.Object, io.netty.channel.ChannelPromise)}方法
     *
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the write operation is made
     * @param msg               the message to write
     * @param promise           the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception        thrown if an error occurs
     */
    void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception;

    /**
     * 冲刷Channel所有挂起的写入。
     *
     * ChannelPipeline出站操作
     * 这将调用ChannelPipeline中的下一个
     * {@link ChannelOutboundHandler#flush(io.netty.channel.ChannelHandlerContext)}方法
     * Called once a flush operation is made. The flush operation will try to flush out all previous written messages
     * that are pending.
     * 当请求通过Channel将入队数据写到远程节点时将被调用
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the flush operation is made
     * @throws Exception        thrown if an error occurs
     */
    void flush(ChannelHandlerContext ctx) throws Exception;
}
