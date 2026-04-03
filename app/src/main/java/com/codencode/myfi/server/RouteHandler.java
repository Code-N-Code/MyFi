package com.codencode.myfi.server;


import fi.iki.elonen.NanoHTTPD;

public interface RouteHandler {
    NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session);
}
