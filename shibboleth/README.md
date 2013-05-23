## About

The shibboleth plugin provides single sign-on capabilities with the [Shibboleth federated identity management system](http://www.shibboleth.net).

## Deployment

To deploy the plugin, either embed it into the web-integrator WAR during it's build process, or drop the generated JAR onto into the ``lib`` directory on the JBoss server.  This plugin relies on the [user-registration](../user-registration/README.md) plugin.  If you are deploying this to the application server classpath, you must deploy the user-registration jar as well.

## Configuration

The shibboleth plugin will look at the request headers to find the values used to authenticate the user within I2B2.  By default, the names of the headers used to find those values are as follows:

<table>
    <thead>
        <tr>
            <th>Header Name</th>
            <th>I2B2 Field</th>
            <th>Purpose</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td nowrap>shib-eduperson-principal-name</td>
            <td nowrap>User ID</td>
            <td>Header value used to authenticate against existing user IDs in the I2B2 PM module.</td>
        </tr>
        <tr>
            <td nowrap>shib-session-id</td>
            <td nowrap>Password</td>
            <td>If the user id is found, this value will be used as the current sessions password by updating the database with the found value.  Since I2B2 stores credentials on the client, which isn't necessarily secure, this will ensure that cached content on the client will not reveal a valid password.</td>
        </tr>
        <tr>
            <td nowrap>shib-inetorgpersonmail</td>
            <td nowrap>Email</td>
            <td>Used to prefill the user registration email field when requestor is new.</td>
        </tr>
        <tr>
            <td nowrap>shib-person-commonname</td>
            <td nowrap>Full Name</td>
            <td>Used to prefill the user registration name field when requestor is new.</td>
        </tr>
        <tr>
            <td nowrap>shib-inetorgperson-givenname</td>
            <td nowrap>First Name</td>
            <td>When a single value for a full name is not available, this can be used to get the first name of the user to prefill the user registration name field when requestor is new.</td>
        </tr>
        <tr>
            <td nowrap>shib-person-surname</td>
            <td nowrap>Last Name</td>
            <td>When a single value for a full name is not available, this can be used to get the last name of the user to prefill the user registration name field when requestor is new.</td>
        </tr>
    </tbody>
</table>

Note that the web framework used automatically makes all header names lowercase.

To change the names of the headers to use, such as to use the LDAP values that are pulled along with Shibboleth, the plugin utilizes the [platform configuration capabilities](../doc/CONFIGURE.md).  The following properties are used to override the names of the headers to use:

<table>
    <thead>
        <tr>
            <th>Property Key</th>
            <th>I2B2 Field</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td nowrap>plugin.shibboleth.prefix</td>
            <td nowrap>Used to prefix all of the header names.  Provides an easy header name modification when Shibboleth is configured to prefix all names with a specific value.</td>
        </tr>
        <tr>
            <td nowrap>plugin.shibboleth.userid.header</td>
            <td nowrap>User ID</td>
        </tr>
        <tr>
            <td nowrap>plugin.shibboleth.password.header</td>
            <td nowrap>Password</td>
        </tr>
        <tr>
            <td nowrap>plugin.shibboleth.email.header</td>
            <td nowrap>Email</td>
        </tr>
        <tr>
            <td nowrap>plugin.shibboleth.name.header</td>
            <td nowrap>Full Name</td>
        </tr>
        <tr>
            <td nowrap>plugin.shibboleth.firstname.header</td>
            <td nowrap>First Name</td>
        </tr>
        <tr>
            <td nowrap>plugin.shibboleth.lastname.header</td>
            <td nowrap>Last Name</td>
        </tr>
    </tbody>
</table>

An example of the section of the properties files if Shibboleth was configured to prepend the value ``Faber_`` to the shibboleth header would be:

    # Shibboleth Plugin Properties
    plugin.shibboleth.prefix=faber_


An example of the section of the properties files if Shibboleth was to utilize passed through LDAP properties:

    # Shibboleth Plugin Properties
    plugin.shibboleth.userid.header=eppn
    #plugin.shibboleth.password.header=Shib-Session-ID
    plugin.shibboleth.email.header=mail
    plugin.shibboleth.name.header=cn
    plugin.shibboleth.firstname.header=givenName
    plugin.shibboleth.lastname.header=sn


## License

Copyright © 2013 Health Sciences of South Carolina

Distributed under the Eclipse Public License, the same as Clojure.
