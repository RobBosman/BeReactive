"use strict";

new PeanutPace();

function PeanutPace() {

  var sliderID = Math.random().toFixed(10);
  var peanutPaceSlider;
  var suppressUpdates = true;
  var whenSliderIsReady = new Future();
  var self = this;

  this.initializeSlider = function() {
    peanutPaceSlider = document.getElementById('peanut-pace');
    noUiSlider.create(peanutPaceSlider, {
      start: 0,
      orientation: 'vertical',
      direction: 'rtl',
      range: { 'min': 0, 'max': 100 },
      pips: { mode: 'positions', values: [0, 25, 50, 75, 100], density: 4 }
    });

    peanutPaceSlider.noUiSlider.on('update', function(values, handle) {
      if (!suppressUpdates) {
        eventBus.publish('peanut.pace.set',
            { 'value': values[handle] / 100.0 },
            { 'sliderID': sliderID });
      }
    });

    suppressUpdates = false;
    whenSliderIsReady.completed();
  };

  this.updatePeanutPace = function(err, msg) {
    if (msg === false) {
      return;
    }
    var intensityPercentage = 100.0 * msg.body.value;
    document.getElementById('peanut-pace-percentage').innerHTML = intensityPercentage.toFixed(0) + "%";
    if (msg.headers == null || msg.headers.sliderID !== sliderID) {
      suppressUpdates = true;
      peanutPaceSlider.noUiSlider.set(intensityPercentage);
      suppressUpdates = false;
    }
  };

  whenDomIsReady
      .thenDo(self.initializeSlider);

  new CompositeFuture()
      .and(whenSliderIsReady)
      .and(whenEventBusIsOpen)
      .thenDo(function() {
        eventBus.registerHandler('peanut.pace.set', '', self.updatePeanutPace);
        eventBus.send('peanut.pace.get', '', self.updatePeanutPace);
      });
}
