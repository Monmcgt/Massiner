package me.monmcgt.code.massiner.checkers.checking;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


class Pinger {
    private InetSocketAddress host;
    private int timeout;

    void setAddress(InetSocketAddress host) {
        this.host = host;
    }

    void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;


        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) {
                return -1;
            }
            if ((k & 0x80) != 128)
                return i;
        }
    }

    private void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {

                out.writeByte(paramInt);
                return;
            }
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }


    public String fetchData() throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(this.timeout);
        socket.connect(this.host, this.timeout);

        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(b);

        handshake.writeByte(0);
        writeVarInt(handshake, 4);
        writeVarInt(handshake, this.host.getHostString().length());
        handshake.writeBytes(this.host.getHostString());
        handshake.writeShort(this.host.getPort());
        writeVarInt(handshake, 1);

        writeVarInt(dataOutputStream, b.size());
        dataOutputStream.write(b.toByteArray());
        dataOutputStream.writeByte(1);
        dataOutputStream.writeByte(0);
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        int size = readVarInt(dataInputStream);
        int id = readVarInt(dataInputStream);
        int length = readVarInt(dataInputStream);

        if (size < 0 || id < 0 || length <= 0) {

            closeAll(new Closeable[] {b, dataInputStream, handshake, dataOutputStream, outputStream, inputStream, socket});
            return null;
        }

        byte[] in = new byte[length];
        dataInputStream.readFully(in);
        closeAll(new Closeable[] {b, dataInputStream, handshake, dataOutputStream, outputStream, inputStream, socket});
        return new String(in);
    }


    public void closeAll(Closeable... closeables) throws IOException {
        for (Closeable closeable : closeables) {
            closeable.close();
        }
    }
}


