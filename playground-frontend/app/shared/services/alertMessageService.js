var alertMessageService = angular.module('PlaygroundApp.alertMessageService', []);

alertMessageService.service('AlertMessageService', function() {
  var observerCallbacks = [];
  return {
    registerCallback: function(callback) {
      observerCallbacks.push(callback);
    },
    notifyObservers: function(message) {
      angular.forEach(observerCallbacks, function(callback) {
        callback(message);
      });
    },
    setAlertMessage: function(alertMessage) {
      this.notifyObservers(alertMessage);
    }
  }
});