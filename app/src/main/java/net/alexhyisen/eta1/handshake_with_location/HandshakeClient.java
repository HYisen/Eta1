package net.alexhyisen.eta1.handshake_with_location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Alex on 2017/4/29.
 * an inheritance of BaseClient from project Eta0
 */

class HandshakeClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public HandshakeClient(String host, int port) throws IOException {
        link(host, port);
    }

    public HandshakeClient() {
    }

    public void link(String host, int port) throws IOException {
        //System.out.println("linking "+host+" at "+port);
        socket = new Socket(host, port);
        out=new PrintWriter(socket.getOutputStream(),true);
        in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(String content) {
        System.out.println("client: "+content);
        out.println(content);
    }

    public String receive() throws IOException {
        String line=in.readLine();
        System.out.println("server: "+line);
        return line;
    }

    public void close() throws IOException {
        //out.close();
        //in.close();
        //Stream from socket would automatically closed, no need to revoke implicitly.
        socket.close();
    }
}
