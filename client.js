#!/usr/bin/env node
var DNode = require('dnode');
var sys = require('sys');

var client = DNode.connect(6060, function (remote) {
    console.log("Connected");
    remote.moo(function (x) {
        console.log(x);
    });
//    remote.sTimesTen(5, function (m) {
//        sys.puts(m); // 50, computation executed on the server
//        remote.timesTen(m, function (n) {
//            sys.puts(n); // 50 * 10 == 500
//        });
//    });
});

