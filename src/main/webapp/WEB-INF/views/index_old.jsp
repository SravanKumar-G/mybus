<!DOCTYPE html>
<html lang="en" ng-app="myBus">
<head>
    <meta charset="UTF-8">
    <title>My Bus </title>

    <script src="lib/underscore-min-1.5.2.js"></script>
    <script src="bower_components/ng-file-upload/angular-file-upload-shim.min.js"></script>
    <script src="bower_components/angular/angular.js"></script>
    <script src="bower_components/ng-file-upload/angular-file-upload.min.js"></script>
    <script src="bower_components/ui-router/angular-ui-router.min.js"></script>
    <script src="bower_components/angular-animate/angular-animate.min.js"></script>
    <script src="bower_components/angular-touch/angular-touch.min.js"></script>
    <script src="bower_components/angular-strap/dist/angular-strap.min.js"></script>
    <script src="bower_components/angular-strap/dist/angular-strap.tpl.min.js"></script>
    <script src="bower_components/ng-table/ng-table.min.js"></script>
    <script src="bower_components/jquery/dist/jquery.js"></script>
    <!-- <script src="bower_components/angular-unsavedChanges/dist/unsavedChanges.js"></script> -->
    <script src="bower_components/spin.js/spin.js"></script>
    <script src="bower_components/angular-spinner/angular-spinner.js"></script>
    <script src="bower_components/sweet-alert/dist/sweetalert.min.js"></script>
    <script src="bower_components/angular-bootstrap/ui-bootstrap-tpls.js"></script>
    <script src="bower_components/drag-drop/angular-drag-and-drop-lists.js"></script>
    <script src="lib/async.js"></script>
    <script src="js/app.js"></script>
    <script src="js/controllers/citiesController.js"></script>
    <script src="js/controllers/expensesController.js"></script>
    <script src="js/controllers/boardingPointsListController.js"></script>
    <script src="js/controllers/vehicleController.js"></script>
    <script src="js/controllers/usersController.js"></script>
    <script src="js/controllers/agentPlanController.js"></script>
    <script src="js/controllers/personController.js"></script>
    <script src="js/controllers/busLayoutController.js"></script>
    <script src="js/controllers/busLayoutEditController.js"></script>
    <script src="js/controllers/busServiceController.js"></script>
    <script src="js/controllers/busServiceEditController.js"></script>
    <script src="js/controllers/homeController.js"></script>
    <script src="js/controllers/busDetailsController.js"></script>
    <script src="js/controllers/routesController.js"></script>
    <script src="js/controllers/paymentController.js"></script>
    <script src="js/controllers/tripController.js"></script>
    <script src="js/controllers/amenitiesController.js"></script>
    <script src="js/controllers/rolesController.js"></script>
    <script src="js/controllers/bookingController.js"></script>
    <script src="js/controllers/shipmentControllers.js"></script>
    <script src="js/controllers/branchOfficeControllers.js"></script>

    <script src="js/services/bookingHelper.js"></script>
    <script src="js/services/appConfigManager.js"></script>
    <script src="js/services/userManager.js"></script>
    <script src="js/services/cityManager.js"></script>
    <script src="js/services/busManager.js"></script>
    <script src="js/services/busServiceManager.js"></script>
    <script src="js/services/routesManager.js"></script>
    <script src="js/services/personService.js"></script>
    <script src="js/services/vehicleManager.js"></script>
    <script src="js/services/agentPlanManager.js"></script>
    <script src="js/services/paymentManager.js"></script>
    <script src="js/services/expensesManager.js"></script>
    <script src="js/services/tripManager.js"></script>
    <script src="js/services/amenitiesManager.js"></script>
    <script src="js/services/roleManager.js"></script>

    <script src="js/services/cancelManager.js"></script>

    <script src="js/directives/ng-really.js"></script>
    <script src="js/directives/pwCheck.js"></script>
    <script src="js/directives/stateOptions.js"></script>
    <script src="js/directives/datePicker.js"></script>
    <script src="js/directives/myMenu.js"></script>
    <script src="js/directives/inputNumber.js"></script>
    <script src="js/filters/arrayNoneFilter.js"></script>
    <script src="js/filters/range.js"></script>
    <script src="js/providers/stateValueProvider.js"></script>
    <link rel="stylesheet" href="assets-new/css/ionicons.min.css">
    <script src="js/filters/someFilters.js"></script>
    <link rel="stylesheet" href="bower_components/sweet-alert/dist/sweetalert.css">
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
            <a class="navbar-brand" href="index.html" title="HOME"><i class="ion-android-bus"></i> Sri Krishna <span>travels</span></a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <my-menu label="DashBoard" class="nav navbar-nav navbar-left"></my-menu>
                <li class="nav navbar-nav navbar-left dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown">Configuration
                        <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <my-menu label="BranchOffices" class="nav navbar-nav col-md-12 " ></my-menu>
                        <my-menu label="Cities" class="nav navbar-nav col-md-12 " ></my-menu>
                        <my-menu label="Amenities" class="nav navbar-nav  col-md-12"></my-menu>
                        <my-menu label="Routes" class="nav navbar-nav col-md-12"></my-menu>
                        <my-menu label="Layouts" class="nav navbar-nav col-md-12"></my-menu>
                        <my-menu label="Vehicles" class="nav navbar-nav col-md-12"></my-menu>
                        <my-menu label="Services" class="nav navbar-nav col-md-12"></my-menu>
                        <my-menu label="Trip" class="nav navbar-nav col-md-12"></my-menu>
                        <my-menu label="Plans" class="nav navbar-nav col-md-12"></my-menu>
                    </ul>
                </li>
                <li class="nav navbar-nav navbar-left dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown">Admin
                        <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <my-menu label="Roles" class="nav navbar-nav col-md-12"></my-menu>
                        <my-menu label="ManagingRoles" class="nav navbar-nav col-md-12"></my-menu>
                    </ul>
                </li>
                <my-menu label="Finance" class="navbar-left nav navbar-nav"></my-menu>
                <my-menu label="Reports" class="navbar-left nav navbar-nav"></my-menu>
                <my-menu label="Shipments" class="navbar-left nav navbar-nav"></my-menu>
                <my-menu label="Users" class="navbar-left nav navbar-nav"></my-menu>
                <my-menu label="Payment" class="navbar-left nav navbar-nav"></my-menu>
                <li><a href="/logout" style=" background: #2f9e43; color: #FFF;"> <b style="color:black;">{{userManager.getUser().username}}</b> Logout</a></li>
            </ul>


        </div><!--/.nav-collapse -->
    </div>

</div>

<div class="container">
    <div class="view-container">
        <div class="view-frame">
            <ui-view>Select from Menu</ui-view>
        </div>
    </div>
</div>

</body>
</html>