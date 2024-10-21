'use strict';

function submitFilterForm() {
  document.getElementById('filterForm').submit();
}

document.getElementById('showPassword').addEventListener('change', function() {
  var passwordInput = document.getElementById('password');
  var rePasswordInput = document.getElementById('rePassword');
  if (this.checked) {
    passwordInput.type = 'text';
    rePasswordInput.type = 'text';
  } else {
    passwordInput.type = 'password';
    rePasswordInput.type = 'password';
  }
});