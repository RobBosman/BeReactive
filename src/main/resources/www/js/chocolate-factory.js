"use strict";

whenEventBusIsOpen
    .thenDo(function () {
      eventBus.registerHandler('peanut', function () {
         producePeanut('conveyorBelt');
       });
    });

function producePeanut(conveyorBeltID) {
  var conveyorBelt = document.getElementById(conveyorBeltID);
  var peanut = document.createElement('div');
  peanut.className = 'peanut';
  peanut.style.transform = "rotate(" + Math.random() + "turn)";
  conveyorBelt.appendChild(peanut);
  setTimeout(function (){ conveyorBelt.removeChild(peanut); }, 5000);
}