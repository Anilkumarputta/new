'use strict';

document.addEventListener('DOMContentLoaded', function () {
  var btn = document.getElementById('btn');
  var message = document.getElementById('message');
  var count = 0;

  btn.addEventListener('click', function () {
    count += 1;
    message.textContent = 'Button clicked ' + count + ' time' + (count === 1 ? '' : 's') + '!';
  });
});
