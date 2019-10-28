function showElementSubmissionForm() {
  makeFirstLayerBarelySeen();
  document.getElementById('addNewElementForm').classList.remove('invisible');
}
function hideElementSubmissionForm() {
  document.getElementById('locationX').value = '';
  document.getElementById('locationY').value = '';
  document.getElementById('name').value = '';
  makeFirstLayerSeen();
  document.getElementById('addNewElementForm').classList.add('invisible');
}
function showEditElementForm() {
  makeFirstLayerBarelySeen();
  document.getElementById('editElementForm').classList.remove('invisible');
}
function hideEditElementForm() {
  makeFirstLayerSeen();
  document.getElementById('editElementForm').classList.add('invisible');
}
function showMessagesContainer() {
  makeFirstLayerBarelySeen();
  document.getElementById('messagesContainer').classList.remove('invisible');
}
function hideMessagesContainer() {
  makeFirstLayerSeen();
  document.getElementById('messagesContainer').classList.add('invisible');
}
function showCustomAlert(status) {
  var alertContainer = document.getElementById('alertMessageContainer')
  if(status == 'ok') {
    alertContainer.classList.add('alertMessageContainerOk');
  }
  else {
    alertContainer.classList.add('alertMessageContainerError');
  }
  alertContainer.classList.remove('invisible');
}
function hideCustomAlert() {
  var alertContainer = document.getElementById('alertMessageContainer')
  alertContainer.classList.remove('alertMessageContainerOk');
  alertContainer.classList.remove('alertMessageContainerError');
  alertContainer.classList.add('invisible');
}
// Helpers
function makeFirstLayerBarelySeen() {
  var firstLayerElements = document.getElementsByClassName('firstLayer'); 
  for (i = 0; i < firstLayerElements.length; i++) {
    firstLayerElements[i].classList.add('firstLayerBarelySeen');
  }
}
function makeFirstLayerSeen() {
  var firstLayerElements = document.getElementsByClassName('firstLayer'); 
  for (i = 0; i < firstLayerElements.length; i++) {
    firstLayerElements[i].classList.remove('firstLayerBarelySeen');
  }
}