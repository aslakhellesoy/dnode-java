package dnode.socketio;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SocketIOCodec {
    private static final String FRAME = "~m~";

    public String encode(String message) {
        return encode(Collections.singletonList(message));
    }

    public String encode(List messages) {
        StringBuilder sb = new StringBuilder();
        for (Object message : messages) {
            String m = stringify(message);
            sb.append(FRAME).append(m.length()).append(FRAME).append(m);
        }
        return sb.toString();
    }

    private String stringify(Object message) {
        if (message instanceof JsonElement) {
            return "~j~" + message.toString();
        } else {
            return String.valueOf(message);
        }
    }

    public List<String> decode(String data) {
        List<String> messages = new ArrayList<String>();
        do {
            String number = "";
            if (!data.substring(0, 3).equals(FRAME)) return messages;
            data = data.substring(3);
            for (int i = 0, l = data.length(); i < l; i++) {
                try {
                    String sub = data.substring(i, i + 1);
                    Integer.parseInt(sub);
                    number += data.substring(i, i + 1);
                } catch (NumberFormatException e) {
                    data = data.substring(number.length() + FRAME.length());
                    break;
                }
            }
            int l = Integer.parseInt(number);
            messages.add(data.substring(0, l));
            data = data.substring(l);
        } while (!data.equals(""));
        return messages;
    }
}
