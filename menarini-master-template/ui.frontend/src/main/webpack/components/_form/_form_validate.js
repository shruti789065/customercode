

import jQuery from "jquery";

//caricamento pagina

jQuery(function () {
    // assegnazione tabindex agli elementi selezionabili dall'utentedel form
    var tabindex = 1;
    jQuery('#new_form *').each(function(){
        var data_cmp_hook_form_text=jQuery(this).attr('data-cmp-hook-form-text');
        if (this.type == "text" || this.type == "checkbox" || data_cmp_hook_form_text=="input" || this.name == "info" || this.type == "radio" || this.type == "SUBMIT" ) {
            var input = jQuery(this);
            input.attr("tabindex", tabindex);
            tabindex++;
        }
    });
    // assegnazione tabindex al submit dopo il ciclo
    var currentSubmit = jQuery('#new_form').closest('form').find(':submit');
    currentSubmit.attr("tabindex", tabindex);
});

jQuery("button[type='submit']").on("click keypress", function() {
  $('#new_form').on('submit', function(e){
    //controllo valori input
    var inputsValid=validateInputs();
    //controllo valori radio button
    var radiosValid=validateRadios();
    //controllo valori recaptcha
    //var recaptchaValid=validateRecaptcha();
    var valid=inputsValid && radiosValid;
    //var valid=inputsValid && radiosValid && recaptchaValid;
    if(!valid) {
      e.preventDefault();
    }
  });
});
//restituisce true se recaptcha validato, false altrimenti
function validateRecaptcha(){
    var tokenRecaptcha = document.getElementById('g-recaptcha-response').value;
    if( tokenRecaptcha === null || tokenRecaptcha === "" ){
    alert('Please, fill the recaptcha');
        return false;
    }
    return true;
}
//restituisce true se input validati, false altrimenti
function validateInputs(){
    var inputsValid=true;
    jQuery('#new_form *').filter(':input').each(function(){   
            //se campo non hidden e non è checkbox vengono eseguiti i controlli
            if (jQuery(this).attr('type')!="hidden" && jQuery(this).attr('type')!="checkbox"){
                /*controllo che sia presente data-cmp-required-message, quindi obbligatorio; 
                se sì vengono eseguiti i controlli*/
                var attr_to_check = jQuery(this).parent().attr('data-cmp-required-message');
                if (typeof attr_to_check !== 'undefined' && attr_to_check !== false) {
                    //se è select
                    if(jQuery(this).is("select")) {
                        var idSelect=jQuery(this).attr("id");
                        var valueSelected=jQuery('#'+idSelect).find(":selected").attr("value");
                        if ( valueSelected === undefined) {
                            jQuery(this).css("border","3px solid #a94442");
                            if(jQuery(this).parent().find('.label_required').length==0){
                                jQuery(this).parent().append( "<p class='label_required'>This field is required</p>" );
                            }
                            inputsValid=false;
                        }
                        else{
                            jQuery(this).css("border","3px solid #000");
                            if(jQuery(this).parent().find('.label_required').length){
                                jQuery(this).parent().find('.label_required').remove();
                            }
                        }                       
                    }
                    //se non è select
                    else{
                        if((jQuery(this).val().length == 0) ) {
                            jQuery(this).css("border","3px solid #a94442");
                            if(jQuery(this).parent().find('.label_required').length==0){
                                jQuery(this).parent().append( "<p class='label_required'>This field is required</p>" );
                            }
                            inputsValid=false;
                        }
                        else{
                            jQuery(this).css("border","3px solid #000");
                            if(jQuery(this).parent().find('.label_required').length){
                                jQuery(this).parent().find('.label_required').remove();
                            }
                        }
                    }

                    //validazione email
                    if(jQuery(this).attr('type')=="email"){
                        var email= jQuery(this).val();
                        var emailFormat = validateEmail(email);
                        //se email valorizzata
                        if( email.length > 0){
                            //email valida
                            if(emailFormat==true){
                               jQuery(this).css("border","3px solid #000");
                                if(jQuery(this).parent().find('.label_email_not_valid').length){
                                    jQuery(this).parent().find('.label_email_not_valid').remove();
                                    jQuery(this).css("border","3px solid #000");
                                }
                            }//fine email valida
                            //email non valida e non esiste messaggio di errore e mail ha almeno un carattere;
                            //se esiste anche la label "required" la toglie
                            else{
                                jQuery(this).css("border","3px solid #a94442");
                                if(jQuery(this).parent().find('.label_email_not_valid').length==0 & jQuery(this).parent().find('.label_required').length==0){
                                    jQuery(this).parent()
                                    .append( "<p class='label_email_not_valid'>Please enter a valid email address</p>" );
                                    if(jQuery(this).parent().find('.label_required').length){
                                        jQuery(this).parent().find('.label_required').remove();
                                    }
                                }
                                inputsValid=false;
                            }//fine email non valida
                        }//fine email valorizzata
                        //se email non valorizzata e esiste la label di mail non valida, viene rimossa
                        else{
                            if(jQuery(this).parent().find('.label_email_not_valid').length){
                                jQuery(this).parent().find('.label_email_not_valid').remove();
                            }
                        }
                    }//fine validazione email
             } //fine if campo obbligatorio
            }//fine if controllo che campo non sia hidden
        });//fine loop sugli input
    return inputsValid;
}
//restituisce true se radio validati, false altrimenti
function validateRadios(){
    var radiosValid=true;
    var isRequired=0;
    jQuery('#new_form *').filter(':radio').each(function(){  
    //il messaggio di errore viene se c'è un checked sul no e il campo è required; si presuppone
    //che il sì sia sempre primo, allora valorizzo una variabile isRequired
    //siamo sul valore si
        if(jQuery(this).attr('value')=="si" | jQuery(this).attr('value')=="yes" | jQuery(this).attr('value')=="1" | jQuery(this).attr('value')=="si"){
            //valore si e obbligatorio
            if(jQuery(this).attr('required')){
                isRequired=1;
                //valore si, obbligatorio e checked
                if(jQuery(this).is(':checked')){
                    if(jQuery(this).parent().siblings('.label_required').length){
                        jQuery(this).parent().siblings('.label_required').remove();
                    }
                }
                //valore si, obbligatorio e non checked
                else{
                }
            }
            //valore no, non obbligatorio
            else{
                isRequired=0;  
            }
        }
        //siamo sul valore no
        if(jQuery(this).attr('value')=="no" | jQuery(this).attr('value')=="0" | jQuery(this).attr('value')=="false"){
            //valore no e checked
            if(jQuery(this).is(':checked')){
                //obbligatorio e checked no
                if(isRequired==1){
                    radiosValid=false;
                    if(jQuery(this).parent().siblings('.label_required').length==0){
                        jQuery(this).parent().after( '<p class="label_required">This field is required</p>' );
                    }
                }
                //non obbligatorio e checked no
                else{
                    if(jQuery(this).parent().siblings('.label_required').length){
                        jQuery(this).parent().siblings('.label_required').remove();
                    }
                }
            }
        }
    });
    return radiosValid;
}
//restituisce true se formato email valido, false altrimenti
function validateEmail(email){
    var re = /^\w+([-+.'][^\s]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
    var emailFormat = re.test(email);
    return emailFormat;
}
//disabilita il tasto submit ed i suoi effetti all'evento hover
function disableSubmit(){
    jQuery("button[type='submit']").attr('disabled', 'true');
    jQuery("button[type='submit']").css('cursor', 'not-allowed');
    jQuery("button[type='submit']").css('background-color', '#EDEFF3');
    jQuery("button[type='submit']").on("mouseenter", function(){ 
        jQuery("button[type='submit']").css('-webkit-transform', 'none');
        jQuery("button[type='submit']").css('transform', 'none');
    }).on("mouseleave", function(){
        jQuery("button[type='submit']").css('-webkit-transform', 'none');
        jQuery("button[type='submit']").css('transform', 'none');
    });
}
//abilita il tasto submit ed i suoi effetti all'evento hover
function enableSubmit(){
    jQuery("button[type='submit']").removeAttr('disabled');
    jQuery("button[type='submit']").css('cursor', 'pointer');
    jQuery("button[type='submit']").css('background-color', '#B62623');
    jQuery("button[type='submit']").on("mouseenter", function(){ 
        jQuery("button[type='submit']").css('-webkit-transform', 'scale(1.02)');
        jQuery("button[type='submit']").css('transform', 'scale(1.02)');
    }).on("mouseleave", function(){
        jQuery("button[type='submit']").css('-webkit-transform', 'none');
        jQuery("button[type='submit']").css('transform', 'none');
    });
}
