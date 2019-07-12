// Global vars
var i = 0;
var isReplacing = true;

jQuery(document).ready(function () {
    // Model for knockout(KO) binding
    function AppViewModel() {
        var self = this;
        self.rulesBindingList = ko.observableArray([]);
        self.eventsBindingList = ko.observableArray([]);
        self.parsedToString = function (item) {
            return JSON.stringify(item, null, 2);
        };
        // Removing the rule
        self.removeRule = function (data, event) {
            var context = ko.contextFor(event.target);
            self.rulesBindingList.splice(context.$index(), 1);
            if (self.rulesBindingList().length == 0) {
                logMessage("All rule types were deleted, but we need at least one, so we add a default rule.");
                self.addRule(ruleTemplate);
            }
        };

        // Removing the events
        self.removeEvent = function (data, event) {
            var context = ko.contextFor(event.target);
            self.eventsBindingList.splice(context.$index(), 1);
            if (self.eventsBindingList().length == 0) {
                logMessage("All events were deleted, but we need at least one, so we add an empty one.");
                self.addEvent({});
            }
        };

        self.validateJSON = function (observableArray) {
            var array = [];
            ko.utils.arrayForEach(observableArray, function (element) {
                try {
                    array.push(JSON.parse(element.data()));
                } catch (e) {
                    logMessage("Invalid json rule format :\n" + element.data());
                    return false;
                }
            });
            return array;
        };

        //This submit function for finding the aggregated object from the rules and events, This function internally call the ajax call
        self.submit = function () {
            var rules = self.validateJSON(self.rulesBindingList());
            var events = self.validateJSON(self.eventsBindingList());
            var rulesAndEventsJson = { 'listRulesJson': rules, 'listEventsJson': events };

            var callback = {
                success: function (responseData, textStatus) {
                    if (responseData.length > 0) {
                        $('#aggregatedObjectContent').text(JSON.stringify(responseData, null, 2));
                        $('#aggregatedObjectModal').modal('show');
                    }
                }
            };

            var ajaxHttpSender = new AjaxHttpSender();
            ajaxHttpSender.sendAjax(backendEndpoints.CHECK_AGGREGATION, "POST", JSON.stringify(rulesAndEventsJson), callback);
        };

        // This function for adding rule
        self.addRule = function (data) {
            self.rulesBindingList.push({ 'data': ko.observable(self.parsedToString(data)) });
        };
        // This function for adding rule
        self.addEvent = function (data) {
            self.eventsBindingList.push({ 'data': ko.observable(self.parsedToString(data)) });
        };

        // This function is used to remove all rules
        self.clearAllRules = function () {
            self.rulesBindingList([]);
            self.addRule(ruleTemplate);
            $('.delete-warning-modal').modal("hide");
        }

        // This function is used to remove all events
        self.clearAllEvents = function () {
            self.eventsBindingList([]);
            self.addEvent({});
            $('.delete-warning-modal').modal("hide");
        }

        // This function is used to remove all rules
        self.clearType = ko.observable("rules");
        self.confirmClearAll = function (clearType) {
            self.clearType(clearType);
            $('.delete-warning-modal').modal('show');
        }

        return self;
    }

    //Create information modal
    $(".rules_info").click(function (event) {
        event.stopPropagation();
        event.preventDefault();
        $('#infoContent').text(test_rule_info);
        $('#infoModal').modal('show');
    });

    var vm = new AppViewModel();
    ko.applyBindings(vm, $("#submitButton")[0]);
    ko.applyBindings(vm, $("#testRulesDOMObject")[0]);
    ko.applyBindings(vm, $("#testEventsDOMObject")[0]);
    ko.applyBindings(vm, $(".delete-warning-modal")[0]);
    vm.addRule(ruleTemplate);
    vm.addEvent({});

    function validateJSONAndUpload(subscriptionFile, isRules) {
        var reader = new FileReader();
        reader.onload = function () {
            var fileContent = reader.result;
            var jsonLintResult = "";
            try {
                jsonLintResult = jsonlint.parse(fileContent);
            } catch (e) {
                logMessage("JSON events Format Check Failed:\n" + e.name + "\n" + e.message);
                return false;
            }
            $.jGrowl('JSON events Format Check Succeeded', {
                sticky: false,
                theme: 'Notify'
            });

            var list = JSON.parse(fileContent);
            if (isRules) {
                if (isReplacing) {
                    vm.rulesBindingList([]);
                }
                list.forEach(function (element) {
                    vm.addRule(element);
                });
            } else {
                if (isReplacing) {
                    vm.eventsBindingList([]);
                }
                list.forEach(function (element) {
                    vm.addEvent(element);
                });
            }
        };

        if (subscriptionFile != null) {
            reader.readAsText(subscriptionFile);
        }
    }

    //Set onchange event on the input element "uploadRulesFile" and "uploadEventsFile"
    var pomRules = document.getElementById('uploadRulesFile');
    pomRules.onchange = function uploadFinished() {
        var subscriptionFile = pomRules.files[0];
        validateJSONAndUpload(subscriptionFile, true);
        $(this).val("");
    };

    var pomEvents = document.getElementById('uploadEventsFile');
    pomEvents.onchange = function uploadFinished() {
        var subscriptionFile = pomEvents.files[0];
        validateJSONAndUpload(subscriptionFile, false);
        $(this).val("");
    };

    //Upload events list json data
    $(".upload_rules").click(function (event) {
        event.stopPropagation();
        event.preventDefault();
        var isRules = true;
        replaceAppendModal(isRules);

    });

    //Upload list of events json data
    $(".upload_events").click(function (event) {
        event.stopPropagation();
        event.preventDefault();
        var isRules = false;
        replaceAppendModal(isRules);
    });

    function replaceAppendModal(isRules) {
        $('#AppendReplaceModal').modal('show');

        document.getElementById('replaceButton').onclick = function () {
            $('#AppendReplaceModal').modal('hide');
            isReplacing = true;
            if (isRules) {
                createRulesUploadWindow();
            } else {
                createUploadWindow();
            }
        };

        document.getElementById('appendButton').onclick = function () {
            $('#AppendReplaceModal').modal('hide');
            isReplacing = false;
            if (isRules) {
                createRulesUploadWindow();
            } else {
                createUploadWindow();
            }
        };
    }

    function createRulesUploadWindow() {
        if (document.createEvent) {
            var event = document.createEvent('MouseEvents');
            event.initEvent('click', true, true);
            pomRules.dispatchEvent(event);
        } else {
            pomRules.click();
        }
    }

    function createUploadWindow() {
        if (document.createEvent) {
            var event = document.createEvent('MouseEvents');
            event.initEvent('click', true, true);
            pomEvents.dispatchEvent(event);
        } else {
            pomEvents.click();
        }
    }

    // Download the modified rule
    $(".download_rules").click(function () {
        var formRules = [];
        $('.formRules').each(function () {
            try {
                formRules.push(JSON.parse($(this).val()));
            } catch (e) {
                logMessage("Invalid json format :\n" + $(this).val());
                return false;
            }
        });
        if (formRules.length !== 0) {
            var jsonData = JSON.stringify(formRules, null, 2);
            downloadFile(jsonData, "application/json;charset=utf-8", "rules.json");
        } else {
            logMessage("Data not available for download!");
        }
    });

    // Download the modified events
    $(".download_events").click(function () {
        var formEvents = [];
        $('.formEvents').each(function () {
            try {
                formEvents.push(JSON.parse($(this).val()));
            } catch (e) {
                logMessage("Invalid json format :\n" + $(this).val());
                return false;
            }
        });
        if (formEvents.length !== 0) {
            var jsonData = JSON.stringify(formEvents, null, 2);
            downloadFile(jsonData, "application/json;charset=utf-8", "events.json");
        } else {
            logMessage("Data not available for download!");
        }
    });

    function getTemplate(name) {
        var callback = {
            success: function (responseData, textStatus) {
                var jsonString = JSON.stringify(responseData, null, 2);
                downloadFile(jsonString, "application/json;charset=utf-8", name + ".json");
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                logMessage("Failed to download template, Error: Could not contact the backend server.");
            }
        };

        var ajaxHttpSender = new AjaxHttpSender();
        var contextPath = backendEndpoints.DOWNLOAD + name;
        ajaxHttpSender.sendAjax(contextPath, "GET", "", callback);
    }

    // Download the rules template
    $(".download_rules_template").click(function (event) {
        event.stopPropagation();
        event.preventDefault();
        getTemplate("rulesTemplate");
    });

    // Download the events template
    $(".download_events_template").click(function (event) {
        event.stopPropagation();
        event.preventDefault();
        getTemplate("eventsTemplate");
    });

    // Start to check is backend Test Rule service status
    function checkTestRulePageEnabled() {
        var callback = {
            error: function () {
                addStatusIndicator(statusType.DANGER, statusText.BACKEND_DOWN);
                elementsDisabled(true);
            },
            success: function (responseData) {
                var isEnabled = responseData.status;
                if (isEnabled != true) {
                    addStatusIndicator(statusType.WARNING, statusText.TEST_RULES_DISABLED);
                } else {
                    removeStatusIndicator();
                }
                elementsDisabled(!isEnabled);
            }
        };
        var ajaxHttpSender = new AjaxHttpSender();
        ajaxHttpSender.sendAjax(backendEndpoints.TEST_RULES_PAGE_ENABLED, "GET", null, callback);
    }
    // Finish to check backend Test Rule Service status

    function elementsDisabled(disabled) {
        $('.main button.btn').prop("disabled", disabled);
        $('textarea').prop("disabled", disabled);
    }

    checkTestRulePageEnabled();

    // Check EI Backend Server Status ########################################

    checkBackendStatus();

    // END OF EI Backend Server check #########################################
});
