var messageBoardModule = angular.module('PlaygroundApp.messageBoard', ['ngRoute']);
messageBoardModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/message_board', {
    templateUrl: 'app/components/messageBoard/messageBoardView.ejs',
  })
}]);
messageBoardModule.controller('MessageBoardController', function($scope, $http, $location, $window, UserService, AlertMessageService) {
  console.info('MessageBoardController - entered');
  if (UserService.isUserValid()) {
    setValuesForUser();
    setInitialPaginationValuesForElements();
    setInitialPaginationValuesForActivities();
    setDefaultValuesForNewElementForm();
    $scope.resetElementsPaginationAndGetElements = false;
    $scope.$watch('resetElementsPaginationAndGetElements', function(newValue, oldValue) {
      $scope.resetElementsPaginationAndGetElements = false;
      setInitialPaginationValuesForElements();
      getNextPageOfElements();
    });
    UserService.registerCallback(function() {
      $location.path('/login');
    });
  }
  else {
    console.error('MessageBoardController - user is not valid, sign in first.');
    $location.path('/login');
  }
  // ##################################################
  // Helpers for Controller
  // ##################################################
  function setValuesForUser() {
    console.info('MessageBoardController - setValuesForUser()');
    $scope.playground = UserService.getPlayground();
    $scope.userRole   = UserService.getRole();
    $scope.userEmail  = UserService.getEmail();
  }
  function setInitialPaginationValuesForElements() {
    console.info('MessageBoardController - setInitialPaginationValuesForElements()');
    // There is no 0 page.
    // Its value will be incremented to 1 once getNextPageOfElements() is called
    $scope.elementsPage     = 0;
    $scope.elementsPageSize = 3;
  }
  function setInitialPaginationValuesForActivities() {
    console.info('MessageBoardController - setInitialPaginationValuesForActivities()');
    // There is no 0 page.
    // Its value will be incremented to 1 once getNextPageOfElements() is called
    $scope.activitiesPage     = 0;
    $scope.activitiesPageSize = 5;
    $scope.message            = {};
  }
  function setDefaultValuesForNewElementForm() {
    console.info('MessageBoardController - setDefaultValuesForNewElementForm()');
    $scope.element                = {};
    $scope.element.name           = 'MessageBoard';
    $scope.element.expirationDate = '2019-12-01T09:15:00';
    $scope.element.type           = 'Message board';
    $scope.element.attributes     = '{"theme": "Fun"}';
  }
  function getNextPageOfElements() {
    console.info('MessageBoardController - getNextPageOfElements()');
    $scope.elementsPage += 1;
    if ($scope.elementsPage < 1) {
      $scope.elementsPage = 1;
    }
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/elements/message_board/next_page',
      data:   {
        userPlayground: $scope.playground,
        userEmail:      $scope.userEmail,
        page:           $scope.elementsPage,
        size:           $scope.elementsPageSize
      }
    }
    $http(request).then(function success(response) {
      console.info('MessageBoardController - $http(request) - success');
      console.info('MessageBoardController - $http(request) - response ' + JSON.stringify(response));
      $scope.elements = response.data;
      if($scope.elements.length == 0 && $scope.elementsPage != 1) {
        $scope.resetElementsPaginationAndGetElements = true;
      }
    }, function failure(response) {
      console.error('MessageBoardController - $http(request) - failure');
      console.error('MessageBoardController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  function getFirstPageOfActivities() {
    console.info('MessageBoardController - getFirstPageOfActivities()');
    $scope.activitiesPage = 1;
    var activityData = {
      playground:        $scope.playground,
      elementPlayground: $scope.messageBoard.playground,
      elementId:         $scope.messageBoard.id,
      type:              'ViewActivities',
      playerPlayground:  $scope.playground,
      playerEmail:       $scope.userEmail,
      attributes:        {
        activityType:      "PostMessage",
        elementId:         $scope.messageBoard.id,
        elementPlayground: $scope.messageBoard.playground,
        page:              String($scope.activitiesPage),
        sizeOfPage:        String($scope.activitiesPageSize)
      }
    }
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/activities/create_activity',
      data:   { activityData: activityData }
    }
    console.info('MessageBoardController - getFirstPageOfActivities() - request : ' + JSON.stringify(request));
    $http(request).then(function success(response) {
      console.info('MessageBoardController - $http(request) - success');
      console.info('MessageBoardController - $http(request) - response ' + JSON.stringify(response));
      $scope.messages = JSON.parse(response.data.attributes.createActivityresponseMessage);
    }, function failure(response) {
      console.error('MessageBoardController - $http(request) - failure');
      console.error('MessageBoardController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  function getNextPageOfActivities() {
    console.info('MessageBoardController - getNextPageOfActivities()');
    $scope.activitiesPage += 1;
    if ($scope.activitiesPage < 1) {
      getFirstPageOfActivities();
    }
    else {
      var activityData = {
        playground:        $scope.playground,
        elementPlayground: $scope.messageBoard.playground,
        elementId:         $scope.messageBoard.id,
        type:              'ViewActivities',
        playerPlayground:  $scope.playground,
        playerEmail:       $scope.userEmail,
        attributes:        {
          activityType:      "PostMessage",
          elementId:         $scope.messageBoard.id,
          elementPlayground: $scope.messageBoard.playground,
          page:              String($scope.activitiesPage),
          sizeOfPage:        String($scope.activitiesPageSize)
        }
      }
      var request = {
        method: 'POST',
        url:    'http://localhost:3000/api/activities/create_activity',
        data:   { activityData: activityData }
      }
      console.info('MessageBoardController - getNextPageOfActivities() - request : ' + JSON.stringify(request));
      $http(request).then(function success(response) {
        console.info('MessageBoardController - $http(request) - success');
        console.info('MessageBoardController - $http(request) - response ' + JSON.stringify(response));
        $scope.messages = JSON.parse(response.data.attributes.createActivityresponseMessage);
        if ($scope.messages.length == 0 && $scope.activitiesPage != 1) {
          getFirstPageOfActivities();
        }
      }, function failure(response) {
        console.error('MessageBoardController - $http(request) - failure');
        console.error('MessageBoardController - $http(request) - statusText - ' + response.statusText);
        AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
      });
    }
  }
  function reLogin() {
    console.info('MessageBoardController - reLogin()');
    var userLoginForm = {
      email:      $scope.userEmail,
      playground: $scope.playground
    }
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/users/login',
      data:   { userForm: userLoginForm }
    }
    console.info('MessageBoardController - reLogin() - ' + JSON.stringify(userLoginForm))
    $http(request).then(function success(response) {
      console.info('MessageBoardController - $http(request) - success');
      console.info('MessageBoardController - $http(request) - response ' + JSON.stringify(response));
      UserService.setUserFields(response.data);
    }, function failure(response) {
      console.error('MessageBoardController - $http(request) - failure');
      console.error('MessageBoardController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  // ##################################################
  // Functions to call from view template
  // ##################################################
  $scope.addNewElement = function() {
    var newElementForm = {
      playground: $scope.playground,
      location: {
        x: $scope.element.locationX,
        y: $scope.element.locationY
      },
      name:              $scope.element.name,
      expirationDate:    $scope.element.expirationDate,
      type:              $scope.element.type,
      attributes:        JSON.parse($scope.element.attributes),
      creatorPlayground: $scope.playground,
      creatorEmail:      $scope.userEmail
    };
    console.info('MessageBoardController - addNewElement() - ' + JSON.stringify(newElementForm));
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/elements/new_element',
      data:   { newElementForm: newElementForm}
    }
    $http(request).then(function success(response) {
      console.info('MessageBoardController - $http(request) - success');
      console.info('MessageBoardController - $http(request) - response ' + JSON.stringify(response));
      $scope.elementsPage -= 1;
      getNextPageOfElements();
      $window.hideElementSubmissionForm();
    }, function failure(response) {
      console.error('MessageBoardController - $http(request) - failure');
      console.error('MessageBoardController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  $scope.postMessage = function() {
    console.info('MessageBoardController - postMessage() - ' + $scope.message.content);
    var activityData = {
      playground:        $scope.playground,
      elementPlayground: $scope.messageBoard.playground,
      elementId:         $scope.messageBoard.id,
      type:              'PostMessage',
      playerPlayground:  $scope.playground,
      playerEmail:       $scope.userEmail,
      attributes:        { content: $scope.message.content}
    }
    console.info('MessageBoardController - postMessage() - activityData : ' + JSON.stringify(activityData));
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/activities/create_activity',
      data:   { activityData: activityData }
    }
    $http(request).then(function success(response) {
      console.info('MessageBoardController - $http(request) - success');
      console.info('MessageBoardController - $http(request) - response ' + JSON.stringify(response));
      var attributes = response.data.attributes;
      AlertMessageService.setAlertMessage({status: 'ok', response: JSON.stringify(attributes)});
      reLogin();
      $scope.stopViewingMessages();
    }, function failure(response) {
      console.error('MessageBoardController - $http(request) - failure');
      console.error('MessageBoardController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  $scope.viewMessage = function (messageToViewId) {
    console.info('MessageBoardController - viewMessage() - ' + messageToViewId);
    var activityData = {
      playground:        $scope.playground,
      elementPlayground: $scope.messageBoard.playground,
      elementId:         $scope.messageBoard.id,
      type:              'GetMessage',
      playerPlayground:  $scope.playground,
      playerEmail:       $scope.userEmail,
      attributes:        { idOfMessageToView: messageToViewId}
    }
    console.info('MessageBoardController - postMessage() - activityData : ' + JSON.stringify(activityData));
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/activities/create_activity',
      data:   { activityData: activityData }
    }
    $http(request).then(function success(response) {
      console.info('MessageBoardController - $http(request) - success');
      console.info('MessageBoardController - $http(request) - response ' + JSON.stringify(response));
      reLogin();
      AlertMessageService.setAlertMessage({status: 'ok', response: JSON.stringify(response.data.attributes)});
    }, function failure(response) {
      console.error('MessageBoardController - $http(request) - failure');
      console.error('MessageBoardController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  $scope.viewMessages = function(messageBoardElement) {
    $scope.messageBoard = messageBoardElement;
    getNextPageOfActivities();
    $window.showMessagesContainer();
  }
  $scope.stopViewingMessages = function() {
    setInitialPaginationValuesForActivities();
    $scope.messageBoard = {};
    $scope.message      = {};
    $window.hideMessagesContainer();
  }
  $scope.nextPageOfElements = function() {
    getNextPageOfElements();
  }
  $scope.previousPageOfElements = function() {
    // We have to get current $scope.elementsPage - 1.
    // Since getNextPageOfElements() adds 1 to $scope.elementsPage,
    // we have to set $scope.elementsPage -= 2
    $scope.elementsPage -= 2;
    getNextPageOfElements();
  }
  $scope.nextPageOfActivities = function() {
    getNextPageOfActivities();
  }
  $scope.previousPageOfActivities = function() {
    // We have to get current $scope.elementsPage - 1.
    // Since getNextPageOfElements() adds 1 to $scope.elementsPage,
    // we have to set $scope.elementsPage -= 2
    $scope.activitiesPage -= 2;
    getNextPageOfActivities();
  }
});