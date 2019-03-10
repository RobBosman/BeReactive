"use strict";

whenEventBusIsOpen
    .thenDo(function() {
      eventBus.registerHandler('peanut.produced', function(err, msg) {
        createPeanut(msg.body.id, 'conveyorBeltID');
      });
      eventBus.registerHandler('peanut.consumed', function(err, msg) {
        consumePeanut(msg.body.id, 'chocolatierID');
      });
    });

function createPeanut(peanutID, conveyorBeltID) {
  var conveyorBelt = document.getElementById(conveyorBeltID);
  var peanut = document.createElement('div');
  peanut.id = peanutID;
  peanut.classList.add('peanut');
  peanut.style.transform = "rotate(" + Math.random() + "turn)";
  conveyorBelt.appendChild(peanut);
  peanut.classList.add('onConveyorBelt');

  discardAfterMillis(peanutID, 10000);
}

function consumePeanut(peanutID, chocolatierID) {
  var peanut = document.getElementById(peanutID);
  var chocolatier = document.getElementById(chocolatierID);
  if (peanut != null && chocolatier != null) {
    peanut.parentElement.removeChild(peanut);
    peanut.classList.remove('onConveyorBelt');
    chocolatier.appendChild(peanut);
    peanut.classList.add('beingConsumed');

    setTimeout(function() {
      processPeanut(peanutID, chocolatierID);
    }, getComputedStyle(peanut).getPropertyValue('--rampUpMillis'));
  }
}

function processPeanut(peanutID, chocolatierID) {
  var peanut = document.getElementById(peanutID);
  var chocolatier = document.getElementById(chocolatierID);
  if (peanut != null && chocolatier != null) {
    peanut.parentElement.removeChild(peanut);
    peanut.classList.remove('beingConsumed');
    chocolatier.getElementsByClassName('progressBar')[0].appendChild(peanut);
    peanut.style.transform = "rotate(0)";
    peanut.classList.add('beingProcessed');

    discardAfterMillis(peanutID, 2000);
  }
}


function discardAfterMillis(elementID, timeoutMillis) {
  setTimeout(function() {
    discardElement(elementID);
  }, timeoutMillis);
}

function discardElement(elementID) {
  var element = document.getElementById(elementID);
  if (element != null) {
    element.parentElement.removeChild(element);
  }
}
