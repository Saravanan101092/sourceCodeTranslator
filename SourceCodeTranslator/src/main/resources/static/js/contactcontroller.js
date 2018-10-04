app.controller('contactController', function($scope, $http){
	console.log('contactController');
	$scope.msgAlert = false;
	$scope.sendMessage = function(){
		var messageString = $scope.name +" contacted you regarding "+$scope.category+".  Message: "+$scope.message+" Contact:"+$scope.email;
		var url = '/codetranslator/api/slack?message='+messageString;
		console.log(url);
		$http.get('/codetranslator/api/slack?message='+messageString).then(function(response){
			console.log("Success:"+response.data);
			$scope.msgAlert = true;
		},function(err){
			console.log(err)
		});

	}
});