(function( window, undefined ) {
	var client = window.i2b2WCI;
	if(client == null){
		client = window.i2b2WCI = {};
	}

	client.SessionLinks = [];
	client.NoSessionLinks = [];
	client.Prerequisites = [];
	client.Events = {};
	client.Events.BeforeInit = [];
	client.Events.BeforeLogin = [];
	client.Events.AfterLogin = [];
	client.Credentials = {};
	client.Projects = [];

	client.haltLogin = false;
	
	client.doEvents = function(events){
		if(events && events.length > 0){
			for(var i = 0; i < events.length; i++){
				if(events[i]() == false){
					return false;
				}
			}
		}
	}
	
	client.doBeforeInit = function(){
		if(client.Prerequisites.length > 0){
			var message = "Unable to proceed until the following prerequisites are met: \n\n";
			for(var i = 0; i < client.Prerequisites.length; i++){
				message += " - " + client.Prerequisites[i] + "\n";
			}
			alert(message);
			client.haltLogin = true;
			window.location = document.URL.substr(0, document.URL.lastIndexOf("/") + 1);
		}
		else{
			if(client.doEvents(client.Events.doBeforeInit) == false){
				client.haltLogin = true;
			}
		}
	};
	
	client.doBeforeLogin = function(loginFx){
		if(client.haltLogin != true){
			if(false != client.doEvents(client.Events.BeforeLogin) && loginFx != null){
				client.setLoginDialog();
				loginFx();
			}
		}
	};
	
	client.doAfterLogin = function(){
		client.Credentials = {};
		i2b2.PM.model.html.loginDialog = null;
		return client.doEvents(client.Events.AfterLogin);
	};

	client.doLogin = function(){
		// Check If Selection Is A Local URL Redirect
		var domains = i2b2.PM.model.Domains;
		var select = i2b2.PM.udlogin.inputDomain;
		var val = select.options[select.selectedIndex].value;
		if (!val.blank() && domains[val]) {
			var domain = domains[val];
			var redirect = domain.redirect;
			if(redirect){
				window.open(i2b2WCI.toURL(redirect));
				return;
			}
		}
		// If we are here, do normal login
		i2b2.PM.doLogin();
	};
	
	client.setLoginDialog = function(){
		var user = (client.Credentials.UserID || "");
		var pass = (client.Credentials.Password || "");
		i2b2.PM.model.html.loginDialog = '<div id="i2b2_login_modal_dialog" style="display:block;">\n'+
			'	<div class="hd">i2b2 Login</div>\n'+
			'	<div class="bd login-dialog">\n'+
			'		<form name="loginForm" style="margin:0;padding:0;" onsubmit="return false;">\n'+
			'			<input type="hidden" name="uname" id="loginusr" value="' + user + '"/>\n'+
			'			<input type="hidden" name="pword" id="loginpass" value="' + pass + '"/>\n'+
			'			<div id="loginMessage">Login incorrect or host not found.</div>\n'+
			'			<div class="formDiv">\n'+
			'				<div class="label">i2b2 Host:</div>\n'+
			'				<div class="input"><select name="server" id="logindomain"><option value="">Loading...</option></select></div>\n'+
			'				<div class="button"><input type="button" value="  Select  " onclick="i2b2WCI.doLogin()" /></div>\n'+	
			'			</div>\n'+
			'		</form>\n'+
			'	</div>\n'+
			'</div>\n';
	};

	client.toURL = function(path){
		var url = window.location.href;
		var baseUrl = url.substring(0, url.indexOf("/viewer.htm"));
		if(path.indexOf("/") == 0){
			return baseUrl + path;
		}
		else{
			return baseUrl + "/" + path;
		}
	};
})(window);
