var router = new Navigo(null, true, '#');
var frontendServiceUrl = $('#frontendServiceUrl').text();
var frontendServiceBackEndPath = "/backend";
var ldapEnabled = true;
var table;
var subscriptionNameRegex = $('#subscriptionNameRegex').text();
var notificationMetaRegex = $('#notificationMetaRegex').text();

// Minimum leangth is 4 due to ellipsis being 3 long
var usernameMaxDisplayLength = 15;

var backendEndpoints = {
    AUTH: "/auth",
    LOGIN: "/auth/login",
    CHECK_STATUS: "/auth/checkStatus",
    SUBSCRIPTIONS: "/subscriptions",
    DOWNLOAD: "/download/",
    DOWNLOAD_SUBSCRIPTIONS_TEMPLATE: "/download/subscriptionsTemplate",
    INFORMATION: "/information",
    TEST_RULES_PAGE_ENABLED: "/rules/rule-check/testRulePageEnabled",
    CHECK_AGGREGATION: "/rules/rule-check/aggregation"
};

// Status handling variables
var PAGES_FOR_STATUS_INDICATION = "subscriptions test-rules ei-info login";
var statusType = {
    SUCCESS: "alert-success",
    INFO: "alert-info",
    WARNING: "alert-warning",
    DANGER: "alert-danger"
};
var statusText = {
    BACKEND_DOWN: "<strong>Back-end is down!</strong> Wait for it go up or switch to another back-end before continuing!",
    UNKNOWN_BACK_END_STATUS: "<strong>Back-end status is unknown!</strong> Wait for it to update or switch to another back-end before continuing!",
    TEST_RULES_DISABLED: "<strong>Test Rule service is disabled!</strong> To enable it set the backend property [testaggregated.enabled] as [true]"
};

var backEndStatus = true;
var backEndStatusUpdated = false;
var previousBackEndStatus;
var backEndStatusTimerInterval;
// End Status variables

// Start ## getters and setters

function isLdapEnabled(){
    return Boolean(ldapEnabled);
}

function setLdapEnabled(value){
    ldapEnabled = Boolean(value);
}

function getCurrentUser() {
    return sessionStorage.getItem("currentUser");
}

function setCurrentUser(user) {
    sessionStorage.removeItem("currentUser");
    sessionStorage.setItem("currentUser", user);
}

function getFrontEndServiceUrl() {
    return frontendServiceUrl;
}

function getFrontendServiceBackEndPath() {
    return frontendServiceBackEndPath;
}

// End   ## getters and setters

// Start ## getters and setters status handling

function isBackEndStatusOk() {
    return Boolean(backEndStatus);
}

function isBackEndStatusUpdated() {
    return Boolean(backEndStatusUpdated);
}

function setBackEndStatusOk(value) {
    backEndStatus = Boolean(value);
    backEndStatusUpdated = true;
}

function isBackEndStatusChanged() {
    if (previousBackEndStatus === undefined) {
        previousBackEndStatus = backEndStatus;
        return false;
    }
    var statusChanged = previousBackEndStatus !== backEndStatus;
    previousBackEndStatus = backEndStatus;
    return Boolean(statusChanged);
}

function setBackEndStatusTimerInterval() {
    if (backEndStatusTimerInterval === undefined) {
        backEndStatusTimerInterval = window.setInterval(function () { updateBackendStatus(); }, 15000);
    }
}

function getWhiteListedPages() {
    return String(PAGES_FOR_STATUS_INDICATION);
}

function getSubscriptionNameRegex() {
    return subscriptionNameRegex;
}

function getNotificationMetaRegex() {
    return notificationMetaRegex;
}

function getUsernameMaxDisplayLength() {
    if (usernameMaxDisplayLength < 4) {
        // Minimum leangth is 4 due to ellipsis being 3 long
        return 4;
    }
    return usernameMaxDisplayLength;
}

// End   ## getters and setters status handling