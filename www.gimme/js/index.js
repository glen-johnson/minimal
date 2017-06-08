


function hide(div) {
   document.getElementById(div).style.display = "none";
}


function show(div) {
   document.getElementById(div).style.display = "block";
}

$( document ).ready(function() {
   //hide('gimme-logo');
   //show('home');
   //show('homeSearch');
   //$('#hlbutton').prop('disabled', false);
   //$('#showMenu').prop('disabled', false);
   $('html, body').animate({ scrollTop: 0 }, 0);
});


function hideAll() {
   hide('no-ads');
   
   hide('results');   
   hide('about');
   hide('tos');
   hide('crawler');
   
   hide('showDetail');
   hide('hlbutton');
   
}


function doQuery(a,b) {
   $('.typeahead').typeahead('close');
   $('#tftextinput').blur();
   $('#htftextinput').blur();
   collapseNavbar();
   if (! $('#results').is(':empty')) {
      $( "#results" ).empty();
   }
   //var a = document.getElementById('tfnewsearch');
   //var b = document.getElementById('tftextinput').value;
   $('#hlbutton').prop('disabled', false);
   $('html, body').animate({ scrollTop: 0 }, 'slow');
   if(b) {
      b = b.trim(b);
      b = b.replace(/[#?=]/g, '');
      b = b.replace(/\s\s+/g, ' ');
      $('#tftextinput').val(b);
      $('#htftextinput').val(b);
      var words = b.split(' ');
      var num_words = words.length;
      if(num_words > 3) {
         var re = /^[A-Z]/;
         var test = re.test(words[words.length - 1]);
         if(test === false) {
            bootbox.alert("Use 3 words or less to describe a product or service! Business owners will probably describe the product using different words.<br><br>Local searches ending with the name of a city or state are excepted from the count if they start with a capital letter. Example: \'honda seat covers Houston\'");
            return;
         }
      }
      if(b.length > 3 && b.length < 50) {
         hideAll();
         $('#showDetail').text("Sort A-Z"); 
         //show('gimme-logo');
         $.ajax({
            url: '/results?query='+b+'&cosmo',
            cache: false
         })
         .done(function( html ) {
            $( "#results" ).append( html );
            //bootbox.alert(html);
         });
         $('#aboutButton').text("About Us");
         show('showDetail');
         show('hlbutton');
         show('results');
         
         var d = b + ' ' + String.fromCharCode(9650);
         $('#scrollText').text(d); 
      }
      else {
         bootbox.alert("Please use atleast 4 characters but no more than 50 to capture a good list of businesses!");
      }
   }
}

function doIdeaQuery(b) {
   $('#tftextinput').blur();
   $('#hlbutton').prop('disabled', false);
   if (! $('#results').is(':empty')) {
      $( "#results" ).empty();
   }
   //collapseNavbar();
   $('html, body').animate({ scrollTop: 0 }, 'slow');
   var c = b.replace(/%20/g, ' ');
   $('#tftextinput').val(c);
   $('#htftextinput').val(c);
   
   hideAll();
   //show('gimme-logo');
   $('#showDetail').text("Sort A-Z"); 
   $.ajax({
      url: '/results?query='+b+'&cosmo',
      cache: false
   })
   .done(function( html ) {
      $( "#results" ).append( html );
   });
   show('showDetail');
   show('hlbutton');
   show('results');
   
   var d = c + ' ' + String.fromCharCode(9650);
   $('#scrollText').text(d); 
   return false;
}

$(function() {
   $('#queryDone').click(function() {
      $('body').removeHighlight();
      if (! $('#results').is(':empty')) {
         $( "#results" ).empty();
      }
      hideAll();
      //collapseNavbar();
      var d = 'Scroll Up' + String.fromCharCode(8593);
      $('#scrollText').text(d); 
      show('no-ads');
      show('homeSearch');
      $('#aboutButton').text("About Us");
      $('html, body').animate({ scrollTop: 0 }, 'slow');
   });
});

$(function() {
   $('#scrollDown').click(function() {
      var bottom = $(document).height() - $(window).height() - $(window).scrollTop();
      $('html, body').animate({ scrollTop: bottom }, 'slow');
   });
});   

$(function() {
   $('#showMenu').click(function() {
      //hideAll();
      //collapseNavbar();
      
      //show('gimme-logo');
      //show('menuDone');
      //show('shopping-links');
      //show('shopping-ideas');
      var d = 'Scroll Up';
      $('#scrollText').text(d); 
      $('html, body').animate({ scrollTop: 0 }, 'slow');
   });
});

$(function() {
   $('#menuDone').click(function() {
      // $('#showMenu').text("Shopping Ideas");
      //hideAll();
      //collapseNavbar();
      //$('#showMenu').css('font-weight', 'bold');
      if (! $('#shopping-links').is(':empty')) {
         $( "#shopping-links" ).empty();
      }
      //show('shopSmall');
      //show('no-ads');
      //show('homeSearch');
      $('html, body').animate({ scrollTop: 0 }, 'slow');
   });
});



function doLinkMenu(b) {
   $.ajax({
      url: b,
      cache: false
   })
   .done(function( html ) {
      $( "#shopping-links" ).append( html );
   });
}

function collapseNavbar() {
   return;
   if ($(".navbar-collapse").is(":visible") && $(".navbar-toggle").is(":visible") ) {
      $('.navbar-collapse').collapse('toggle');
   }
}

function navDone()
{
   //collapseNavbar();
   doHome();
   return false;
}

function doHome() {
   $("#homeButton").blur();
   if (! $('#results').is(':empty')) {
      $( "#results" ).empty();
   }
   hideAll();
   $('#aboutButton').text("About Us");
   show('no-ads');
   show('homeSearch');
   $('html, body').animate({ scrollTop: 0 }, 'slow');
   return false;
}


$(function() {
   $('#aboutButton').click(function() {

      if (! $('#results').is(':empty')) {
         $( "#results" ).empty();
      }
      hideAll();
      $(this).blur();
      if ($(this).text() == "Close") {
         $(this).text("About Us");
         //var a = document.getElementById('aboutButton');
         //if (a.html() === "Close") {
         docDone();
      }
      else {
         $(this).text("Close");
         show('about');
      }
      var d = 'Scroll Up';
      $('#scrollText').text(d); 
      $('html, body').animate({ scrollTop: 0 }, 0);
      
   });
});

$(function() {
   $('#tosButton').click(function() {

      if (! $('#results').is(':empty')) {
         $( "#results" ).empty();
      }
      hideAll();
      $('#tosButton').blur();
      $('#aboutButton').text("Close");
      show('tos');
      var d = 'Scroll Up';
      $('#scrollText').text(d); 
      $('html, body').animate({ scrollTop: 0 }, 0);
      
   });
});

$(function() {
   $('#crawlerButton').click(function() {

      if (! $('#results').is(':empty')) {
         $( "#results" ).empty();
      }
      hideAll();
      $('#crawlerButton').blur();
      $('#aboutButton').text("Close");
      show('crawler');
      var d = 'Scroll Up';
      $('#scrollText').text(d); 
      $('html, body').animate({ scrollTop: 0 }, 0);
      
   });
});
   


function docDone() {
   if (! $('#results').is(':empty')) {
      $( "#results" ).empty();
   }
   hideAll();
   $('#aboutButton').blur();
   show('homeSearch');
   show('no-ads');
   $('html, body').animate({ scrollTop: 0 }, 'slow');
}


$(function() {
   // constructs the suggestion engine
   var suggestions = new Bloodhound({
      datumTokenizer: Bloodhound.tokenizers.whitespace,
      queryTokenizer: Bloodhound.tokenizers.whitespace,
      prefetch: {
         url: 'suggestions.json',
         ttl: 0
      }
   });
   
   suggestions.initialize();
   
   // the space after bloodhound is REQUIRED
   $('#bloodhound .typeahead').typeahead({
      hint: false,
      highlight: true,
      minLength: 3,
      items: 10
      },
      {
         //source:states.ttAdapter(),
         name: 'suggestions',
         source: suggestions
      }
   );
   
   // the space after bloodhound is REQUIRED
   $('#navbar-bloodhound .typeahead').typeahead({
      hint: false,
      highlight: true,
      minLength: 3,
      items: 10
      },
      {
         //source:states.ttAdapter(),
         name: 'suggestions',
         source: suggestions
      }
   );
});


function ga(href) {
   var xhttp;
   if (window.XMLHttpRequest) {
      xhttp = new XMLHttpRequest();
   }
   else {
      xhttp = new ActiveXObject("Microsoft.XMLHTTP");
   }
   if(xhttp) {
      var visit = 'ga-analytics=' + href;
      xhttp.open("GET", visit, true);
      xhttp.send();
   }
}


function showLinks(el) {
   // window.alert(el);
   $(el).removeClass('btn-default');
   $(el).addClass('btn-info');
}


function hlaction() {
   $('#hlbutton').prop('disabled', true);
   var a = document.getElementById('tfnewsearch');
   var b = document.getElementById('tftextinput').value;
   if(b) {
      
      b = b.trim(b);
      b = b.replace(/AND/g, ' ');
      b = b.replace(/OR/g, ' ');
      b = b.replace(/[#?=]/g, '');
      b = b.replace(/\s+/g, ' ');
      
      if(b.match(/\s/)) {
         var c = b.replace(' ', '');
         $('#results').highlight(c);
         
         var words = b.split(' ');
         for (i = 0; i < words.length; i++) {
            
            var word = words[i];
            $('#results').highlight(word);
            
            if(word.match(/s$/) != null) {
               if(word.match(/ies$/) != null) {
                  c = word.replace(/ies$/, 'y');
                  $('#results').highlight(c);
               }
               else {
                  c = word.replace(/s$/, '');
                  $('#results').highlight(c);
               }
            }
         }
      }
      else {
         $('body').highlight(b);
         if(b.match(/s$/) != null) {
            if(b.match(/ies$/) != null) {
               b = b.replace(/ies$/, 'y');
               $('#results').highlight(b);
            }
            else {
               b = word.replace(/s$/, '');
               $('#results').highlight(b);
            }
         }
      }
   }
}


$(function() {
   $('#showDetail').click(function() {
      $('#buttons').toggle();
      $('#details').toggle();
      if ($(this).text() == "Sort A-Z") {
         $(this).text("Sort 1-10"); 
      }
      else {
         $(this).text("Sort A-Z");
      }
   });
});


$(function() {
   $(document).on( 'scroll', function() {
      if ($(window).scrollTop() > 100) {
         $('.scroll-top-wrapper').addClass('show');
      } else {
         $('.scroll-top-wrapper').removeClass('show');
      }
   });
   $('.scroll-top-wrapper').on('click', scrollToTop);
});
 
function scrollToTop() {
   verticalOffset = typeof(verticalOffset) != 'undefined' ? verticalOffset : 0;
   element = $('body');
   offset = element.offset();
   offsetTop = offset.top;
   $('html, body').animate({scrollTop: offsetTop}, 500, 'linear');
}

 
		

