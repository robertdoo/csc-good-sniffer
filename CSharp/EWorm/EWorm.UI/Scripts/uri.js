/*    
    <Uri.js>
    Powered By: Techird
    Copyright 2012
*/

/*
    获得当前文档Uri及其相关段，可以用于查询、修改参数并获得新生成的Uri
*/
document.getUriSegments = function () {
    var segments = {};
    var uri = window.location.href;
    var matches = /((\w+):\/\/)?([^\/\&\?]+)(\/[^?&]*)?(([?&][^=]+?=[^?&]*){1,})/.exec(uri);

    /*协议名称（http/https）*/
    segments.protocal = matches[2];

    /*获得主机域名或IP地址*/
    segments.host = matches[3];

    /*获得相对于主机的路径*/
    segments.path = matches[4];
    if (segments.host.indexOf(':') > 0) {
        var splited = segments.host.split(':');
        segments.host = splited[0];

        /*获得Uri端口号*/
        segments.port = splited[1];
    }

    /*获得不含查询字符串部分的完整主机名和路径*/
    segments.fullPath = matches[1] + matches[3] + matches[4];

    /*获得查询字符串部分*/
    segments.query_string = matches[5];
    if (segments.query_string) {
        /*获得参数数组*/
        segments.params = [];
        var param_segs = segments.query_string.split(/[&?]/);
        for (var i in param_segs) {
            if (param_segs[i].indexOf('=') > 0) {
                var param = param_segs[i].split('=');
                segments.params.push({
                    /*参数的名称*/
                    name: param[0],
                    /*参数的值*/
                    value: param[1]
                });
            }
        }
    }

    /*移除指定名称或位置的参数*/
    segments.removeParam = function (indexOrName) {
        if (typeof (indexOrName) == "number" && indexOrName < segments.params) {
            segments.params.splice(indexOrName, 1);
            return segments.params;
        } else if (typeof (indexOrName) == "string") {
            for (var i in this.params) {
                if (this.params[i].name == name) {
                    return this.removeParam(i);
                }
            }
        }
    };

    /*设置参数的值*/
    segments.setParam = function (name, value) {
        for (var i in this.params) {
            if (this.params[i].name == name) {
                this.params[i].value = value;
                return this.params[i];
            }
        }
        var param = {
            name: name,
            value: value
        };
        this.params.push(param);
        return param;
    };

    /*获取参数*/
    segments.getParam = function (name) {
        for (var i in this.params) {
            if (this.params[i].name == name) {
                return this.params[i];
            }
        }
        return undefined;
    };

    /*获取参数的值*/
    segments.getParamValue = function (name) {
        var param = this.getParam(name);
        return param ? param.value : undefined;
    };

    /*根据当前参数构造Url*/
    segments.getGeneratedUri = function () {
        var uri = this.fullPath;
        uri += '?';
        for (var i in this.params) {
            uri += this.params[i].name + "=" + this.params[i].value;
            if (i != this.params.length - 1) {
                uri += "&";
            }
        }
        return uri;
    }
    return segments;
};