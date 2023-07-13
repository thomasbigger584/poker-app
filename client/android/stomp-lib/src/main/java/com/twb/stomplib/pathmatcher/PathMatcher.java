package com.twb.stomplib.pathmatcher;


import com.twb.stomplib.dto.StompMessage;

public interface PathMatcher {

    boolean matches(String path, StompMessage msg);
}