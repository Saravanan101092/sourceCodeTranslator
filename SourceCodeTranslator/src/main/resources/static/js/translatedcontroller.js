app.controller('translatedController', function($scope, $http){
	$scope.files = [];
	console.log('translatedcontroller');
	$scope.getTranslatedFiles = function(){
		$http.get('/codetranslator/api/translatedfiles').then(function(response){
			$scope.files = response.data;
		});
	}
	$scope.getTranslatedFiles();
});