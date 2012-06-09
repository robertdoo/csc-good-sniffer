/// <reference path="jquery-1.5.1-vsdoc.js" />
/// <reference path="uri.js" />


var selectSort = function () {
    var value = this.value;
    var query = document.getUriSegments();
    query.setParam('o', value);
    if (query.getParamValue('d') == 'true') {
        query.removeParam('d');
    } else {
        query.setParam('d', 'true');
    }
    document.location.href = query.getGeneratedUri();
};

$(document).ready(function () {
    $("#sel-sort-option").change(selectSort);
});