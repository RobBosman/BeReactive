"use strict";

whenEventBusIsOpen
    .thenDo(function() {
      eventBus.registerHandler('peanut.produced', function(err, msg) {
        createPeanut(msg.body.id, 'conveyorBelt');
      });
      eventBus.registerHandler('peanut.consumed', function(err, msg) {
        processPeanut(msg.body.id, 'conveyorBelt');
      });
    });

function createPeanut(peanutID, conveyorBeltID) {
  var conveyorBelt = document.getElementById(conveyorBeltID);
  var peanut = document.createElement('div');
  peanut.id = peanutID;
  peanut.className = 'peanut';
  peanut.style.transform = "rotate(" + Math.random() + "turn)";
  conveyorBelt.appendChild(peanut);

  setTimeout(function() { removePeanut(peanutID); }, 10000);
}

function removePeanut(peanutID) {
  var peanut = document.getElementById(peanutID);
  if (peanut != null) {
    peanut.parentElement.removeChild(peanut);
  }
}

function processPeanut(peanutID) {
  removePeanut(peanutID);
}
