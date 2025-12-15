package com.twb.stomplib.pathmatcher;

import com.twb.stomplib.dto.StompHeader;
import com.twb.stomplib.dto.StompMessage;

public class SimplePathMatcher implements PathMatcher {

    @Override
    public boolean matches(String path, StompMessage msg) {
        var dest = msg.findHeader(StompHeader.DESTINATION);
        if (dest == null) return false;
        else return path.equals(dest);
    }
}