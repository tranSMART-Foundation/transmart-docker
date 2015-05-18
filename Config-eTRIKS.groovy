/*
 * Edited and managed by the WP1 for eTRIKS tranSMART deployment
 * NOTE
 * ----
 * This configuration assumes that the development environment will be used with
 * run-app and the production environment will be used with the application
 * packaged as a WAR and deployed to tomcat. Running grails run-war or otherwise
 * running a WAR with the development profile set up or activating the
 * production environment when running grails run-app are scenarios that have
 * NOT been tested.
 */

// eTRIKS tranSMART conf file 04-2015

// Core variables

def catalinaBase      = System.getProperty('catalina.base') ?: '.'
def explodedWarDir    = catalinaBase + '/webapps/transmart'
def solrPort          = 8983
def searchIndex       = catalinaBase + '/searchIndex'
def jobsDirectory     = "/var/tmp/jobs"
def oauthEnabled      = false
def samlEnabled       = false
def gwavaEnabled      = false
def LDAPEnabled       = false
def transmartURL      = "http://localhost:${System.getProperty('server.port', '8080')}/transmart/"


// Per project variable configuration

//transmartURL      = 'http://YOUR.DNS/transmart/'
//oauthEnabled      = true
//samlEnabled       = true
//gwavaEnabled      = true

// The project name
com.recomdata.projectName = "eTRIKS tranSMART 1.2.x Docker AIO"

// name and URL of the supporter entity shown on the welcome page
com.recomdata.providerName = "eTRIKS.org"
com.recomdata.providerURL = "http://www.etriks.org"

// Application logo
com.recomdata.largeLogo = "transmartlogo.jpg"

// Application search logo
com.recomdata.searchtool.smallLogo="transmartlogosmall.jpg"

// Contact email
// com.recomdata.contactUs = "mailto:none@none.org"

// Application title
com.recomdata.appTitle = "eTRIKS - tranSMART v" + org.transmart.originalConfigBinding.appVersion

// Location of the help pages
com.recomdata.adminHelpURL = "$transmartURL/help/adminHelp/default.htm"

// Bugreport URL
// com.recomdata.bugreportURL = "https://example.ex.org"

// Email address of the administrator to contact
com.recomdata.administrator="none@none.org"

// Metadata view
com.recomdata.view.studyview = 'studydetail'
com.recomdata.plugins.resultSize = 5000

// Hide internal tabs including doc and jubilant tabs
// com.recomdata.searchtool.hideInternalTabs = 'false'

// Hide across trial panel
// com.recomdata.datasetExplorer.hideAcrossTrialsPanel = 'false'

// Disable sample explorer
// com.recomdata.hideSampleExplorer = 'false'

// Configuration for Galaxy and Metacore
// com.thomsonreuters.transmart.metacoreAnalyticsEnable = true
// com.galaxy.blend4j.galaxyEnabled = false
// com.galaxy.blend4j.galaxyURL = "http://localhost:8081"

// Login and password policy

com.recomdata.guestAutoLogin = true
environments { development { com.recomdata.guestAutoLogin = true } }
com.recomdata.guestUserName = 'guest'

bruteForceLoginLock {
    allowedNumberOfAttempts = 5
    lockTimeInMinutes = 10
}

// Password strength criteria, please change description accordingly
//com.recomdata.passwordstrength.pattern = ~/^.*(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[\d])(?=.*[\W]).*$/

// Password strength description, please change according to pattern
//com.recomdata.passwordstrength.description =
//    'It should contain a minimum of 8 characters including at least ' +
//    '1 upper and 1 lower case letter, 1 digit and 1 special character.'

// HTTP session variables

if (com.recomdata.guestAutoLogin) {
  com.recomdata.sessionTimeout = Integer.MAX_VALUE / 1000 as int /* ~24 days */
  com.recomdata.heartbeatLaps = 900
} else {
  com.recomdata.sessionTimeout = 600
  com.recomdata.heartbeatLaps = 30
}

environments { development {
    com.recomdata.sessionTimeout = Integer.MAX_VALUE / 1000 as int /* ~24 days */
    com.recomdata.heartbeatLaps = 900
} }


// User Interface setup

ui {
    tabs {
        //Search was not part of 1.2. It's not working properly. You need to set `show` to `true` to see it on UI
        search.show = false
        browse.hide = false
        sampleExplorer.hide = false
        geneSignature.hide = false
        gwas.hide = true
        uploadData.hide = true
    }
}


