#!/usr/bin/env node
var DNode = require('dnode');
var sys = require('sys');

// server-side:
var server = DNode({
//    timesTen : function (n,reply) { reply(n * 10) },
    moo : function (reply) { 
        reply(100); 
        server.close();
    },
    boo : function (n, reply) { 
        reply(n+1); 
        server.close();
    }
//    sTimesTen : DNode.sync(function (n) { return n * 10 }),
}).listen(6060);
