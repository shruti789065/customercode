$(document).ready(function ($) {
	
	//lingua in mobile 
	$('#language-selector-title').click(function() {
		$('#language-selector-list').toggle('fast');
	});

    //uppermenu rosso in alto
    $('.HeaderTopPane .navbar-collapse .nav-item .dropdown').click(function() {
        if( $('.HeaderTopPane .navbar-collapse .dropdown').hasClass('show')) {
            /*
            $('.dnnbooster header .topbar').css("background-color", "#F5333F");
            */
        }
        else {
            $('.dnnbooster header .topbar').css("background-color", "#eeeee4");
        }
    });

    /*--- cambio placeholder search ---*/
    if ( $('html').attr('lang') == 'en-US' ) {
        $('.searchPane  .form-control.search-box.typeahead.tt-input').attr('placeholder', 'Input your search...');
    } else {
        $('.searchPane  .form-control.search-box.typeahead.tt-input').attr('placeholder', 'Cerca nel sito...');
    }
    /*--- fine ---*/

    /*--- switch menu ita eng ---*/
    if ( $('html').attr('lang') == 'en-US' ) {
        $(".cultureITA").remove();
    }  
    if ( $('html').attr('lang') == 'it-IT' ) {
        $(".cultureENG").remove();
    }
    /*--- fine ---*/

    /*----- menu non si ciude al click -----*/
    $(".headerMenu .dropdown-menu > *").not(".headerMenu .dropdown-menu > a.nav-link").click(function(noClose) {
        console.log('stop propagation 1');
        noClose.stopPropagation(); 
        console.log('stop propagation 2');
    });
    $(".bg_topMenuCountrues").click(function(noCloseTopMenuC) {
        noCloseTopMenuC.stopPropagation(); 
    });
    $(".bg_topMenuContactUs").click(function(noCloseTopMenuD) {
        noCloseTopMenuD.stopPropagation(); 
    });

    /*$(".headerMenu .dropdown-menu a.nav-link").click(function() {
        console.log('il menu non si chiude 1');
        $(this).closest(".headerMenu .dropdown-menu").dropdown("toggle");
        console.log('il menu non si chiude 2');
    });*/
    
    /*----- fine -----*/

});

/*-------------------------------------------------- BROWSER DETECTION --------------------------------------------------*/
 var BrowserDetect = {
        init: function () {
            this.browser = this.searchString(this.dataBrowser) || "Other";
            this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || "Unknown";
        },
        searchString: function (data) {
            for (var i = 0; i < data.length; i++) {
                var dataString = data[i].string;
                this.versionSearchString = data[i].subString;

                if (dataString.indexOf(data[i].subString) !== -1) {
                    return data[i].identity;
                }
            }
        },
        searchVersion: function (dataString) {
            var index = dataString.indexOf(this.versionSearchString);
            if (index === -1) {
                return;
            }
            var rv = dataString.indexOf("rv:");
            if (this.versionSearchString === "Trident" && rv !== -1) {
                return parseFloat(dataString.substring(rv + 3));
            } else {
                return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
            }
        },
        dataBrowser: [
            {string: navigator.userAgent, subString: "Edge", identity: "MS Edge"},
            {string: navigator.userAgent, subString: "MSIE", identity: "Explorer"},
            {string: navigator.userAgent, subString: "Trident", identity: "Explorer"},
            {string: navigator.userAgent, subString: "Firefox", identity: "Firefox"},
            {string: navigator.userAgent, subString: "Opera", identity: "Opera"},  
            {string: navigator.userAgent, subString: "OPR", identity: "Opera"},  
            {string: navigator.userAgent, subString: "Chrome", identity: "Chrome"}, 
            {string: navigator.userAgent, subString: "Safari", identity: "Safari"}       
        ]
    };
    BrowserDetect.init();
    //document.write("You are using <b>" + BrowserDetect.browser + "</b> with version <b>" + BrowserDetect.version + "</b>");

    var bv= BrowserDetect.browser;
    if( bv == "Chrome"){
        $("body").addClass("chrome");
    }
    else if(bv == "MS Edge"){
     $("body").addClass("edge");
    }
    else if(bv == "Explorer"){
     $("body").addClass("ie");
    }
    else if(bv == "Firefox"){
     $("body").addClass("Firefox");
    }
   /*-------------------------------------------------- FINE BROWSER DETECTION --------------------------------------------------*/

//Cloak Email 
$(".cemail").each(function(){
    var ats, dots, address, i;
    ats = ['(at)'];
    dots = ['(dot)'];
    address = $(this).html();
    for ( i = 0; i < ats.length; i++ ) {
        address = address.replace(ats[i], '@');
    }
    for ( i = 0; i < dots.length; i++ ) {
        address = address.replace(dots[i], '.');
    }
    $(this).html('<a href="mailto:' + address + '">' + address + '</a>');
});

//modifica classe header a scroll pagina
$(window).scroll(function(){  
    if ($(this).scrollTop() > 40) {
        if($(".headerMenu .nav-item").hasClass("show") == false){
            $('header').addClass("scrolled-page");
            $('body').addClass("scrolled-page");
        }
    }else{
        if($(".headerMenu .nav-item").hasClass("show") == false){
            $('header').removeClass("scrolled-page");
            $('body').removeClass("scrolled-page");
        }
    }   
});

//accordion toggle: assegnazione sì/no classe highlighted
$( "a.accordionLink" ).click(function() {
  $( this ).toggleClass( "highlighted" );
});

//mobile main menu
$(".headerMenu .navbar-toggler").click(function() {
    if ($("body").hasClass("mobile_mainmenu_opened")) {
        $("body").removeClass("mobile_mainmenu_opened");
    }else{
        $("body").addClass("mobile_mainmenu_opened");
    }
});
//mobile main menu II livello
$(".mainMenuMobile .dropdown-menu-toggler").click(function() {
    if ($(".mainMenuMobile .dropdown.open.show .dropdown-menu").hasClass("show")) {
        $(".mainMenuMobile .dropdown.open.show .dropdown-menu").removeClass("show");
        console.log('menu 2°lvl mobile chiuso');
    }else{
        console.log('il menu va aperto prima');
    }
});

/*-------------------------------------------------- menu --------------------------------------------------*/


