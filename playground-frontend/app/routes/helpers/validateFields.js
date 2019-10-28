// ##################################################
// Activity fields
// ##################################################
function validateActivityType(type) {
  if (type) {
    var pattern = new RegExp('^(FeedAnAnimal|GetMessage|PokeAnAnimal|PostMessage|ViewActivities)$')
    return pattern.test(type);
  }
  return false;
}
// ##################################################
// Element fields
// ##################################################
function validateElementName(name) {
  if(name) {
    var pattern = new RegExp('^(fox|bear|bison|lion|tiger|wolf|messageboard)$');
    return pattern.test(name.toLowerCase());
  }
  return false;
}
function validateElementType(type) {
  if(type) {
    var pattern = new RegExp('^(animal|message board)$');
    return pattern.test(type.toLowerCase());
  }
  return false;
}
// ##################################################
// User fields
// ##################################################
function validateRole(role) {
  if(role) {
    var pattern = new RegExp('^(player|manager)$');
    return pattern.test(role);
  }
  return false;
}
function validateAvatar(avatar) {
  if(avatar) {
    var pattern = new RegExp('^(male|female)$');
    return pattern.test(avatar);
  }
  return false;
}
function validateEmail(email) {
  if(email) {
    var pattern = new RegExp('^([a-z]|[A-Z]|[0-9])+@([a-z]|[A-Z]|[0-9])+\.([a-z])+$');
    return pattern.test(email)
  }
  return false;
}
// ##################################################
// Common validators
// ##################################################
function validatePlayground(playground) {
  if(playground) {
    var pattern = new RegExp('^(2019A\.kariv)$')
    return pattern.test(playground);
  }
  return false;
}

module.exports = {
  validateActivityType: validateActivityType,
  validateElementName:  validateElementName,
  validateElementType:  validateElementType,
  validateRole:         validateRole,
  validateAvatar:       validateAvatar,
  validateEmail:        validateEmail,
  validatePlayground:   validatePlayground
}