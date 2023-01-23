import jQuery from "jquery";

//caricamento pagina
//jQuery(function ($) {
jQuery(function () {
    // assegnazione tabindex agli input (testo e checkbox) e textarea del form nel ciclo each
    var tabindex = 1;
    jQuery('#new_form *').each(function(){
        var data_cmp_hook_form_text=jQuery(this).attr('data-cmp-hook-form-text');
        if (this.type == "text" || this.type == "checkbox" || data_cmp_hook_form_text=="input" || this.type == "SUBMIT" ) {
            var input = jQuery(this);
            input.attr("tabindex", tabindex);
            tabindex++;
        }
    });
    // assegnazione tabindex al submit dopo il ciclo
    var currentSubmit = jQuery('#new_form').closest('form').find(':submit');
    currentSubmit.attr("tabindex", tabindex);
    //invio disabilitato poichè sicuramente il checkbox della privacy è disabilitato
    disableSubmit();
});
//click checkbox: se checkbox privacy disabilitato invio disabilitato, altrimenti viene abilitato
jQuery('.cmp-form-options__field--checkbox').on("click", function() {
    if (jQuery("button[type='submit']").attr('disabled')) {
        enableSubmit();
    } else {
        disableSubmit();
    }
});
//click o pressione invio
jQuery("button[type='submit']").on("click keypress", function() {
    //ciclo nei campi del form
    jQuery('#new_form *').filter(':input').each(function(){
        //se campo non hidden e non è checkbox vengono eseguiti i controlli
        if (jQuery(this).attr('type')!="hidden" && jQuery(this).attr('type')!="checkbox"){
            //controllo che sia preesnte data-cmp-required-message, quindi obbligatorio; se sì vengono eseguiti i controlli
            var attr_to_check = jQuery(this).parent().attr('data-cmp-required-message');
            if (typeof attr_to_check !== 'undefined' && attr_to_check !== false) {
                if( jQuery(this).val().length == 0 ) {
                    jQuery(this).css("border","3px solid #a94442");
                    if(jQuery(this).parent().find('.label_required').length==0){
                        jQuery(this).parent().append( "<p class='label_required'>This field is required</p>" );
                    }
                }
                else{
                    jQuery(this).css("border","3px solid #000");
                    if(jQuery(this).parent().find('.label_required').length){
                        jQuery(this).parent().find('.label_required').remove();
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
                        //email non valida e non esiste messaggio di errore e mail ha almeno un carattere
                        else{
                            jQuery(this).css("border","3px solid #a94442");
                            if(jQuery(this).parent().find('.label_email_not_valid').length==0 & jQuery(this).parent().find('.label_required').length==0){
                                jQuery(this).parent()
                                .append( "<p class='label_email_not_valid'>Please enter a valid email address</p>" );
                            }
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
}); 
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
