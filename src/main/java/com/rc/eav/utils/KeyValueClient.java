package com.rc.eav.utils;
import java.io.*;
import java.net.Socket;
import java.nio.*;
import java.util.*;

public class KeyValueClient implements Closeable {
    private final String host ;
    private final int port;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public KeyValueClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        connect();
    }

    private void connect() throws IOException {
        this.socket = new Socket(host, port);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
    }

    public String set(String key, String value) throws IOException, InterruptedException {
        return sendAndReadResponse(Arrays.asList("set", key, value));
    }

    public String get(String key) throws IOException, InterruptedException {
        return sendAndReadResponse(Arrays.asList("get", key));
    }

    public String sendCustomCommand(List<String> command) throws IOException, InterruptedException {
        return sendAndReadResponse(command);
    }

    private String sendAndReadResponse(List<String> command) throws IOException, InterruptedException {
        if (!send(command)) {
            throw new IOException("Failed to send command: " + command);
        }

        byte[] response = readResponse();
        Thread.sleep(10);
        return parseResponse(response);
    }

    private boolean send(List<String> cmd) throws IOException {
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        payload.write(intToLittleEndian(cmd.size()));

        for (String arg : cmd) {
            byte[] argBytes = arg.getBytes("UTF-8");
            payload.write(intToLittleEndian(argBytes.length));
            payload.write(argBytes);
        }

        byte[] body = payload.toByteArray();
        ByteBuffer message = ByteBuffer.allocate(4 + body.length)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(body.length)
                .put(body);

        out.write(message.array());
        out.flush();
        return true;
    }
    private byte[] readResponse() throws IOException {
        byte[] header = new byte[4];
        if (in.read(header) < 4) throw new IOException("Incomplete response header");

        int len = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (len <= 0 || len > 4096) throw new IOException("Invalid response length: " + len);

        byte[] responseBody = new byte[len];
        int totalRead = 0;
        while (totalRead < len) {
            int bytesRead = in.read(responseBody, totalRead, len - totalRead);
            if (bytesRead == -1) throw new EOFException("Unexpected end of stream");
            totalRead += bytesRead;
        }
        return responseBody;
    }

    private static byte[] intToLittleEndian(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

    private static String parseResponse(byte[] data) throws IOException {
        final int SER_NIL = 0, SER_ERR = 1, SER_STR = 2, SER_INT = 3, SER_DBL = 4, SER_ARR = 5, SER_KV = 6;

        if (data == null || data.length == 0) throw new IOException("Empty response.");

        int rtype = Byte.toUnsignedInt(data[0]);
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        switch (rtype) {
            case SER_NIL: return "(nil)";
            case SER_ERR: {
                buf.position(1);
                long code = Integer.toUnsignedLong(buf.getInt());
                int strlen = buf.getInt();
                String msg = new String(data, 9, strlen, "UTF-8");
                return String.format("(err) %d: %s", code, msg);
            }
            case SER_STR: {
                buf.position(1);
                int strlen = buf.getInt();
                return String.format("(str) %s", new String(data, 5, strlen, "UTF-8"));
            }
            case SER_INT: {
                buf.position(1);
                return String.format("(int) %d", buf.getLong());
            }
            case SER_DBL: {
                buf.position(1);
                return String.format("(dbl) %f", buf.getDouble());
            }
            case SER_ARR: {
                buf.position(1);
                int arrLen = buf.getInt();
                StringBuilder sb = new StringBuilder(String.format("(arr) len=%d\n", arrLen));
                int offset = 5;
                for (int i = 0; i < arrLen; i++) {
                    byte[] slice = Arrays.copyOfRange(data, offset, data.length);
                    String elem = parseResponse(slice);
                    sb.append("  ").append(elem).append("\n");
                    offset += ByteBuffer.wrap(slice).order(ByteOrder.LITTLE_ENDIAN).getInt(1) + 5;
                }
                return sb.append("(arr) end").toString();
            }
            case SER_KV: {
                buf.position(1);
                int totalLen = buf.getInt();
                int keyLen = buf.getInt();
                String key = new String(data, 9, keyLen, "UTF-8");
                int valOffset = 9 + keyLen;
                int valLen = ByteBuffer.wrap(data, valOffset, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
                String value = new String(data, valOffset + 4, valLen, "UTF-8");
                return String.format("(kv) key: %s, value: %s", key, value);
            }
            default: throw new IOException("Unknown response type: " + rtype);
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null) socket.close();
        if (in != null) in.close();
        if (out != null) out.close();
    }
}
