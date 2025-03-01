package org.example.server;



import org.example.register.Register;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public class RpcServer {
    private ThreadPoolExecutor threadPoolExecutor;
    private int port;
    private Register register;
    public RpcServer(int port, Register register,ThreadPoolExecutor threadPoolExecutor){
        this.register=register;
        this.port=port;
        this.threadPoolExecutor=threadPoolExecutor;
        CompletableFuture.runAsync(this::start,threadPoolExecutor);

    }

    public void start(){
        System.out.println("服务启动，监听端口: " + this.port);
        System.out.println("当前线程: " + Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getId());
        try (ServerSocket serverSocket=new ServerSocket(this.port)){
            Socket socket=null;
            while((socket=serverSocket.accept())!=null){
                System.out.println("收到客户端连接: " + socket.getInetAddress());
                CompletableFuture.runAsync(new ServerHandler(socket,register),threadPoolExecutor);
            }
        }
        catch (Exception e){
            System.err.println("启动 ServerSocketChannel 时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void start2() {
        System.out.println("服务启动，监听端口: " + this.port);
        System.out.println("当前线程: " + Thread.currentThread().getName());

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.socket().bind(new InetSocketAddress(this.port));
            serverSocketChannel.configureBlocking(false);

            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    System.out.println("收到客户端连接: " + socketChannel.socket().getInetAddress());
                    CompletableFuture.runAsync(new ServerHandler(socketChannel.socket(), register), threadPoolExecutor);
                }
            }
        } catch (IOException e) {
            System.err.println("启动 ServerSocketChannel 时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
