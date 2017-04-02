<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en" ng-app="myBus">
<head>
    <meta charset="UTF-8">
    <title>My Bus </title>


    <link rel="stylesheet" href="node_modules/bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="node_modules/bootstrap/dist/css/bootstrap-theme.css">
    <link rel="stylesheet" href="node_modules/sweetalert/dist/sweetalert.css">
    <link rel="stylesheet" href="node_modules/ng-table/bundles/ng-table.css">

    <link href="node_modules/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <script src="lib/underscore-min-1.5.2.js"></script>
    <script src="node_modules/jquery/dist/jquery.js"></script>
    <script src="node_modules/bootstrap/dist/js/bootstrap.js"></script>
    <script src="node_modules/angular/angular.js"></script>
    <script src="node_modules/angular-animate/angular-animate.js"></script>

    <script src="node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js"></script>
    <script src="node_modules/angular-strap/dist/angular-strap.js"></script>
    <script src="node_modules/angular-strap/dist/angular-strap.tpl.min.js"></script>
    <script src="node_modules/angular-ui-router/release/angular-ui-router.min.js"></script>
    <script src="node_modules/sweetalert/dist/sweetalert.min.js"></script>
    <script src="node_modules/ng-table/bundles/ng-table.js"></script>
    <script src="js/app.js"></script>
    <script src="js/modules/usersModule.js"></script>
    <script src="js/services/appConfigManager.js"></script>
    <script src="js/modules/amenitiesModule.js"></script>
    <script src="js/modules/cityModule.js"></script>
    <script src="js/modules/vehicleModule.js"></script>
    <script src="js/modules/branchOfficeModules.js"></script>
    <script src="js/modules/serviceReportsModule.js"></script>
    <script src="js/modules/paymentModule.js"></script>
    <script src="js/modules/routeModule.js"></script>
    <script src="js/modules/cancelModule.js"></script>
    <script src="js/modules/roleModule.js"></script>
    <script src="js/modules/agentModule.js"></script>
    <script src="js/modules/dueReportModule.js"></script>
    <script src="js/modules/serviceComboModule.js"></script>
    <script src="js/modules/cashTransfersModule.js"></script>
    <script src="js/modules/vehicleExpensesModule.js"></script>

    <script src="js/services/paginationService.js"></script>


    <script src="js/controllers/headerNavBarhomeCtrl.js"></script>
    <script src="js/directives/ng-really.js"></script>
    <script src="js/directives/pwCheck.js"></script>
    <script src="js/directives/stateOptions.js"></script>
    <script src="js/directives/datePicker.js"></script>
    <script src="js/directives/myMenu.js"></script>
    <script src="js/directives/someDirectives.js"></script>
    <script src="js/filters/arrayNoneFilter.js"></script>
    <script src="js/filters/range.js"></script>
    <script src="js/providers/stateValueProvider.js"></script>
    <link rel="stylesheet" href="assets-new/css/ionicons.min.css">
    <script src="js/filters/someFilters.js"></script>

    <link rel="stylesheet" href="css/app.css">

