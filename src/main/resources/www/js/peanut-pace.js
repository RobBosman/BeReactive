"use strict";

function PeanutPace() {

  var ID = Math.random().toFixed(10);
  var peanutPaceSlider;
  var isSuppressingUpdates = true;

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
      if (!isSuppressingUpdates) {
        eventBus.publish('peanut.pace.set',
          { 'value': values[handle] / 100.0 },
          { 'id': ID });
      }
    });

    isSuppressingUpdates = false;
    whenSliderIsReady.completed();
  };

  this.updatePeanutPace = function(err, msg) {
  if (msg === false) {
    return;
  }
    var intensityPercentage = 100.0 * msg.body.value;
    document.getElementById('peanut-pace-percentage').innerHTML = intensityPercentage.toFixed(0) + "%";
    if (msg.headers == null || msg.headers.id != ID) {
      isSuppressingUpdates = true;
      peanutPaceSlider.noUiSlider.set(intensityPercentage);
      isSuppressingUpdates = false;
    }
  };

  whenDomIsReady.thenDo(this.initializeSlider);
}