// Variables checks

environments { production {
    if (transmartURL.startsWith('http://localhost:')) {
        println "[WARN] transmartURL not overriden. Some settings (e.g. help page) may be wrong"
    }
    if (jobsDirectory == '/var/tmp/jobs/PROJECT') {
        println "[WARN] jobsDirectory isn't setup according to the project. R won't work correctly"
    }
} }

// Logs configuration

log4j = {
    environments {
        development {
            root {
                info 'stdout'
            }

            // for a less verbose startup & shutdown
            warn  'org.codehaus.groovy.grails.commons.spring'
            warn  'org.codehaus.groovy.grails.orm.hibernate.cfg'
            warn  'org.codehaus.groovy.grails.domain.GrailsDomainClassCleaner'

            debug 'org.transmartproject'
            debug 'com.recomdata'
            debug 'grails.app.services.com.recomdata'
            debug 'grails.app.services.org.transmartproject'
            debug 'grails.app.controllers.com.recomdata'
            debug 'grails.app.controllers.org.transmartproject'
            debug 'grails.app.domain.com.recomdata'
            debug 'grails.app.domain.org.transmartproject'
            // debug 'org.springframework.security'
            // (very verbose) debug  'org.grails.plugin.resource'
        }

// Production

        production {
            def logDirectory = "${catalinaBase}/logs".toString()
            appenders {
                rollingFile(name: 'transmart',
                            file: "${logDirectory}/transmart.log",
                            layout: pattern(conversionPattern: '%d{dd-MM-yyyy HH:mm:ss,SSS} %5p %c{1} - %m%n'),
                            maxFileSize: '100MB')
            }
            root {
                warn 'transmart'
            }
        }
    }
}


// Solr configuration

environments {
    development {
        com.rwg.solr.scheme = 'http'
        com.rwg.solr.host   = 'localhost:8983'
        com.rwg.solr.path   = '/solr/rwg/select/'
    }

// Production

    production {
        com.rwg.solr.scheme = 'http'
        com.rwg.solr.host   = 'localhost:' + solrPort
        com.rwg.solr.path   = '/solr/rwg/select/'
    }
}

com.rwg.solr.browse.path    = '/solr/browse/select/'
com.rwg.solr.update.path    = '/solr/browse/dataimport/'
com.recomdata.solr.baseURL  = "${com.rwg.solr.scheme}://${com.rwg.solr.host}" +
                             "${new File(com.rwg.solr.browse.path).parent}"

def fileStoreDirectory      = new File(System.getenv('HOME'), '.grails/transmart-filestore')
def fileImportDirectory     = new File(System.getProperty("java.io.tmpdir"), 'transmart-fileimport')
com.recomdata.FmFolderService.filestoreDirectory = fileStoreDirectory.absolutePath
com.recomdata.FmFolderService.importDirectory = fileImportDirectory.absolutePath

[fileStoreDirectory, fileImportDirectory].each {
    if (!it.exists()) {
        it.mkdir()
    }
}



// Search tool configuration

// Lucene index location for documentation search
com.recomdata.searchengine.index = searchIndex


// Sample Explorer configuration
// This is an object to dictate the names and 'pretty names' of the SOLR fields.
// Optionally you can set the width of each of the columns when rendered.

sampleExplorer {
    fieldMapping = [
        columns:[
            [header:'Sample ID',dataIndex:'id', mainTerm: false, showInGrid: false],
            [header:'BioBank', dataIndex:'BioBank', mainTerm: true, showInGrid: true, width:10],
            [header:'Source Organism', dataIndex:'Source_Organism', mainTerm: true, showInGrid: true, width:10]
            // Continue as you have fields
        ]
    ]
    resultsGridHeight = 100
    resultsGridWidth = 100
    idfield = 'id'
}

edu.harvard.transmart.sampleBreakdownMap = [
    "id":"Aliquots in Cohort"
]


// Solr configuration for the Sample Explorer

com { recomdata { solr {
    maxNewsStories = 10
    maxRows = 10000
}}}


// Dataset Explorer configuration

