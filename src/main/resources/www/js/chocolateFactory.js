"use strict";

var eventBus = new EventBus('/eventbus');
var peanutSpeed = new PeanutSpeed();

window.onload = whenDomIsReady.completed;
eventBus.onopen = whenEventBusIsOpen.completed;

whenEventBusIsOpen
    .thenDo(function() {
      eventBus.registerHandler('peanut.notify', function(){ produceItem('conveyorBelt', 'peanut'); });
    });

new CompositeFuture()
    .and(whenSliderIsReady)
    .and(whenEventBusIsOpen)
    .thenDo(function() {
      eventBus.registerHandler('peanut.speed.set', '', peanutSpeed.updatePeanutSpeed);
      eventBus.send('peanut.speed.get', '', peanutSpeed.updatePeanutSpeed);
    });