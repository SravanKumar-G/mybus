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
    <link href="js/s-chat/s-chat-support.css" rel="stylesheet">
    <script src="js/s-chat/s-chat-support.js"></script>
    <script src="lib/underscore-min-1.5.2.js"></script>
    <script src="node_modules/jquery/dist/jquery.js"></script>
    <script src="lib/jquery.table2excel.js"></script>
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
    <script src="js/modules/tripComboModule.js"></script>
    <script src="js/modules/cashTransfersModule.js"></script>
    <script src="js/modules/vehicleExpensesModule.js"></script>
    <script src="js/modules/expensesIncomesReportsModule.js"></script>
    <script src="js/modules/officeExpensesModule.js"></script>
    <script src="js/services/paginationService.js"></script>
    <script src="js/modules/returnTicketsModule.js"></script>
    <script src="js/modules/bookingModule.js"></script>
    <script src="js/modules/sequenceModule.js"></script>
    <script src="js/modules/fuelExpenseReportModule.js"></script>
    <script src="js/modules/invoiceModule.js"></script>
    <script src="js/modules/gstFiltersModule.js"></script>
    <script src="js/modules/suppliersModule.js"></script>
    <script src="js/modules/tripReportsModule.js"></script>
    <script src="js/modules/cargoBookingModule.js"></script>
    <script src="js/modules/collectionZoneModule.js"></script>
    <script src="js/modules/operatorAccountsModule.js"></script>
    <script src="js/modules/staffModule.js"></script>
    <script src="js/modules/cargoDashboardModule.js"></script>
    <script src="js/modules/cargoBrachSummaryModule.js"></script>

    <script src="js/modules/headerNavBarhomeCtrl.js"></script>
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

    <script>
        sChat.init('ZTmyJTKcxMoHTTshK', {
            ssl: true,
            welcomeMessage: 'Hi, how can I help you?',
            hostName: 'www.simplechat.support',
            labels: {
                sendPlaceholder: 'Send the message...',
                headerTitle: 'Welcome to Srikrishna Travels!'
            }
        });
    </script>
