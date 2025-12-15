package com.twb.stomplib.pathmatcher;

import com.twb.stomplib.dto.StompHeader;
import com.twb.stomplib.dto.StompMessage;

import java.util.ArrayList;

public class RabbitPathMatcher implements PathMatcher {

    /**
     * RMQ-style wildcards.
     * See more info <a href="https://www.rabbitmq.com/tutorials/tutorial-five-java.html">here</a>.
     */
    @Override
    public boolean matches(String path, StompMessage msg) {
        var dest = msg.findHeader(StompHeader.DESTINATION);
        if (dest == null) return false;

        // for example string "lorem.ipsum.*.sit":

        // split it up into ["lorem", "ipsum", "*", "sit"]
        var split = path.split("\\.");
        var transformed = new ArrayList<String>();
        // check for wildcards and replace with corresponding regex
        for (var s : split) {
            switch (s) {
                case "*":
                    transformed.add("[^.]+");
                    break;
                case "#":
                    // TODO: make this work with zero-word
                    // e.g. "lorem.#.dolor" should ideally match "lorem.dolor"
                    transformed.add(".*");
                    break;
                default:
                    transformed.add(s.replaceAll("\\*", ".*"));
                    break;
            }
        }
        // at this point, 'transformed' looks like ["lorem", "ipsum", "[^.]+", "sit"]
        var sb = new StringBuilder();
        for (var s : transformed) {
            if (sb.length() > 0) sb.append("\\.");
            sb.append(s);
        }
        var join = sb.toString();
        // join = "lorem\.ipsum\.[^.]+\.sit"

        return dest.matches(join);
    }
}