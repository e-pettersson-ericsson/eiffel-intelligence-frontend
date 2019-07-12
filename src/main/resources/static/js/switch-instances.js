jQuery(document).ready(function () {
    var frontendServiceUrl = $('#frontendServiceUrl').text();
    var frontendServiceBackEndPath = "/backend";
    document.getElementById('switcher').disabled = true;

    function multipleInstancesModel(backendInstanceData) {
        var self = this;
        var selected;

        var jsonBackendInstanceData = JSON.parse(ko.toJSON(backendInstanceData));
        var instanceModels = getInstanceModels(jsonBackendInstanceData);

        self.instances = ko.observableArray();
        instanceModels.forEach(function (instanceModel) {
            self.instances.push(instanceModel);
        });

        self.checked = function () {
            document.getElementById('switcher').disabled = false;
            selected = JSON.parse(ko.toJSON(this));
            return true;
        };

        self.removeInstance = function () {
            self.instances.remove(this);
            $.ajax({
                url: frontendServiceUrl + frontendServiceBackEndPath,
                type: "DELETE",
                data: ko.toJSON(this),
                contentType: 'application/json; charset=utf-8',
                cache: false,
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    parseAndLogMessage(XMLHttpRequest.responseText);
                },
                success: function (responseData, XMLHttpRequest, textStatus) {
                    $.jGrowl(responseData.message, { sticky: false, theme: 'Notify' });
                }
            });
        };

        self.submit = function () {
            sessionStorage.selectedActive = selected.name;
            navigateToRoute('subscriptions');
        };
    }

    $.ajax({
        url: frontendServiceUrl + frontendServiceBackEndPath,
        type: "GET",
        contentType: 'application/json; charset=utf-8',
        cache: false,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            parseAndLogMessage(XMLHttpRequest.responseText);
        },
        success: function (responseData, XMLHttpRequest, textStatus) {
            var observableObject = $("#instancesModel")[0];
            ko.cleanNode(observableObject);
            ko.applyBindings(new multipleInstancesModel(responseData), observableObject);
        }
    });
});
