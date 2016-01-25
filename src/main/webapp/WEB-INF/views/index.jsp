<!DOCTYPE html>
<html lang="en" ng-app="myBus">
<head>
    <meta charset="UTF-8">
    <title>My Bus </title>

    <script src="lib/underscore-min-1.5.2.js"></script>
    <script src="bower_components/ng-file-upload/angular-file-upload-shim.min.js"></script>
    <script src="bower_components/angular/angular.min.js"></script>
    <script src="bower_components/ng-file-upload/angular-file-upload.min.js"></script>
    <script src="bower_components/angular-route/angular-route.min.js"></script>
    <script src="bower_components/angular-animate/angular-animate.min.js"></script>
    <script src="bower_components/angular-touch/angular-touch.min.js"></script>
    <script src="bower_components/angular-strap/dist/angular-strap.min.js"></script>
    <script src="bower_components/angular-strap/dist/angular-strap.tpl.min.js"></script>
    <script src="bower_components/ng-table/ng-table.min.js"></script>
    <script src="bower_components/jquery/dist/jquery.js"></script>
    <script src="bower_components/angular-unsavedChanges/dist/unsavedChanges.js"></script>
    <script src="bower_components/spin.js/spin.js"></script>
    <script src="bower_components/angular-spinner/angular-spinner.js"></script>
    <script src="lib/ui-bootstrap-tpls-0.11.0.min.js"></script>
    <script src="lib/async.js"></script>
    <script src="js/myBus.js"></script>
    <script src="js/controllers/citiesController.js"></script>
    <script src="js/controllers/expensesController.js"></script>
    <script src="js/controllers/boardingPointsListController.js"></script>
    <script src="js/controllers/personController.js"></script>
    <script src="js/controllers/busLayoutController.js"></script>
    <script src="js/controllers/busLayoutEditController.js"></script>
    <script src="js/controllers/homeController.js"></script>
    <script src="js/controllers/busDetailsController.js"></script>
    <script src="js/controllers/routesController.js"></script>

    <script src="js/services/appConfigManager.js"></script>
    <script src="js/services/userManager.js"></script>
    <script src="js/services/cityManager.js"></script>
    <script src="js/services/busManager.js"></script>
    <script src="js/services/routesManager.js"></script>
    <script src="js/services/personService.js"></script>
    <script src="js/services/expensesManager.js"></script>
    <script src="js/directives/ng-really.js"></script>
    <script src="js/directives/stateOptions.js"></script>
    <script src="js/filters/unsafeFilter.js"></script>
    <script src="js/filters/arrayNoneFilter.js"></script>
    <script src="js/providers/stateValueProvider.js"></script>

    <%--
        <script src="js/services/categoriesManager.js"></script>
        <script src="js/services/classificationsManager.js"></script>
        <script src="js/services/appUtils.js"></script>
        <script src="js/services/businessNavHelper.js"></script>
        <script src="js/services/geocoderService.js"></script>
        <script src="js/services/googlePlacesService.js"></script>
        <script src="js/services/infoOverlayService.js"></script>
        <script src="js/directives/ng-really.js"></script>
        <script src="js/directives/stateOptions.js"></script>
        <script src="js/filters/unsafeFilter.js"></script>
        <script src="js/filters/arrayNoneFilter.js"></script>
        <script src="js/providers/stateValueProvider.js"></script>
        <script src="js/controllers/homeController.js"></script>
        <script src="js/controllers/conditionController.js"></script>
        <script src="js/controllers/procedureController.js"></script>
        <script src="js/controllers/neighborhoodsController.js"></script>
        <script src="js/controllers/beaconController.js"></script>
        <script src="js/controllers/businessController.js"></script>
        <script src="js/controllers/businessImportController.js"></script>
        <script src="js/controllers/apiDocsController.js"></script>
        <script src="js/controllers/accountController.js"></script>
        <script src="js/controllers/categoriesController.js"></script>
        <script src="js/controllers/usersController.js"></script>--%>



    <link rel="stylesheet" href="bower_components/bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="bower_components/ng-table/ng-table.css">
    <link rel="stylesheet" href="css/app.css">

</head>
<body>
<!-- Fixed navbar -->
<div class="navbar navbar-default navbar-fixed-top" role="navigation" bs-navbar>
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li data-match-route="/dashboard"><a href="#/dashboard">Home</a></li>
                <li data-match-route="/cities"><a href="#/cities">Cities</a></li>
                <li data-match-route="/routes"><a href="#/routes">Routes</a></li>
                <li data-match-route="/persons"><a href="#/persons">Persons</a></li>
                <li data-match-route="/expenses"><a href="#/expenses">Expenses</a></li>
                <li data-match-route="/reports"><a href="#/reports">Reports</a></li>
                <li data-match-route="/layouts"><a href="#/layouts">Layouts</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/logout"> <b style="color:white;">{{userManager.getUser().username}}</b> Logout</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>

</div>

<div class="container">
    <div class="view-container">
        <div ng-view class="view-frame">

        </div>
    </div>
</div>

</body>
</html>