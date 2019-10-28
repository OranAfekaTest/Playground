var alertModule = angular.module('PlaygroundApp.alert', ['ngRoute']);

alertModule.controller('AlertController', function($scope, $window, AlertMessageService) {
  console.info('AlertController - entered');
  var showAlertInBrowser = function(messageObj) {
    console.info('AlertController - showAlertInBrowser() - ' + JSON.stringify(messageObj));
    $scope.alertMessage = messageObj.response;
    $window.showCustomAlert(messageObj.status);
  }
  AlertMessageService.registerCallback(showAlertInBrowser);
});