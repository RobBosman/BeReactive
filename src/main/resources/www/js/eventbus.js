"use strict";

var eventBus = new EventBus('/eventbus');

var whenEventBusIsOpen = new Future();
eventBus.onopen = whenEventBusIsOpen.completed;
