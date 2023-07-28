<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false displayWide=false showAnotherWayIfPresent=true>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" class="${properties.kcHtmlClass!}">

<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="robots" content="noindex, nofollow">
    <meta
        name="viewport"
        content="width=device-width,initial-scale=1"
    />

    <#if properties.meta?has_content>
        <#list properties.meta?split(' ') as meta>
            <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
        </#list>
    </#if>
    <title>Cluster Login</title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico" />
    <#if properties.stylesCommon?has_content>
        <#list properties.stylesCommon?split(' ') as style>
            <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.scripts?has_content>
        <#list properties.scripts?split(' ') as script>
            <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
        </#list>
    </#if>
    <#if scripts??>
        <#list scripts as script>
            <script src="${script}" type="text/javascript"></script>
        </#list>
    </#if>
</head>

<body class="${properties.kcBodyClass!}">
    <div id="kc-modal">
        <div class="modal-wrapper">
            <header class="modal-header">
                <h1>
                    Willkommen auf der Login Seite des<br>
                    <span>Clusters für KI & Data Science</span><br>
                    der Fachhochschule Südwestfalen!
                </h1>
            </header>
            <div class="modal-body">
                <p>
                    Bitte wählen Sie eine der unten stehenden Anmeldeoption, um fortzufahren.
                    Um sich mit der <span>FH Kennung</span> einzuloggen, 
                    sollte der <span>SSO Login</span> verwendet werden.
                </p>
            </div>
            <footer class="modal-footer">
                <#if social.providers??>
                    <#list social.providers as p>
                        <#if p.alias == 'fh-saml'>
                            <button class="sso-login" onclick='window.location.href="${p.loginUrl}"'>
                                <p>SSO Login</p>
                                <span>mit der FH Kennung</span>
                            </button>
                        </#if>
                    </#list>
                </#if>
                <button class="cluster-login">Cluster Login</button>
            </footer>
        </div>
    </div>

    <div
        id="kc-header"
        class="login-pf-page-header"
    >
        <h1>
            Cluster Login
        </h1>
    </div>
    <div id="login-hint">
        <p>Login mit FH Anmeldedaten</p>
        <#if social.providers??>
            <#list social.providers as p>
                <#if p.alias == 'fh-saml'>
                    <a href="${p.loginUrl}">Hier klicken</a>
                </#if>
            </#list>
        </#if>
    </div>
    <div class="${properties.kcFormCardClass!} <#if displayWide>${properties.kcFormCardAccountClass!}</#if>">
      <header class="${properties.kcFormHeaderClass!}">
        <#if realm.internationalizationEnabled  && locale.supported?size gt 1>
            <div id="kc-locale">
                <div id="kc-locale-wrapper" class="${properties.kcLocaleWrapperClass!}">
                    <div class="kc-dropdown" id="kc-locale-dropdown">
                        <a href="#" id="kc-current-locale-link">${locale.current}</a>
                        <ul>
                            <#list locale.supported as l>
                                <li class="kc-dropdown-item"><a href="${l.url}">${l.label}</a></li>
                            </#list>
                        </ul>
                    </div>
                </div>
            </div>
        </#if>
        <#if !(auth?has_content && auth.showUsername() && !auth.showResetCredentials())>
            <#if displayRequiredFields>
                <div class="${properties.kcContentWrapperClass!}">
                    <div class="${properties.kcLabelWrapperClass!} subtitle">
                        <span class="subtitle"><span class="required">*</span> ${msg("requiredFields")}</span>
                    </div>
                    <div class="col-md-10">
                        <h1 id="kc-page-title"><#nested "header"></h1>
                    </div>
                </div>
            </#if>
        <#else>
            <#if displayRequiredFields>
                <div class="${properties.kcContentWrapperClass!}">
                    <div class="${properties.kcLabelWrapperClass!} subtitle">
                        <span class="subtitle"><span class="required">*</span> ${msg("requiredFields")}</span>
                    </div>
                    <div class="col-md-10">
                        <#nested "show-username">
                        <div class="${properties.kcFormGroupClass!}">
                            <div id="kc-username">
                                <label id="kc-attempted-username">${auth.attemptedUsername}</label>
                                <a id="reset-login" href="${url.loginRestartFlowUrl}">
                                    <div class="kc-login-tooltip">
                                        <i class="${properties.kcResetFlowIcon!}"></i>
                                        <span class="kc-tooltip-text">${msg("restartLoginTooltip")}</span>
                                    </div>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            <#else>
                <#nested "show-username">
                <div class="${properties.kcFormGroupClass!}">
                    <div id="kc-username">
                        <label id="kc-attempted-username">${auth.attemptedUsername}</label>
                        <a id="reset-login" href="${url.loginRestartFlowUrl}">
                            <div class="kc-login-tooltip">
                                <i class="${properties.kcResetFlowIcon!}"></i>
                                <span class="kc-tooltip-text">${msg("restartLoginTooltip")}</span>
                            </div>
                        </a>
                    </div>
                </div>
            </#if>
        </#if>
      </header>
      <div id="kc-content">
        <div id="kc-content-wrapper">

          <#-- App-initiated actions should not see warning messages about the need to complete the action -->
          <#-- during login.                                                                               -->
          <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
              <div class="alert alert-${message.type}">
                  <#if message.type = 'success'><span class="${properties.kcFeedbackSuccessIcon!}"></span></#if>
                  <#if message.type = 'warning'><span class="${properties.kcFeedbackWarningIcon!}"></span></#if>
                  <#if message.type = 'error'><span class="${properties.kcFeedbackErrorIcon!}"></span></#if>
                  <#if message.type = 'info'><span class="${properties.kcFeedbackInfoIcon!}"></span></#if>
                  <span class="kc-feedback-text">${kcSanitize(message.summary)?no_esc}</span>
              </div>
          </#if>

          <#nested "form">

          <#if auth?has_content && auth.showTryAnotherWayLink() && showAnotherWayIfPresent>
          <form id="kc-select-try-another-way-form" action="${url.loginAction}" method="post" <#if displayWide>class="${properties.kcContentWrapperClass!}"</#if>>
              <div <#if displayWide>class="${properties.kcFormSocialAccountContentClass!} ${properties.kcFormSocialAccountClass!}"</#if>>
                  <div class="${properties.kcFormGroupClass!}">
                    <input type="hidden" name="tryAnotherWay" value="on" />
                    <a href="#" id="try-another-way" onclick="document.forms['kc-select-try-another-way-form'].submit();return false;">${msg("doTryAnotherWay")}</a>
                  </div>
              </div>
          </form>
          </#if>

          <#if displayInfo>
              <div id="kc-info" class="${properties.kcSignUpClass!}">
                  <div id="kc-info-wrapper" class="${properties.kcInfoAreaWrapperClass!}">
                      <#nested "info">
                  </div>
              </div>
          </#if>
        </div>
      </div>
    </div>

    <div id="fhswf-footer">
        <div id="fhswf_helpdesk">
            <a
                href="https://www.fh-swf.de/de/studierende/rund_ums_studium/its/serivces_allgemein/index_6/index.php"
                target="_blank"
                >Passwort vergessen?</a
            >
            <a
                target="_blank"
                href="https://www.fh-swf.de/de/studierende/rund_ums_studium/its/it_servicedesk/index.php"
                >ServiceDesk</a
            ><a href="mailto:servicedesk@fh-swf.de">servicedesk@fh-swf.de</a>
            <a href="tel:+4923714356000">+49 2371 4356000</a>
        </div>
        <div id="fhswf_imprint">
            <a
                href="https://www.fh-swf.de/de/footer_3/impressum_1.php"
                target="_blank"
                >Impressum</a
            >
            |
            <a
                href="https://www.fh-swf.de/de/footer_3/datenschutz_5.php"
                target="_blank"
                >Datenschutz</a
            >
        </div>
    </div>
</body>
</html>
</#macro>
