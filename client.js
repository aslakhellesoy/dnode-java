#!/usr/bin/env node
var DNode = require('dnode');
var m = process.ARGV[2];

var client = DNode.connect(6060, function (remote) {
  remote[m](function (x) {
    console.log(x);
  });
});

