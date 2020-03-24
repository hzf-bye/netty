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

/**
 * {@link ChannelHandler} which adds callbacks for state changes. This allows the user
 * to hook in to state changes easily.
 * 处理入站数据以及各种状态变化
 */
public interface ChannelInboundHandler extends ChannelHandler {

    /**
     * 当channel已经注册到它的EventLoop并且能够处理I/O时被调用
     * The {@link Channel} of the {@link ChannelHandlerContext} was registered with its {@link EventLoop}
     */
    void channelRegistered(ChannelHandlerContext ctx) throws Exception;

    /**
     * 当channel从它的EventLoop注销并且无法处理任何I/O时被调用
     * The {@link Channel} of the {@link ChannelHandlerContext} was unregistered from its {@link EventLoop}
     */
    void channelUnregistered(ChannelHandlerContext ctx) throws Exception;

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} is now active
     * 客户端在到服务器的连接已经被建立后将被调用
     * 或者
     * 服务器来自于客户端的连接已经被建立后将被调用
     * 也就是连接建立时，客户端与服务器的该方法都将被调用
     *
     * 当channel离开活动状态并且不再连接它的远程节点时被调用。
     *
     */
    void channelActive(ChannelHandlerContext ctx) throws Exception;

    /**
     * 当channel处于活动状态时被调用，channel已经连接/绑定并且已经就绪。
     * The {@link Channel} of the {@link ChannelHandlerContext} was registered is now inactive and reached its
     * end of lifetime.
     */
    void channelInactive(ChannelHandlerContext ctx) throws Exception;

    /**
     * Invoked when the current {@link Channel} has read a message from the peer.
     * 对于每个传入的消息都要调用
     */
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

    /**
     * Invoked when the last message read by the current read operation has been consumed by
     * {@link #channelRead(ChannelHandlerContext, Object)}.  If {@link ChannelOption#AUTO_READ} is off, no further
     * attempt to read an inbound data from the current {@link Channel} will be made until
     * {@link ChannelHandlerContext#read()} is called.
     *  通知ChannelInboundHandler最后一次对channelRead的调用时当前批量读取中的最后一条消息
     *  当前最后一次channelRead方法被调用后会执行该方法
     *  当CHannel的上一个读操作完成是将被调用。
     *
     *  当所有可读的字节已经从channel中读取之后将会调用该回调方法；
     *  所以，可能在channelReadComplete之前看到多次调用channelRead
     */
    void channelReadComplete(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called if an user event was triggered.
     */
    void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;

    /**
     * Gets called once the writable state of a {@link Channel} changed. You can check the state with
     * {@link Channel#isWritable()}.
     */
    void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called if a {@link Throwable} was thrown.
     * 当处理过程中在ChannelPipeline中有错误产生时被调用
     * 在读取操作期间，有异常抛出时会调用
     * 比如在channelRead或者channelReadComplete或者channelActive等方法中出现异常
     */
    @Override
    @SuppressWarnings("deprecation")
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;
}
