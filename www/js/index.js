


function hide(div) {
   document.getElementById(div).style.display = "none";
}


function show(div) {
   document.getElementById(div).style.display = "block";
}


$( document ).ready(function() {
   
   // $( "#results" ).empty();
   // hide('shoppingPages');
   //$('html, body').animate({ scrollTop: 0 }, 0);
   //bootbox.alert('finally GLen');
   //var el = document.getElementById("tftextinput");
   //if(el != null) {
   //   el.select();
   //}
   //document.forms[0].elements[0].focus();
});


function loadDomain() {
   window.location.href = "domain.html";
}

function loadHome() {
   window.location.href = "/";
}


function hideAll() {
   hide('clearButton');
   hide('newSearch');
   
}

/*
$(':input').focus(function(){
    var center = $(window).height()/2;
    var top = $(this).offset().top ;
    if (top > center){
        $(window).scrollTop(top-center);
    }
});


$(':input').focus(function () {
   var center = $(window).height() / 2;
   var top = $(this).offset().top;
   if (top > center) {
      $('html, body').animate({ scrollTop: top - center }, 'fast');            
   }
});
*/

var amountScrolled = 300;

$(window).scroll(function() {
	if ( $(window).scrollTop() > amountScrolled ) {
		$('a.back-to-top').fadeIn('slow');
	} else {
		$('a.back-to-top').fadeOut('slow');
	}
});

$('a.back-to-top').click(function() {
   scrollToTop();
	//$('html, body').animate({
	//	scrollTop: 0
	//}, 700);
	return false;
});

function scrollToTop() {
   //verticalOffset = typeof(verticalOffset) != 'undefined' ? verticalOffset : 0;
   //element = $('body');
   //offset = element.offset();
   //offsetTop = offset.top;
   //$('html, body').animate({scrollTop: offsetTop}, 500, 'linear');
   $('html, body').animate({scrollTop: 0}, 500, 'linear');
   return false;
}