com { recomdata { datasetExplorer {
    // set to 'true' (quotes included) to enable gene pattern integration

    genePatternEnabled = 'false'

    // The tomcat URL that gene pattern is deployed within -usually it's proxyed through apache

    genePatternURL = 'http://23.23.185.167'

    // Gene Pattern real URL with port number

    genePatternRealURLBehindProxy = 'http://23.23.185.167:8080'

    // default Gene pattern user to start up - each tranSMART user will need a separate user account to be created in Gene Pattern

    genePatternUser = 'biomart'

    // Absolute path to PLINK executables

    plinkExcutable = '/usr/local/bin/plink'
} } }


// RModules & Data Export Configuration

environments {
    // This is to target a remove Rserv. Bear in mind the need for shared network storage

    RModules.host = "127.0.0.1"
    RModules.port = 6340

    // This is not used in recent versions; the URL is always /analysisFiles/

    RModules.imageURL = "/tempImages/"

    // Whether or not to enable FTP

    ftpFlag = 'false'

    production {
        // The working direcotry for R scripts, where the jobs get created and
        // output files get generated

        RModules.tempFolderDirectory = jobsDirectory

        // Whether to copy the images from the jobs directory to another
        // directory from which they can be served. Should be false for
        // performance reasons. Files will be served from the
        // tempFolderDirectory instead, which should be exposed as
        // <context path>/analysisFiles (formerly: <context path>/tempImages)

        RModules.transferImageFile = false

        // Copy inside the exploded WAR. In actual production, we don't want this
        // The web server should be able to serve static files from this
        // directory via the logical name specified in the imageUrl config entry
        // Not needed because transferImageFile is false

        //Rmodules.temporaryImageFolder = explodedWarDir + '/images/tempImages/'
    }

    development {
        RModules.tempFolderDirectory = "/tmp"

        // we have stuff in _Events.groovy to make available the contens in
        // the tempFolderDirectory
        RModules.transferImageFile = false
    }

    // Used to access R jobs parent directory outside RModules (e.g. data export)

    com.recomdata.plugins.tempFolderDirectory = RModules.tempFolderDirectory

}

// Data export FTP settings

// com.recomdata.transmart.data.export.ftp.server = ''
// com.recomdata.transmart.data.export.ftp.serverport = ''
// com.recomdata.transmart.data.export.ftp.username = ''
// com.recomdata.transmart.data.export.ftp.password = ''
// com.recomdata.transmart.data.export.ftp.remote.path = ''

// Misc Configuration

// This can be used to debug JavaScript callbacks in the dataset explorer in
// Chrome. Unfortunately, it also sometimes causes chrome to segfault
com.recomdata.debug.jsCallbacks = 'false'
environments {
    production {
        com.recomdata.debug.jsCallbacks = 'false'
    }
}

// To be defined

grails.resources.adhoc.excludes = [ '/images' + RModules.imageURL + '**' ]

// Adding properties to the Build information panel

buildInfo { properties {
   include = [ 'app.grails.version', 'build.groovy' ]
   exclude = [ 'env.proc.cores' ]
} }


// Spring Security configuration

