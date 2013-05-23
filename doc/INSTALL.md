## Installing I2B2 Web Client Integration Platform

The Web Client Integration (WCI) installation instructions provided make the following assumptions:

* You have successfully installed I2B2 services on a JBoss server
* You have successfully installed the default webclient on an Apache HTTPd server.
* You are using/installing I2B2 version 1.6.8.

## Apache HTTPd Configuration

The Apache HTTPd configuration below makes only a few assumptions.  If any of those assumptions do not match your setup you will need to make changes accordingly.  The assumptions are:

* I2B2 is accessed at a root path.  IE, got to http://i2b2.my.org/ to access I2B2.  No context path.
* Access to I2B2 must be over HTTPS.

Configuration:

    # ------------------------------------------------------------------------------------ #
    # Apache Virtual Host Mappings For I2B2 Web Client & Integration Platform
    # ------------------------------------------------------------------------------------ #
    NameVirtualHost *:80
    <VirtualHost *:80>
        # ------------------------------------------------------------------------------------ #
    	# Redirect All HTTP Requests To HTTPS
    	# ------------------------------------------------------------------------------------ #
    	ServerName i2b2.faber.edu
    	Redirect permanent / https://i2b2.faber.edu/
    </VirtualHost>
    
    NameVirtualHost *:443
    <VirtualHost *:443>
        # ------------------------------------------------------------------------------------ #
        # Virtual Server Basic Configuration
        # ------------------------------------------------------------------------------------ #
        ServerName i2b2.faber.edu
        DocumentRoot "/path/to/webclient"
    
        # ------------------------------------------------------------------------------------ #
        # SSO & SSL Configuration Here
        # ------------------------------------------------------------------------------------ #
        REPLACE ME WITH REAL STUFF
        
        # ------------------------------------------------------------------------------------ #
        # Proxy Configuration
        # ------------------------------------------------------------------------------------ #
        ProxyPass /assets !
        ProxyPass /help !
        ProxyPass /js-ext !
        ProxyPass /js-i2b2 !
        ProxyPass /viewer.htm !
        ProxyPass /i2b2_config_data.js !
        ProxyPass /index.php !
        ProxyPass / ajp://i2b2jboss.faber.edu:8009/
    </VirtualHost>


## Default Web Client Integration

One of the motivating factors behind the WCI project was to provide a way to integrate and extend the default I2B2 webclient with as little refactoring as possible.  With the default webclient having a single web view (default.htm) that is presented to the user, our integration refactors are isolated to that file.

### Step 1.  Rename ``default.htm`` To ``viewer.htm``.

We do not want the HTTPd server to redirect from any path to the view automatically, and it provides an immediate flag that this file is not your provided default.htm file.

### Step 2.  Add Script Tag To ``viewer.htm``.

The WCI provides a dynamically generated script that contains the integration hooks for the webclient.  That script needs to be injected into the viewer.htm file to provide the integration.  To inject, a script tag needs to be added immediately after the call to the ``js-i2b2/i2b2_loader.js`` script call. 

Original HTML markup before adding the script (Starting at line 156 in I2b2 version 1.6.8):

```html
	<!-- load i2b2 framework -->
	<script type="text/javascript" src="js-i2b2/i2b2_loader.js"></script>
	<link type="text/css" href="assets/i2b2.css" rel="stylesheet" />
	<link type="text/css" href="assets/i2b2-NEW.css" rel="stylesheet" />
```

Modified HTML markup after adding the script:

```html
	<!-- load i2b2 framework -->
	<script type="text/javascript" src="js-i2b2/i2b2_loader.js"></script>
	<script type="text/javascript" src="scripts/webclient/bootstrap.js"></script>
	<link type="text/css" href="assets/i2b2.css" rel="stylesheet" />
	<link type="text/css" href="assets/i2b2-NEW.css" rel="stylesheet" />
```

### Step 3.  Add JavaScript Hooks To ``viewer.htm``.