var last_query = "msvopmsofvjbs";
var startRow = 0;
function doQuery(a,b) {

   $('.typeahead').typeahead('close');
   $('#tftextinput').blur();
   
   if(!b) {
      b = "womens jeans";
      //return false;
   }
   
   if(b === last_query) {
      return false;
   }
   
   last_query = b;
   //hide('clearButton');
   //hide('newSearch');
   //
   //if (! $('#results').is(':empty')) {
   //   $( "#results" ).empty();
   //}
   
   if(b.length < 3) {
      bootbox.alert('Too many results, I need a longer word!');
      return false;
   }


   // $('#hlbutton').prop('disabled', false);
   b = b.trim(b);
   b = b.replace(/[#?=]/g, '');
   b = b.replace(/\s\s+/g, ' ');
   
   if(b.length < 3) {
      $('#tftextinput').val(b);
      bootbox.alert('Too many results, I need a longer word!');
      return false;
   }
   
   if(b.length > 60) {
      var words = b.split(' ');
      var num_words = words.length;
      if(num_words == 1) {
         b = str.substr(0, 60);
      }
      else {
         var space = ' ';
         var index = 1;
         var c = getPosition(b, space, index);
         if(c.length > 60) {
            b = str.substr(0, 60);
         }
         else {
            var d = c;
            while(c.length < 60) {
               ++index;
               c = getPosition(b, space, index);
               if(c.length < 60) {
                  d = c;
               }
            }
            b = d;
         }
      }
   }
   startRow = 0;
   window.location = '/search?query='+b+'&mindy';
   
   //$('#tftextinput').val(b);
   //
   //$.ajax({
   //   url: '/results?query='+b+'&mindy',
   //   cache: false,
   //   async: false,
   //})
   //.done(function( html ) {
   //   
   //   var count = (html.match(/title\-font/g) || []).length;
   //   if(count < 25) {
   //      hide('clearButton');
   //      show('newSearch');
   //   }
   //   else {
   //      show('clearButton');
   //      hide('newSearch');
   //   }
   //   $( "#results" ).append( html );
   //});
   //
   //hide('examples');
   //show('results');
   //
   ////window.scrollTo(0,0);
   //scrollToTop();
   //
   //startRow = 0;
   return false;
}

function doNewQuery(a,b) {

   $('.typeahead').typeahead('close');
   $('#trtextinput').blur();
   
   if(!b) {
      b = "e.g. womens jeans";
      //return false;
   }
   
   if(b === last_query) {
      return false;
   }
   
   last_query = b;
   //hide('clearButton');
   //hide('newSearch');
   
   //if (! $('#results').is(':empty')) {
   //   $( "#results" ).empty();
   //}
   
   if(b.length < 3) {
      bootbox.alert('Too many results, I need a longer word!');
      return false;
   }


   // $('#hlbutton').prop('disabled', false);
   b = b.trim(b);
   b = b.replace(/[#?=]/g, '');
   b = b.replace(/\s\s+/g, ' ');
   
   if(b.length < 3) {
      $('#trtextinput').val(b);
      bootbox.alert('Too many results, I need a longer word!');
      return false;
   }
   
   if(b.length > 60) {
      var words = b.split(' ');
      var num_words = words.length;
      if(num_words == 1) {
         b = str.substr(0, 60);
      }
      else {
         var space = ' ';
         var index = 1;
         var c = getPosition(b, space, index);
         if(c.length > 60) {
            b = str.substr(0, 60);
         }
         else {
            var d = c;
            while(c.length < 60) {
               ++index;
               c = getPosition(b, space, index);
               if(c.length < 60) {
                  d = c;
               }
            }
            b = d;
         }
      }
   }
   startRow = 0;
   window.location = '/search?query='+b+'&mindy';
   
   //$('#trtextinput').val(b);
   //
   //$.ajax({
   //   url: '/results?query='+b+'&mindy',
   //   cache: false,
   //   async: false,
   //})
   //.done(function( html ) {
   //   
   //   var count = (html.match(/title\-font/g) || []).length;
   //   if(count < 25) {
   //      hide('clearButton');
   //      show('newSearch');
   //   }
   //   else {
   //      show('clearButton');
   //      hide('newSearch');
   //   }
   //   $( "#results" ).append( html );
   //});
   //
   //hide('examples');
   //show('results');
   //
   ////window.scrollTo(0,0);
   //scrollToTop();
   //
   //startRow = 0;
   return false;
}


function shopQuery(a,b) {
   bootbox.alert('finally GLen');
   return(false);
}


function doDomainQuery(a,b) {
   
   // bootbox.alert('domain');
   

   if(!b || b === last_query || b.length < 3) {
      return;
   }
   b = b.trim(b);
   if(!b || b.length < 3) {
      return;
   }
   b = b.replace(/^https*:+\/+/, '');
   if(!b || b.length < 3) {
      return;
   }
   b = b.replace(/^www\./, '');
   if(!b || b.length < 3) {
      return;
   }
   
   if(b.length > 40) {
      bootbox.alert('40 characters total is our max. Sorry!');
      return;
   }
   b = b.toLowerCase();
   
   if(b.match(/[^a-z0-9\-.]/)) {
      bootbox.alert('Please use a legal domain URL, like "shoes.com"  or "www.shoes.com"');
      return;
   }
   
   var words = b.split('.');
   var num_words = words.length;
   if(num_words == 0 || num_words > 2) {
      bootbox.alert('Please use a single domain, like "shoes.com"  or "www.shoes.com"');
      return;
   }
      
   last_query = b;
   // bootbox.alert(b);
   
   // hide the virtual keyboard
   $('#tfdtextinput').blur();
   
   if (! $('#dresults').is(':empty')) {
      $( "#dresults" ).empty();
   }

   $.ajax({
      url: '/drequest?query='+b+'&simba',
      cache: false,
      async: false,
   })
   
   .done(function( html ) {
      $( "#dresults" ).append( html );
      
      //bootbox.alert(html);
   });

   hide('guidelines');
   show('dresults');
   $('#tfdtextinput').val(b);
   
   // var e = b + ' ' + String.fromCharCode(9650);
   // $('#scrollText').text(e);
   // $('html, body').animate({ scrollTop: 0 }, 0);
   //window.scrollTo(0, 0);
   startRow = 0;
}


function incResults() {
   
   if(last_query === "msvopmsofvjbs") {
      //return;
   }
   $('#tftextinput').val(last_query);
   
   //$('#hlbutton').prop('disabled', false);
   
   startRow += 25;
   $.ajax({
      url: '/results?query='+last_query+'&cosmo&start=' + startRow,
      cache: false,
      async: false,
   })

   .done(function( html ) {
      // var n = len.toString();
      // bootbox.alert(n);
      $( "#results" ).append( html );
      var count = (html.match(/title\-font/g) || []).length;
      if(count < 25) {
         hide('clearButton');
         show('newSearch');
      }
      else {
         show('clearButton');
         hide('newSearch');
      }

      $('html,body').animate({
         scrollTop: $(window).scrollTop() + 400
      })
   });
}


function getPosition(string, subString, index) {
   return string.split(subString, index).join(subString).length;
}



function clearSearch() {
   if (! $('#results').is(':empty')) {
      $( "#results" ).empty();
   }
   $('html, body').animate({ scrollTop: 0 }, 0);
   hide('clearButton');
   hide('newSearch');
   last_query = "msvopmsofvjbs";
}


$(function() {
   $('#queryDone').click(function() {
      $('body').removeHighlight();
      if (! $('#results').is(':empty')) {
         $( "#results" ).empty();
      }
      hideAll();
      var d = 'Scroll Up' + String.fromCharCode(8593);
      $('#scrollText').text(d);
      $('html, body').animate({ scrollTop: 0 }, 'slow');
   });
});




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

/*

$(function() {
   $('#scrollDown').click(function() {
      var bottom = $(document).height() - $(window).height() - $(window).scrollTop();
      $('html, body').animate({ scrollTop: bottom }, 'slow');
   });
});   


function showLinks(el) {
   $(el).removeClass('btn-default');
   $(el).addClass('btn-info');
}

function numColAction() {
   $('#results').removeClass('col-md-4');
   $('#results').addClass('col-sm-12');
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
   $(document).on( 'scroll', function() {
      if ($(window).scrollTop() > 100) {
         $('.scroll-top-wrapper').addClass('show');
      } else {
         $('.scroll-top-wrapper').removeClass('show');
      }
   });
   $('.scroll-top-wrapper').on('click', scrollToTop);
});
 

*/

 
		

