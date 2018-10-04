var app = angular.module('translatorApp', ["ngRoute"]);
console.log("router");
app.config(function($routeProvider) {
    $routeProvider
    .when("/", {
    	controller: "mainCtrl",
        templateUrl : "view/main.html"
    })
    .when("/contact", {
    	controller: "contactController",
        templateUrl : "view/contact.html"
    })
    .when("/about", {
        templateUrl : "view/about.html"
    })
    .when("/translated", {
    	controller: "translatedController",
    	templateUrl : "view/translatedfiles.html"
    }).otherwise({
        template : "<h1>None</h1><p>Nothing has been selected</p>"
    });;
});