grails { plugin { springsecurity {
    // customized user GORM class

    userLookup.userDomainClassName = 'org.transmart.searchapp.AuthUser'

    // customized password field

    userLookup.passwordPropertyName = 'passwd'

    // customized user /role join GORM class

    userLookup.authorityJoinClassName = 'org.transmart.searchapp.AuthUser'

    // customized role GORM class

    authority.className = 'org.transmart.searchapp.Role'

    // request map GORM class name - request map is stored in the db

    requestMap.className = 'org.transmart.searchapp.Requestmap'

    // requestmap in db

    securityConfigType = grails.plugin.springsecurity.SecurityConfigType.Requestmap

    // url to redirect after login in

    successHandler.defaultTargetUrl = '/userLanding'

    // logout url

    logout.afterLogoutUrl = '/login/forceAuth'

    // configurable requestmap functionality in transmart is deprecated

    def useRequestMap = false

    if (useRequestMap) {
        // requestmap in db

        securityConfigType = 'Requestmap'

        // request map GORM class name - request map is stored in the db

        requestMap.className = 'org.transmart.searchapp.Requestmap'
    } else {
        securityConfigType = 'InterceptUrlMap'
        def oauthEndpoints = [
            '/oauth/authorize.dispatch'   : ['IS_AUTHENTICATED_REMEMBERED'],
            '/oauth/token.dispatch'       : ['IS_AUTHENTICATED_REMEMBERED'],
        ]
        interceptUrlMap = [
            '/login/**'                   : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/css/**'                     : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/js/**'                      : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/grails-errorhandler'        : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/images/analysisFiles/**'    : ['IS_AUTHENTICATED_REMEMBERED'],
            '/images/**'                  : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/static/**'                  : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/search/loadAJAX**'          : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/analysis/getGenePatternFile': ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/analysis/getTestFile'       : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/requestmap/**'              : ['ROLE_ADMIN'],
            '/role/**'                    : ['ROLE_ADMIN'],
            '/authUser/**'                : ['ROLE_ADMIN'],
            '/secureObject/**'            : ['ROLE_ADMIN'],
            '/accessLog/**'               : ['ROLE_ADMIN'],
            '/authUserSecureAccess/**'    : ['ROLE_ADMIN'],
            '/secureObjectPath/**'        : ['ROLE_ADMIN'],
            '/userGroup/**'               : ['ROLE_ADMIN'],
            '/secureObjectAccess/**'      : ['ROLE_ADMIN'],
            *                             : (oauthEnabled ?  oauthEndpoints : [:]),
            '/**'                         : ['IS_AUTHENTICATED_REMEMBERED'], // must be last
        ]
        rejectIfNoRule = true
    }


    // Password hash algorithm

    password.algorithm = 'bcrypt'
    // Number of bcrypt rounds
    //password.bcrypt.logrounds = 14
    // password.algorithm = 'SHA-1'
    // password.hash.iterations = 1

    // Spring security – error messages
    errors.login.expired         = 'Your account has expired'
    errors.login.passwordExpired = 'Your password has expired'
    errors.login.disabled        = 'Your login has been disabled'
    errors.login.locked          = 'Your account has been locked'
    errors.login.fail            = 'Login has failed; check the provided credentials'

    // Authentication providers (LDAP and Guest mainly)

    providerNames = ['daoAuthenticationProvider',
        'anonymousAuthenticationProvider',
        'rememberMeAuthenticationProvider',
    ]

    // OAuth check

    if (oauthEnabled) {
        providerNames << 'clientCredentialsAuthenticationProvider'

        oauthProvider {
            clients = [
                    [clientId: 'api-client', clientSecret: 'api-client']
            ]
        } }

    // LDAP check

    if (LDAPEnabled) { providerNames << 'ldapAuthProvider' }

} } }


// LDAP configuration

if (LDAPEnabled) {

org.transmart.security.spnegoEnabled = true

grails { plugin { springsecurity { ldap {

	context {

		// Address of the LDAP server
		server = 'ldap://YOUR.LDAP'
		// Distinguished Name (DN) to authenticate with
		managerDn = 'cn=admin,dc=etriks,dc=eu'
		// Password to authenticate with
		managerPassword = 'PASSWORD'
		// Whether or not integrate internal roles. Blurry notion, documentation missing
		allowInternaRoles = 'false'
	}

	search {
		// Context name to search in, relative to the base of the configured ContextSource, e.g. 'dc=example,dc=com', 'ou=users,dc=example,dc=com'
		base = 'ou=none,dc=none,dc=org'
		// The filter expression used in the user search
		filter = '(uid={0})'
	}

	// Names of attribute ids to return; use null to return all and an empty list to return none
	authenticator.attributesToReturn = ['uid', 'mail', 'cn']

	authorities {
		// The base DN from which the search for group membership should be performed
		groupSearchBase = 'ou=Transmart,ou=Applications,dc=etriks,dc=eu'
		// The pattern to be used for the user search. {0} is the user's DN
		groupSearchFilter = 'memberUid={1}'
		// Whether PartialResultExceptions should be ignored in searches, typically used with Active Directory since AD servers often have a problem with referrals
		ignorePartialResultException = true
		// Whether to infer roles based on group membership
		retrieveGroupRoles = true
		// Whether to retrieve additional roles from the database using the User/Role many-to-many
		retrieveDatabaseRoles = false
	}

} } } } }



// SAML Configuration

if (samlEnabled) {
    // don't do assignment to grails.plugin.springsecurity.providerNames
    // see GRAILS-11730
    grails { plugin { springsecurity {
        providerNames << 'samlAuthenticationProvider'
    } } }
    // again, because of GRAILS-11730
    def ourPluginConfig
    grails {
        ourPluginConfig = plugin
    }

    org { transmart { security {
        samlEnabled = true
        ssoEnabled  = "true"

        // URL to redirect to after successful authentication
        successRedirectHandler.defaultTargetUrl = ourPluginConfig.springsecurity.successHandler.defaultTargetUrl
        // URL to redirect to after successful logout
        successLogoutHandler.defaultTargetUrl = ourPluginConfig.springsecurity.logout.afterLogoutUrl

        saml {
            // Service provider details (we)
            sp {
                // ID of the Service Provider
                id = "gustavo-transmart"

                // URL of the service provider. This should be autodected, but it isn't
                url = "http://localhost:8080/transmart"

                // Alias of the Service Provider
                alias = "transmart"

                // Alias of the Service Provider's signing key, see keystore details
                signingKeyAlias = "saml-signing"
                // Alias of the Service Provider's encryption key
                encryptionKeyAlias = "saml-encryption"
            }

            // Metadata file of the provider. We insist on keeping instead of just
            // retrieving it from the provider on startup to prevent transmart from
            // being unable to start due to provider being down. A copy will still be
            // periodically fetched from the provider
            idp.metadataFile = '/home/glopes/idp-local-metadata.xml'

            // Keystore details
            keystore {
                // Generate with:
                //  keytool -genkey -keyalg RSA -alias saml-{signing,encryption} \
                //    -keystore transmart.jks -storepass changeit \
                //    -validity 3602 -keysize 2048
                // Location of the keystore. You can use other schemes, like classpath:resource/samlKeystore.jks
                file = 'file:///home/glopes/transmart.jks'

                // keystore's storepass
                password="changeit"

                // keystore's default key
                defaultKey="saml-signing"

                // Alias of the encryption key in the keystore
                encryptionKey.alias="saml-encryption"
                // Password of that the key with above alis in the keystore
                encryptionKey.password="changeit"

                // Alias of the signing key in the keystore
                signingKey.alias="saml-signing"
                // Password of that the key with above alis in the keystore
                signingKey.password="changeit"
            }

            // Creation of new users
            createInexistentUsers = "true"
            attribute.username    = "urn:custodix:ciam:1.0:principal:username"
            attribute.firstName   = "urn:oid:2.5.4.42"
            attribute.lastName    = "urn:oid:2.5.4.4"
            attribute.email       = ""
            attribute.federatedId = "personPrincipalName"

            // Suffix of the login filter, saml authentication is initiated when user browses to this url
            entryPoint.filterProcesses = "/saml/login"
            // SAML Binding to be used for above entry point url.
            entryPoint.binding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
            // This property must be set otherwise the default binding is used which, in this configuration, is HTTP-ARTIFACT
            entryPoint.defaultAssertionConsumerIndex = "1"

            // Suffix of the Service Provider's metadata, this url needs to be configured on IDP
            metadata.filterSuffix = "/saml/metadata"
            // Id of the spring security's authentication manager
            authenticationManager = "authenticationManager"
            // Whether sessions should be invalidated after logout
            logout.invalidateHttpSession = "true"
            // Id of the spring security user service that should be called to fetch users.
            saml.userService = "org.transmart.FederatedUserDetailsService"
        }
    } } }
} else { // if (!samlEnabled)
    org { transmart { security {
        samlEnabled = false
    } } }
}

// GWAVA configuration

if (gwavaEnabled) {
    com.recomdata.rwg.webstart.codebase      = "$transmartURL/gwava"
    com.recomdata.rwg.webstart.jar           = './ManhattanViz2.1g.jar'
    com.recomdata.rwg.webstart.mainClass     = 'com.pfizer.mrbt.genomics.Driver'
    com.recomdata.rwg.webstart.gwavaInstance = 'transmartstg'
    com.recomdata.rwg.webstart.transmart.url = "$transmartURL/transmart"
}


// Quartz jobs configuration

// start delay for the sweep job
com.recomdata.export.jobs.sweep.startDelay = 60000 // d*h*m*s*1000

// repeat interval for the sweep job
com.recomdata.export.jobs.sweep.repeatInterval = 86400000 // d*h*m*s*1000

// specify the age of files to be deleted (in days)
com.recomdata.export.jobs.sweep.fileAge = 3




// EOF You MUST leave this at the end

org.transmart.configFine = true
