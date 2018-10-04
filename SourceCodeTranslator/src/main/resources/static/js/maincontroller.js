app.controller('mainCtrl', function($scope, $http, $httpParamSerializer,$window) {
	console.log("controller");
	$scope.loadingflag=true;
	$scope.translatedflag=false;
	$scope.loadingtext="loading languages.."
	$http.get("/codetranslator/api/languages").then(function(response){
		$scope.languages=response.data;
		$scope.srclan='Spanish';
		$scope.tgtlan='English';
	});
	$http.get("/codetranslator/api/supportedtypes").then(function(response){
		$scope.supportedTypes=response.data;
	});
	$scope.loadingflag=false;
	$scope.loadingtext="Loading.."
	
	$scope.translate = function(formdat){
	
		 var fd = new FormData(formdat);
		 var filebox = document.getElementById("file");
		 $scope.file = filebox.files[0];
	       fd.append('file', $scope.file);
	        var typeTextbox = document.getElementById("filetype");
	        $scope.filetype = typeTextbox.value;
	        var srcln = $scope.srclan;
	        var tgtln = $scope.tgtlan;
	        var ptflag = $scope.isplaintxt;
	        console.log("flag = "+ptflag);
	        if(ptflag === undefined){
	        	console.log("changing flag to false.");
	        	ptflag = false;
	        }
	        var url = "/codetranslator/api/file/"+$scope.filetype+"?sourceLanguage="+srcln+"&targetLanguage="+tgtln+"&plainTxtFlag="+ptflag;
	    	$scope.loadingflag=true;
	    	$scope.loadingtext="Translating file..";
	        $http.post(url, fd, {
                transformRequest : angular.identity,
                headers : {
                    'Content-Type' : undefined
                }}).then(function(response){
                	var name = response.data.name;
                	console.log('File upload complete.'+response.data);
                	$scope.loadingflag=false;
                	$scope.translatedflag=true;
                	$scope.downloadFileLink = "/codetranslator/api/download/file?fileName="+name;
                },function(error){
                	$scope.loadingflag=false;
                	window.alert("Something went wrong! try again later. Or contact the developer");
                });
	}

	$scope.downloaded = function(){
		$scope.translatedflag=false;
	}
	
});