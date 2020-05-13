var userId;
var editing;
var parent;

// Work-around for missing JQuery.postJSON
jQuery["postJSON"] = function(url,data,callback) {
  var options = {
    url:url,
    type:'POST',
    data:JSON.stringify(data),
    contentType:'application/json',
    dataType:'json',
    success: callback
  };
  $.ajax(options);
};

// Work-around for missing JQuery.putJSON
jQuery["putJSON"] = function(url,data,callback) {
  var options = {
    url:url,
    type:'PUT',
    data:JSON.stringify(data),
    contentType:'application/json',
    dataType:'json',
    success: callback
  };
  $.ajax(options);
};

function submitSuccess(data) {
  $('#two').hide();
  $('#one').show();  
}

function handleSubmitProfile() {
	var parentObject;
    if(editing) {
		parentObject = parent;
		parentObject.name = $('#name').val();
		parentObject.phone = $('#phone').val();
		parentObject.address = $('#address').val();
		parentObject.city = $('#city').val();
		parentObject.email = $('#email').val();
	} else {
		parentObject = {name:$('#name').val(),
		                phone:$('#phone').val(),
						address:$('#address').val(),
						city:$('#city').val(),
						email:$('#email').val(),
						user:userId};
	}
	var url = 'http://localhost:8085/parent';
	if(editing) 
		$.putJSON(url,parentObject,submitSuccess);
	else
		$.postJSON(url,parentObject,submitSuccess);
}

function handleCreateProfile() {
  $('#one').hide();
  $('#two').show();
  $('#two input[type="text"]').val('');
  editing = false;
}

function startEdit(data) {
  parent = data;
  $('#one').hide();
  $('#two').show();
  $('#name').val(parent.name);
  $('#phone').val(parent.phone);
  $('#address').val(parent.address);
  $('#city').val(parent.city);
  $('#email').val(parent.email);
  editing = true;
}

function handleEditProfile() {
  var url = 'http://localhost:8085/parent?user='+userId;
  $.getJSON(url,startEdit);
}

function loginSuccess(data) {
  // On successful login the server will send us a user id
  if(data > 0) {
	  userId = data;
	  $('#auth').hide();
	  $('#one').show();
  }
}

function handleNewAccount() {
  var user = $('#user').val();
  var pwd = $('#password').val();
  var userObject = {name:user,password:pwd};
  var url = 'http://localhost:8085/user';
  $.postJSON(url,userObject,loginSuccess);
}

function handleLogin() {
  var user = $('#user').val();
  var pwd = $('#password').val();
  var url = 'http://localhost:8085/user?name='+user+'&password='+pwd;
  $.getJSON(url,loginSuccess);
}

// Event handler function for the document ready event.
function setUpButtons() {
  // Set up event handlers for the buttons
  $('#login').click(handleLogin);
  $('#new').click(handleNewAccount);
  $('#create').click(handleCreateProfile);
  $('#edit').click(handleEditProfile);
  $('#done').click(handleSubmitProfile);

  // Hide sections one, two and three
  $('#one').hide();
  $('#two').hide();
  $('#three').hide();
}

$(document).ready(setUpButtons);