</head>
<body>

        <nav class="navbar navbar-default panel_heading_background ng-scope" role="navigation" ng-controller="headerNavBarhomeCtrl">
            <div class="navbar-header">
                <a class="navbar-brand" href="index.html" title="HOME"><i class="fa fa-bus"></i> Sri Krishna <span>travels</span></a>
            </div>
            
                <div class="collapse navbar-collapse in" collapse="isCollapsed" style="height: auto;">
                    <div class="nav navbar-nav navbar-right navbar_background_clr">
                        <!-- ngIf: privlgeAndCheck.showInchargeAmounts -->
                        <li class="dropdown header_li_border">
                           <a href="#!/payments"> Cash Balance: {{amountToBePaid()|number:2}}</a>
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
                    </div>
                </div>
                <!-- /.navbar-collapse -->
            
            <!-- /.container-fluid -->
        </nav>

        <div id="wrapper" class="toggled">
      
        <div  id="sidebar-wrapper" class="nav-side-menu" ng-controller="MenuBarController">
                    <div class="menu-list">
                        <ul id="menu-content" class="menu-content ">
                            <li data-toggle="collapse" data-target="#cargo" class="collapsed">
                                <a><i class="fa fa-globe fa-lg"></i> Cargo <span class="arrow"></span></a>
                            </li>
                            <ul class="sub-menu collapse" id="cargo">
                                <my-menu url="cargodashboard" label="CargoDashBoard" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu url="newBooking" label="NewBooking" class="nav navbar-nav col-md-12" ng-if="canAccessModule('cargodashboard')" ></my-menu>
                                <my-menu url="cargoBookings" label="CargoBookings" class="nav navbar-nav col-md-12" ng-if="canAccessModule('cargodashboard')"></my-menu>
                                <my-menu url="branchbookingsummary" label="BranchBookingSummary" class="nav navbar-nav col-md-12" ng-if="canAccessModule('cargodashboard')"></my-menu>
                                <my-menu url="unloadingsheet" label="UnloadingSheet" class="nav navbar-nav col-md-12" ng-if="canAccessModule('cargodashboard')"></my-menu>
                                <my-menu url="deliverysheet" label="DeliverySheet" class="nav navbar-nav col-md-12" ng-if="canAccessModule('cargodashboard')"></my-menu>
                            </ul>
                            <li data-toggle="collapse" data-target="#analytics" class="collapsed">
                                <a><i class="fa fa-globe fa-lg"></i> Analytics <span class="arrow"></span></a>
                            </li>
                            <ul class="sub-menu collapse" id="analytics">
                                <my-menu url="bookingAnalytics" label="BookingAnalytics" class="nav navbar-nav  col-md-12" ng-if="canAccessModule('bookinganalytics')"></my-menu>
                            </ul>
                            <li data-toggle="collapse" data-target="#master" class="collapsed">
                                <a><i class="fa fa-book fa-lg"></i> Master <span class="arrow"></span></a>
                            </li>
                            <ul class="sub-menu collapse" id="master">
                                <my-menu label="Agents" class="nav navbar-nav  col-md-12" ng-if="canAccessModule('agents')"></my-menu>
                                <my-menu label="Amenities" class="nav navbar-nav  col-md-12" ng-if="canAccessModule('amenities')"></my-menu>
                                <my-menu label="BranchOffices" class="nav navbar-nav col-md-12" ng-if="canAccessModule('branchoffices')"></my-menu>
                                <my-menu label="Cities" class="nav navbar-nav col-md-12 " ng-if="canAccessModule('cities')"></my-menu>
                                <my-menu label="CollectionZones" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu label="GSTFilters" class="nav navbar-nav col-md-12" ></my-menu>
                                <my-menu label="Suppliers" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu label="OperatorAccounts" class="nav navbar-nav col-md-12" ng-if="currentuser.superAdmin"></my-menu>
                                <my-menu label="Roles" class="nav navbar-nav col-md-12" ng-if="currentuser.admin"></my-menu>
                                <my-menu label="ManageRoles" class="nav navbar-nav col-md-12" ng-if="currentuser.admin"></my-menu>
                                <my-menu label="Routes" class="nav navbar-nav col-md-12" ng-if="canAccessModule('routes')"></my-menu>
                                <my-menu label="ServiceCombo" class="nav navbar-nav col-md-12" ng-if="canAccessModule('servicecombo')"></my-menu>
                                <my-menu label="TripCombo" class="nav navbar-nav col-md-12" ng-if="canAccessModule('tripcombo')"></my-menu>
                                <my-menu label="Users" class="nav navbar-nav col-md-12" ></my-menu>
                                <my-menu label="Vehicles" class="nav navbar-nav col-md-12" ng-if="canAccessModule('vehicles')"></my-menu>
                                <my-menu label="Staff" class="nav navbar-nav col-md-12" ng-if="canAccessModule('staff')"></my-menu>
                            </ul>
                            <li data-toggle="collapse" data-target="#reports" class="collapsed">
                                <a><i class="fa fa-book fa-lg"></i>Reports <span class="arrow"></span></a>
                            </li>
                            <ul class="sub-menu collapse" id="reports">
                                <my-menu url="tripreports" label="TripReports" class="nav navbar-nav  col-md-12" ng-if="canAccessModule('tripreports')"></my-menu>
                                <my-menu url="serviceReports" label="ServiceReports" class="nav navbar-nav  col-md-12" ng-if="canAccessModule('servicereports')"></my-menu>
                                <my-menu label="FuelExpenseReports" class="nav navbar-nav  col-md-12" ng-if="canAccessModule('fuelexpensereports')"></my-menu>
                                <my-menu label="DueReport" class="nav navbar-nav  col-md-12" ng-if="canAccessModule('duereport')"></my-menu>
                                <my-menu label="CashTransfers" class="nav navbar-nav col-md-12" ng-if="canAccessModule('cashtransfers')">Cash Transfers</my-menu>
                                <my-menu label="Payments" class="nav navbar-nav  col-md-12" ng-if="canAccessModule('payments')">Payments</my-menu>
                                <my-menu label="OfficeExpenses" class="nav navbar-nav col-md-12" ng-if="canAccessModule('officeexpenses')"></my-menu>
                                <my-menu label="ExpensesIncomesReports" class="nav navbar-nav col-md-12" ng-if="canAccessModule('expensesincomesreports')"></my-menu>
                                <my-menu label="ReturnTickets" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu label="CashBalances" class="nav navbar-nav col-md-12" ng-if="canAccessModule('cashbalances')"></my-menu>
                                <my-menu label="Invoices" class="nav navbar-nav col-md-12"></my-menu>
                                <my-menu label="ShipmentSequence" class="nav navbar-nav col-md-12"></my-menu>

                            </ul>
                            <li data-toggle="collapse" data-target="#checklist" class="collapsed" ng-if="canAccessModule('pendingreports')">
                                <a><i class="fa fa-book fa-lg"></i> Check List <span class="arrow"></span></a>
                            </li>
                            <ul class="sub-menu collapse" id="checklist">
                                <my-menu url="pendingreports" label="PendingReports" class="nav navbar-nav  col-md-12" ng-if="canAccessModule('pendingreports')"></my-menu>
                                <my-menu url="reportstobereviewed" label="ReportsToBeReviewed" class="nav navbar-nav  col-md-12" ng-if="canAccessModule('reportstobereviewed')"></my-menu>
                            </ul>

                            <li data-toggle="collapse" data-target="#profile" class="collapsed">
                                <a><i class="fa fa-book fa-lg"></i> Profile <span class="arrow"></span></a>
                            </li>
                            <ul class="sub-menu collapse" id="profile">
                                <my-menu url="updatePassword" label="UpdatePassword" class="nav navbar-nav  col-md-12"></my-menu>
                            </ul>
                            <li>
                                <a href="#">
                                    <i class="fa fa-users fa-lg"></i> Users
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
        <!-- /#sidebar-wrapper -->
        <a href="#menu-toggle" class="btn btn-default" id="menu-toggle" style="position: relative;top: 65px;z-index: 2;"><i class="fa fa-bars fa-2x toggle-btn"></i> </a>
        <!-- Page Content -->
        <div id="page-content-wrapper">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <breadscrumb class="ng-scope">
                            <div class="breadcrumbs printhide" ng-click="breadscrumb_print_fn()">
                               <div class="view-container">
                                    <div class="view-frame">
                                        <ui-view>Select from Menu</ui-view>
                                    </div>
                                </div>
                            </div>
                        </breadscrumb>
                    </div>
                </div>
            </div>
        </div>
        <!-- /#page-content-wrapper -->

    </div>
    <!-- /#wrapper -->
    <script>
    $("#menu-toggle").click(function(e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });
    </script>
<script src="js/directives/menu.js"></script>

</body>
</html>