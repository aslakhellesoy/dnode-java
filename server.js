#!/usr/bin/env node
var DNode = require('dnode');
var sys = require('sys');

var server = DNode({
  moo : function (reply) { 
    reply(100); 
    server.close();
  },
  boo : function (n, reply) { 
    reply(n+1); 
    server.close();
  }
}).listen(6060);
