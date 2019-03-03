"use strict";

var eventBus = new EventBus('/eventbus');
var peanutPace = new PeanutPace();

var whenEventBusIsOpen = new Future();
eventBus.onopen = whenEventBusIsOpen.completed;

whenEventBusIsOpen
    .thenDo(function() {
      eventBus.registerHandler('peanut', function() {
         var peanut = produceItem('conveyorBelt', 'peanut');
         peanut.style.transform = "rotate(" + (Math.random() * 360) + "deg)";
       });
    });

new CompositeFuture()
    .and(whenSliderIsReady)
    .and(whenEventBusIsOpen)
    .thenDo(function() {
      eventBus.registerHandler('peanut.pace.set', '', peanutPace.updatePeanutPace);
      eventBus.send('peanut.pace.get', '', peanutPace.updatePeanutPace);
    });