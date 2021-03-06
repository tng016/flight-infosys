package com.czce4013.network;

import com.czce4013.entity.Response;
import com.czce4013.marshaller.Marshallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

public class UDPCommunicator {
    private DatagramSocket dSocket;
    private InetSocketAddress serverSocket;
    private static final Logger logger = LoggerFactory.getLogger(UDPCommunicator.class);

    public UDPCommunicator(InetSocketAddress serverSocket){
        try {
            dSocket = new DatagramSocket();
            this.serverSocket = serverSocket;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UDPCommunicator(int selfPortNumber){
        try {
            dSocket = new DatagramSocket(selfPortNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send (Marshallable data, InetSocketAddress dest){
        //logger.info("Send: {}", data);
        byte[] byteArray = data.marshall();
        DatagramPacket packet = new DatagramPacket(byteArray,byteArray.length, dest);

        try {
            this.dSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Response receive() {

        byte[] inputBuffer = new byte[1024];
        DatagramPacket p = new DatagramPacket(inputBuffer, inputBuffer.length);
        try {
            dSocket.receive(p);
            Response resp = new Response((InetSocketAddress) p.getSocketAddress(), Marshallable.unmarshall(p.getData()));
            //logger.info("Recv: {}", resp);
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // public Response receive() {
    //     final CompletableFuture<Response> future = new CompletableFuture<>();
    //     final CompletableFuture<Boolean> timeoutFuture = new CompletableFuture<>();
    //     this.receiveAsync(future::complete, timeoutFuture::complete,5, false);
    //     try {
    //         Response resp = future.get();
    //         logger.info("Recv: {}", resp);
    //         return resp;
    //     } catch (InterruptedException | ExecutionException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }

    // public void receive(Consumer<Response> callback, int timeout) {
    //     final CompletableFuture<Boolean> future = new CompletableFuture<>();
    //     this.receiveAsync(callback, future::complete ,timeout, true);
    //     try {
    //         future.get();
    //     } catch (InterruptedException | ExecutionException e) {
    //         e.printStackTrace();
    //     }
    // }

    // public void receiveAsync (Consumer<Response> callback, Consumer<Boolean> timeoutCall, int timeout, boolean listen){


    //     ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    //     final Future handler = executor.submit(() -> {
    //         try {
    //             do {
    //                 byte[] inputBuffer = new byte[1024];
    //                 DatagramPacket p = new DatagramPacket(inputBuffer, inputBuffer.length);
    //                 dSocket.receive(p);
    //                 callback.accept(new Response((InetSocketAddress) p.getSocketAddress(), Marshallable.unmarshall(p.getData())));
    //             } while (listen);
    //         } catch (IOException e) {
    //             callback.accept(null);
    //         }
    //         return false;
    //     });
    //     executor.schedule(() -> {
    //         handler.cancel(true);
    //         timeoutCall.accept(true);
    //     }, timeout, TimeUnit.SECONDS);

    // }

    public static String getIPaddress(){
        String ret= null;
        try{
            ret = InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ret;

    }

    InetSocketAddress getServerSocket() {
        return serverSocket;
    }

}
