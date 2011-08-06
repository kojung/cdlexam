var updated_counter = false;
$(document).ready(function() {
  // allow question options to be selected
  $("div.question-options").click(function() {
    $(this)[0].firstChild.click();
  });
  // round question
  $("div.question").corner("30px");
  // round test categories
  $("div.test-category").corner("30px");
  // turn confirm into a button
  $("div.question-confirm")
    .corner("20px")
    .show('fade', {}, 1000)
    .click(function(evt) {
      // prevent default event
      evt.preventDefault();
      // button has effect only if an answer is selected
      var selected = $("input[name='answer']:checked"); 
      if (selected.length != 0) {
        var correct = $("input[name='correct-answer']")[0].value;
        if ( selected.val() == correct ) {
          $("div.question-confirm")
            .text("Correct!")
            .css('background-color', 'lightgreen')
            .effect('pulsate', {times:1}, 500);
          $("div.next-button")
            .corner("20px")
            .show('fade', {}, 1000)
            .click(function() {
              $("form:first")[0].submit();
            });
          if ( !updated_counter ) {
            updated_counter = true;
            var correct_counter = $("input[name='correct']")[0];
            correct_counter.value = parseInt(correct_counter.value) + 1;
          }
        } else {
          $("div.question-confirm")
            .text("Incorrect! Please try again.")
            .css('background-color', 'red')
            .effect('bounce', {times:3}, 100);
          $("div.next-button").hide();
          if ( !updated_counter ) {
            updated_counter = true;
            var incorrect_counter = $("input[name='incorrect']")[0];
            incorrect_counter.value = parseInt(incorrect_counter.value) + 1;
          }
        }
      };
    });
});