While the script ``scripts/webclient/bootstrap.js``  contains the integration logic, you need to intercept the normal operations of the webclient in order for it to be effective.

#### Init Hook

The first hook to intercept normal operations is a call prior to I2B2 initializing itself.

Original JavaScript before adding the hook  (Starting at line 241 in I2b2 version 1.6.8):

```javascript
	function init() {
		// ------------------------------------------------------
		// put any pre-i2b2 initialization code here
		// ------------------------------------------------------

		// initialize the i2b2 framework
		initI2B2();
	}
```

Modified JavaScript after adding the hook  (Starting at line 241 in I2b2 version 1.6.8):
```javascript
	function init() {
		// ------------------------------------------------------
		// put any pre-i2b2 initialization code here
		// ------------------------------------------------------
		i2b2WCI.doBeforeInit();
		// initialize the i2b2 framework
		initI2B2();
	}
```

#### Pre-Login Hook

The secod hook to intercept normal operations is to wrap the call to perform the login:

Original JavaScript before adding the hook  (Starting at line 173 in I2b2 version 1.6.8):
```javascript
	function initI2B2() 
	{
		//debugOnScreen("default.htm.initI2B2: browserViewPort = " + initBrowserViewPortDim.width + " " + initBrowserViewPortDim.height );
		i2b2.events.afterCellInit.subscribe(
			(function(en,co,a) {
				var cellObj = co[0];
				var cellCode = cellObj.cellCode;
				switch (cellCode) {
					case "PM":
						// This i2b2 design implementation uses a prebuild login DIV we connect the Project Management cell to
						// handle this method of login, the other method used for login is the PM Cell's built in floating
						// modal dialog box to prompt for login credentials.  You can edit the look and feel of this dialog box
						// by editing the CSS file.  You can remark out the lines below with no ill effect.  Use the following
						// javascript function to display the modal login form: i2b2.hive.PM.doLoginDialog();
						//cellObj.doConnectForm($('loginusr'),$('loginpass'),$('logindomain'), $('loginsubmit'));
						i2b2.PM.doLoginDialog();
						break;
				}
			})
		);
		...
	}
```

Modified JavaScript after adding the hook  (Starting at line 173 in I2b2 version 1.6.8):

```javascript
	function initI2B2() 
	{
		//debugOnScreen("default.htm.initI2B2: browserViewPort = " + initBrowserViewPortDim.width + " " + initBrowserViewPortDim.height );
		i2b2.events.afterCellInit.subscribe(
			(function(en,co,a) {
				var cellObj = co[0];
				var cellCode = cellObj.cellCode;
				switch (cellCode) {
					case "PM":
						// This i2b2 design implementation uses a prebuild login DIV we connect the Project Management cell to
						// handle this method of login, the other method used for login is the PM Cell's built in floating
						// modal dialog box to prompt for login credentials.  You can edit the look and feel of this dialog box
						// by editing the CSS file.  You can remark out the lines below with no ill effect.  Use the following
						// javascript function to display the modal login form: i2b2.hive.PM.doLoginDialog();
						//cellObj.doConnectForm($('loginusr'),$('loginpass'),$('logindomain'), $('loginsubmit'));
						i2b2WCI.doBeforeLogin(i2b2.PM.doLoginDialog);
						break;
				}
			})
		);
		...
	}
```

#### Post-Login Hook

The last hook to intercept normal operations is a call after the login process is successful:

Original JavaScript before adding the hook  (Insert at line 238 in I2b2 version 1.6.8):

```javascript
function initI2B2() 
{
	...
	// start the i2b2 framework
	i2b2.Init();
}
```

Modified JavaScript after adding the hook  (Insert at line 238 in I2b2 version 1.6.8):

```javascript
function initI2B2() 
{
	...
	i2b2.events.afterLogin.subscribe(i2b2WCI.doAfterLogin);
	// start the i2b2 framework
	i2b2.Init();
}
```

## License

Copyright © 2013 Health Sciences of South Carolina

Distributed under the Eclipse Public License, the same as Clojure.
