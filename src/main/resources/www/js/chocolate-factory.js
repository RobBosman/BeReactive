"use strict";

whenEventBusIsOpen
    .thenDo(function() {
      eventBus.registerHandler('peanut.produced', function(err, msg) {
        producedPeanut(msg.body.id, 'conveyorBelt');
      });
      eventBus.registerHandler('peanut.consumed', function(err, msg) {
        consumedPeanut(msg.body.id, 'conveyorBelt');
      });
    });

function producedPeanut(peanutID, conveyorBeltID) {
  var conveyorBelt = document.getElementById(conveyorBeltID);
  var peanut = document.createElement('div');
  peanut.id = peanutID;
  peanut.className = 'peanut';
  peanut.style.transform = "rotate(" + Math.random() + "turn)";
  conveyorBelt.appendChild(peanut);
}

function consumedPeanut(peanutID) {
  var peanut = document.getElementById(peanutID);
  peanut.parentElement.removeChild(peanut);
}
