var userService = angular.module('PlaygroundApp.userService', []);

userService.service('UserService', function() {
  var observerCallbacks = [];
  var user = {};
  return {
    registerCallback: function(callback) {
      observerCallbacks.push(callback);
    },
    notifyObservers: function() {
      angular.forEach(observerCallbacks, function(callback) {
        callback();
      });
    },
    getUser: function() {
      return user;
    },
    getEmail: function() {
      return user.email ? user.email : null;
    },
    getPlayground: function() {
      return user.playground ? user.playground : null;
    },
    getUsername: function() {
      return user.username ? user.username : null;
    },
    getAvatar: function() {
      return user.avatar ? user.avatar : null;
    },
    getRole: function() {
      return user.role ? user.role : null;
    },
    getPoints: function() {
      return user.points ? user.points : null;
    },
    setEmail: function(email) {
      user.email = email;
    },
    setPlayground: function(playground) {
      user.playground = playground;
    },
    setUsername: function(username) {
      user.username = username;
    },
    setAvatar: function(avatar) {
      user.avatar = avatar;
    },
    setRole: function(role) {
      user.role = role;
    },
    setPoints: function(points) {
      user.points = points;
    },
    setUserFields(loggedInUser) {
      this.setEmail(loggedInUser.email);
      this.setPlayground(loggedInUser.playground);
      this.setUsername(loggedInUser.username);
      this.setAvatar(loggedInUser.avatar);
      this.setRole(loggedInUser.role);
      this.setPoints(loggedInUser.points);
    },
    isUserValid: function() {
      return user.email != null && user.playground != null &&
            user.username != null && user.avatar != null &&
            user.role != null && user.points != null;
    },
    resetUser: function() {
      user = {};
      this.notifyObservers();
    }
  }
});