</head>
<body>

        <nav class="navbar navbar-default panel_heading_background ng-scope" role="navigation" ng-controller="headerNavBarhomeCtrl">
            <div class="navbar-header">
                <!-- ngIf: general_operater -->
                <!--<div class="pull-left ng-scope" ng-if="general_operater">
                    <a href="" ui-sref-active="active" ng-click="redirectToHome()">
                        <img ng-src="library/images/srikrishna.jpg" class="img-responsive image_logo" alt="Responsive image" src="library/images/srikrishna.jpg">
                    </a>
                </div> --> <!-- end ngIf: general_operater -->
                <a class="navbar-brand" href="index.html" title="HOME"><i class="fa fa-bus"></i> Sri Krishna <span>travels</span></a>
            </div>
            <div>
                <div class="collapse navbar-collapse in" collapse="isCollapsed" style="height: auto;">
                    <div class="nav navbar-nav navbar-right navbar_background_clr">
                        <!-- ngIf: privlgeAndCheck.showInchargeAmounts -->
                        <li class="dropdown header_li_border" ng-if="!isAdmin()">
                           <a href="#!/payments"> Cash Balance: {{branchOffice.cashBalance|number:2}}</a>
                        </li>
                        <li class="dropdown header_li_border" dropdown="" on-toggle="toggled(open)">
                            <a href="#" class="dropdown-toggle panel_title_color" dropdown-toggle="" role="button" ng-click="getNotification()" aria-expanded="false" aria-haspopup="true">
                                <i class="ace-icon fa fa-bell icon-animated-bell"></i>
                            </a>
                            <ul class="dropdown-menu dropdown-caret navbar-pink dropdown-navbar" role="menu">
                                <li class="dropdown-header ng-binding">
                                    <i class="ace-icon fa fa-exclamation-triangle"></i>
                                    Pending Tasks To Do
                                </li>
                                <li class="dropdown-content ace-scroll">
                                    <div class="scroll-track show h_154">
                                        <div class="scroll-bar h_132 top_0"></div>
                                    </div>
                                    <div class="scroll-content">
                                        <ul class="dropdown-menu dropdown-navbar navbar-pink">

                                        </ul>
                                    </div>
                                </li>
                                <!-- ngIf: totalCount>0 -->
                            </ul>
                        </li>
                        <li class="header_li_border">
                            <a class="welcome panel_title_color ng-binding">
                                <i class="fa fa-calendar bigger-110"></i>
                                &nbsp;{{currentDate()}}  &nbsp;

                            </a>
                        </li>
                        <li class="dropdown header_li_border" dropdown="" on-toggle="toggled(open)">
                            <a href="#" class="dropdown-toggle welcome panel_title_color ng-binding" dropdown-toggle="" role="button" aria-expanded="false" aria-haspopup="true">
                                Welcome &nbsp;
                                <b href="" ui-sref-active="active" class="handCursor ng-binding">
                                    {{userName()}}
                                </b>
                                <span class="caret navebar_caret"></span>
                            </a>
                            <ul class="dropdown-menu" role="menu">
                                <li>
                                    <a ui-sref="userProfile" class="ng-binding" href="#/userProfile">
                                        Profile
                                    </a>
                                </li>
                                <li>
                                    <a ui-sref="changepassword" class="ng-binding" href="#/changepassword">
                                        Change Password
                                    </a>
                                </li>
                            </ul>
                        </li>
                        <li class="header_li_border">
                            <a ng-click="logout()" class="welcome panel_title_color ng-binding" ui-sref-active="active" href="login?logout">
                                <i class="fa fa-power-off panel_title_color"></i>
                                Logout&nbsp; &nbsp;&nbsp;&nbsp;
                            </a>
                        </li>

                        <!--tooltip="{{'Counter'|translate}} : {{counterToolTip.counterName}} {{'Shift'|translate}} : {{counterToolTip.shiftName}}"-->

                        <!-- ngIf: isCounterLogoutShow -->
                    </div>
                </div>
                <!-- /.navbar-collapse -->
            </div>
            <!-- /.container-fluid -->
        </nav>
        <div class="row">
            <div class="col-xs-2">
                <div id="sidebar-menu" class="nav-side-menu">
                    <i class="fa fa-bars fa-2x toggle-btn" data-toggle="collapse" data-target="#menu-content"></i>

                    <div class="menu-list">

                        <ul id="menu-content" class="menu-content collapse out">
                            <li>
                                <a href="#">
                                    <i class="fa fa-home fa-lg"></i> Home
                                </a>
                            </li>

                            <li data-toggle="collapse" data-target="#service" class="collapsed">
                                <a><i class="fa fa-globe fa-lg"></i> Services <span class="arrow"></a>
                            </li>
                            <ul class="sub-menu collapse" id="service">
                                <li>New Service 1</li>
                            </ul>


                            <li data-toggle="collapse" data-target="#master" class="collapsed">
                                <a><i class="fa fa-book fa-lg"></i> Master <span class="arrow"></a>
                            </li>
                            <ul class="sub-menu collapse" id="master">
                                <my-menu label="Agents" class="nav navbar-nav  col-md-12"></my-menu>
                                <my-menu label="Amenities" class="nav navbar-nav  col-md-12"></my-menu>
                                <my-menu label="BranchOffices" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu label="Cities" class="nav navbar-nav col-md-12 " ></my-menu>
                                <my-menu label="ServiceCombo" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu label="Roles" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu label="Routes" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu label="Users" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu label="Vehicles" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu label="Vehicle Staff" class="nav navbar-nav col-md-12"></my-menu>
                            </ul>
                            <li data-toggle="collapse" data-target="#reports" class="collapsed">
                                <a><i class="fa fa-book fa-lg"></i>Reports <span class="arrow"></a>
                            </li>
                            <ul class="sub-menu collapse" id="reports">
                                <my-menu url="serviceReports" label="ServiceReports" class="nav navbar-nav  col-md-12"></my-menu>
                                <my-menu label="DueReport" class="nav navbar-nav  col-md-12"></my-menu>
                                <my-menu label="CashTransfers" class="nav navbar-nav col-md-12">Cash Transfers</my-menu>
                                <my-menu label="Payments" class="nav navbar-nav  col-md-12">Payments</my-menu>
                                <my-menu label="VehicleExpenses" class="nav navbar-nav col-md-12"></my-menu>

                            </ul>

                            <li>
                                <a href="#">
                                    <i class="fa fa-user fa-lg"></i> Profile
                                </a>
                            </li>

                            <li>
                                <a href="#">
                                    <i class="fa fa-users fa-lg"></i> Users
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-xs-9">
                <breadscrumb class="ng-scope">
                    <div class="breadcrumbs printhide" ng-click="breadscrumb_print_fn()">
                   <div class="view-container">
                    <div class="view-frame">
                        <ui-view>Select from Menu</ui-view>
                    </div>
                </div>
            </div>
        </div>
<script src="js/directives/menu.js"></script>

</body>
</html>