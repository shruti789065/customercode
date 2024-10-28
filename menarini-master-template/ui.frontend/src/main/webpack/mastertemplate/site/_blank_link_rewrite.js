import $ from "jquery";

$(function() {
    $('a[href][target="_blank"]').attr('rel', 'noopener noreferrer');
});