/// <reference path="jquery-1.5.1-vsdoc.js" />

/*
格式化字符串 format(str, param1, param2, ...)
*/
String.format = function () {
    var args = arguments;
    var str = args[0];
    var param_pattern = /\{(\d+)\}/g;
    str = str.replace(param_pattern, function () {
        var param_index = arguments[1] * 1;     // arguments[1] 是(\d+)的结果
        return args[param_index + 1];
    });
    return str;
}

/* 获得应用程序根目录 */
var getRootUrl = function () {
    if (document._root_url_) return document._root_url_;
    var js = document.scripts;
    var jsPath;
    for (var i = 0; i < js.length; i++) {
        if (js[i].src.indexOf("monitor.js") > -1) {
            jsPath = js[i].src.substring(0, js[i].src.lastIndexOf("/") + 1);
        }
    }
    var rootPattern = /(.+?\/)Scripts/;
    document._root_url_ = rootPattern.exec(jsPath)[1];
    return document._root_url_;
};

var loadJobQueue = function () {
    $.ajax({
        url: getRootUrl() + "Crawler/JobQueue",
        type: 'GET',
        success: loadJobQueueSuccess
    });
};

var loadJobQueueSuccess = function (result) {
    if (result.has_result) {
        var job_queue = result.job_queue;
        var current_job = result.current_job;
        var html = "";
        html += String.format('<p class="current-job">{0}</p>', current_job);
        for (var i in job_queue) {
            html += String.format('<p>{0}</p>', job_queue[i]);
        }
        $("#job-queue").html(html);
    } else {
        $("#job-queue").html("爬虫启动中...");
    }
    setTimeout("loadJobQueue();", 300);
}

var loadKeywordQueue = function () {
    $.ajax({
        url: getRootUrl() + "Crawler/KeywordQueue",
        type: 'GET',
        success: loadKeywordQueueSuccess
    });
};

var loadKeywordQueueSuccess = function (result) {
    if (result.has_result) {
        var keyword_queue = result.keyword_queue;
        var html = "";
        for (var i in keyword_queue) {
            html += String.format('<p>{0}</p>', keyword_queue[i]);
        }
        $("#keyword-queue").html(html);
    } else {
        $("#keyword-queue").html("爬虫启动中...");
    }
    setTimeout("loadKeywordQueue();", 300);
};

$(document).ready(function () {
    loadJobQueue();
    loadKeywordQueue();
});