var PhantAuth = (function () {

    var flags = {
        "noemail":"mail", "detect":"mail", "plus":"mail", "dot":"mail",
        "noavatar":"avatar", "ai":"avatar", "sketch":"avatar", "photo":"avatar", "dice":"avatar", "kitten":"avatar", "adorable":"avatar", "notfound":"avatar", "mp":"avatar", "identicon":"avatar", "monsterid":"avatar", "wavatar":"avatar", "retro":"avatar", "robohash":"avatar", "blank":"avatar",
        "nologo":"logo", "icon":"logo", "fractal":"logo",
        "nogender":"gender", "guess":"gender", "male":"gender","female":"gender",
        "tiny":"size","small":"size","medium":"size","large":"size", "huge":"size"
    }

    var parseFlags = function(input) {
        var map = {}
        if ( ! input ) {
            return map;
        }

        var fields = input.split(";");
        for(var i = 0; i<fields.length; i++) {
            if ( flags[fields[i]] ) {
                map[flags[fields[i]]] = fields[i];
            }
        }
        return map;
    }

    var me = document.getElementById("phantauth");
    var tenant = JSON.parse(me.getAttribute("data-tenant"));
    tenant.userinfo |= "";
    tenant.flag = parseFlags(tenant.flags || "");
    var historyItem = "login.history@" + tenant.sub;

    function submitConsent(value) {
        var form = document.getElementById("hidden");
        var input = document.querySelector("#hidden input[name=consent]");
        input.value = value == null ? "cancel" : value;
        form.submit();
    }

    var onConsentSubmit = function (event) {
        event.preventDefault();

        var scopes = ["profile", "email", "phone", "address", "uid"]
        var value = "openid";
        for (var i = 0; i < scopes.length; i++) {
            var name = scopes[i]
            if (event.target.elements[name] && event.target.elements[name].checked) {
                value += " " + name;
            }
        }
        submitConsent(value);
        return false;
    }

    var onCancel = function (event) {
        event.preventDefault()
        submitConsent(null);
    }

    function unique(arr) {
        var u = {}, a = [];
        for (var i = 0, l = arr.length; i < l; ++i) {
            if (!u.hasOwnProperty(arr[i])) {
                a.push(arr[i]);
                u[arr[i]] = 1;
            }
        }
        return a;
    }

    function getLoginHistory() {
        return localStorage.getItem(historyItem) ? JSON.parse(localStorage.getItem(historyItem)) : [];
    }

    function addToLoginHistory(username) {
        var history = getLoginHistory()
        history.splice(0, 0, username)
        history = unique(history)
        if (history.length > 10) {
            history.shift()
        }
        localStorage.setItem(historyItem, JSON.stringify(history));
    }

    var get = function (kind, sub, callback) {
        $.ajax({
            url: tenant.issuer + '/' + kind + '/' + encodeURIComponent(sub),
            success: function (resource) {
                resource["flag"] = parseFlags(resource["flags"] || resource["sub"]);
                callback(resource);
            }
        });
    }

    var post = function (kind, data, callback) {
        $.ajax({
            type: "POST",
            data: JSON.stringify(data),
            contentType: 'application/json',
            dataType: 'text',
            success: function (resource) {
                callback(resource);
            }
        });
    }

    function login(username, password) {
        if (!password) {
            get("user", username, function (user) {
                login(user.sub, user.password);
            });
        } else {
            authorize(username, password);
        }
    }

    var submitLogin = function (value) {
        var form = document.getElementById("hidden");
        var input = document.querySelector("#hidden input[name=login_token]");
        input.value = value;
        form.submit();
    }

    function authorize(username, password) {
        addToLoginHistory(username);
        var scope = document.querySelector("#hidden input[name=scope]").value;
        $.ajax({
            url: tenant.issuer + '/auth/token',
            method: 'POST',
            data: {
                username: username,
                password: password,
                grant_type: 'password',
                scope: scope
            },
            success: function (auth) {
                submitLogin(auth["login_token"]);
            }
        });
    }

    function newPlainJWT(playload) {
        function b64(str) {
            return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
            function toSolidBytes(match, p1) {
                return String.fromCharCode('0x' + p1);
            }));
        }
        var token = "eyJhbGciOiJub25lIn0.";
        token += b64(JSON.stringify(playload)).replace(/\+/g, '-').replace(/\//g, '_').replace(/\=+$/, '')
        token += ".";
        return token;
    }

    var onLoginSubmit = function (event) {
        event.preventDefault();
        var username = event.target.elements["username"].value;
        if ( username && username.charAt(0) == '{' ) {
            username = newPlainJWT(JSON.parse(username));
        }
        var password = event.target.elements["password"];
        if (password) {
            login(username, password.value);
        } else {
            login(username);
        }
        return false;
    }

    var onSelectSubmit = function (event) {
        event.preventDefault();
        var subtenant = event.target.elements["subtenant"].value;
        var form = document.getElementById("hidden");
        form.action = form.action.replace(/(\/_[^/]*)/, "\$1~" + subtenant);
        form.submit();
        return false;
    }

    var consentForm = document.querySelector("form.consent-form");
    if (consentForm) {
        consentForm.addEventListener("submit", onConsentSubmit);
        consentForm.addEventListener("reset", onCancel);
    }

    var loginForm = document.querySelector("form.login-form");
    if (loginForm) {
        loginForm.addEventListener("submit", onLoginSubmit);
        loginForm.addEventListener("reset", onCancel);
    }

    var selectForm = document.querySelector("form.select-form");
    if (selectForm) {
        selectForm.addEventListener("submit", onSelectSubmit);
        selectForm.addEventListener("reset", onCancel);
    }

    var loginHistory = getLoginHistory();
    if (loginHistory.length > 0) {
        var historyList = document.querySelector("datalist.login-history");
        if (historyList) {
            for (var i = 0, len = loginHistory.length; i < len; i++) {
                historyList.append(new Option(loginHistory[i]));
            }
        }
        var lastLogin = document.querySelector("input.login-history");
        if (lastLogin) {
            lastLogin.value = loginHistory[0]
        }
    }

    return { tenant: tenant, get: get, post: post}
})();
