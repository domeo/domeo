function BibmanCtrl($scope, $sce, $http) {
	
	$scope.paginationSize = [{ name: "2", id: 0 }, { name: "10", id: 1 }, { name: "20", id: 2 }, { name: "30", id: 2 }, { name: "40", id: 2 }, { name: "50", id: 2 }];
	
	// ---------------------------
	//  STAGED
	// ---------------------------
	$scope.stagedBibliography = [];
	
	$scope.$watch('stagedBibliography', function() {
		$scope.staged = $scope.stagedBibliography.length;
	}, true)
	
	$scope.stage = function() {
		if($scope.selectedBibliography.length==0) {
			alertify.alert('No bibliographic item selected<br/><br/><span style="font-weight: bold;">Help</span>');
		} else {
			angular.forEach($scope.selectedBibliography, function(selection) {
				if ($scope.stagedBibliography.indexOf(selection) === -1) {
		            $scope.stagedBibliography.push(selection);
		            selection.staged = true;
		        }
			});
		}
	}

	$scope.clearStaged = function() {
		if($scope.stagedBibliography.length==0) {
			alertify.alert('No staged items to clear<br/><br/><span style="font-weight: bold;">Help</span>');
		} else {
			alertify.confirm("Clear the staged references?", function (e) {
			    if (e) {
			    	angular.forEach($scope.stagedBibliography, function(bibliographicItem) {
			    		bibliographicItem.staged=false;
			    	});
			    			
			    	$scope.stagedBibliography = [];
			    	$scope.$digest(); // fire all the watches
			    } else {
			        // user clicked "cancel"
			    }
			});	
		}
	}
	
	$scope.displayStaged = function() {
		if($scope.stagedBibliography.length==0) {
			alertify.alert('No bibliographic item staged<br/><br/><span style="font-weight: bold;">Help</span>');
		}
	}
	
	$scope.shareStaged = function() {
		if($scope.stagedBibliography.length==0) {
			alertify.alert('No staged items to share<br/><br/><span style="font-weight: bold;">Help</span>');
		} else {
			alertify.alert('share staged: ' + $scope.selectedBibliography);
		}
	}	
	
	$scope.exportStaged = function() {
		if($scope.stagedBibliography.length==0) {
			alertify.alert('No staged items to export<br/><br/><span style="font-weight: bold;">Help</span>');
		} else {
			alertify.alert('export staged: ' + $scope.selectedBibliography);
		}
	}	
	
	
	$scope.$watch('bibresults', function() {		
		$scope.paginationTotal = $scope.stagedBibliography.length;
	}, true)
	
	
	// ---------------------------
	//  SELECTION
	// ---------------------------	
	$scope.selectedBibliography = [];
	
	$scope.$watch('selection', function() {
		$scope.selectedBibliographyTotal = $scope.selectedBibliography.length;
	}, true)
	
	$scope.isSelected = function(id) {
		return $scope.selected.indexOf(id) >= 0;
	};
	
	$scope.toggleSelection = function (bibitem) {
        if ($scope.selectedBibliography.indexOf(bibitem) === -1) {
            $scope.selectedBibliography.push(bibitem);
        } else {
            $scope.selectedBibliography.splice($scope.selectedBibliography.indexOf(bibitem), 1);
        }
    };
	
	$scope.notifySelection = function($event) {
		var checkbox = $event.target;
		var action = (checkbox.checked ? true : false);
		if(action==true) {
			if ($scope.selectedBibliography.indexOf(checkbox.value) != -1) return;
			$scope.selectedBibliography.push(checkbox.value);
		} else {
			$scope.selectedBibliography.pop(checkbox.value)
		}
	}
	
	$scope.deselectAll = function() {
		for (var i = 0; i < $scope.bibliographicResults.length; i++) {
		    var entity = $scope.bibliographicResults[i];
		    entity.selected = false;
		}
	}
	
	$scope.selectAll = function($event) {
		var checkbox = $event.target;
		var action = (checkbox.checked ? true : false);
		for (var i = 0; i < $scope.bibliographicResults.length; i++) {
			var entity = $scope.bibliographicResults[i];
			entity.selected = action;
			if ($scope.selectedBibliography.indexOf(entity) === -1) {
				$scope.selectedBibliography.push(entity);
			}
		}
	};
	
	$scope.isSelectedAll = function() {
		return $scope.selectedBibliography.length === $scope.bibliographicResults.length;
	};
	
	// ---------------------------
	//  PAGINATION
	// ---------------------------	
	$scope.pages = [];
	
	$scope.paginationSizeSelection = $scope.paginationSize[1];
	$scope.paginationTotal = 0;
	$scope.paginationOffset = 0;
	$scope.paginationMax = 2;
	$scope.paginationTop = '';
	
	$scope.refreshPagination  = function() {
		
		console.log('refreshingPagination max: ' + $scope.paginationMax + ' offset: ' + $scope.paginationOffset + ' total: ' + $scope.paginationTotal);
		//console.log($scope.paginationTotal /  $scope.paginationMax);
		//console.log(Math.floor($scope.paginationTotal /  $scope.paginationMax));
		//console.log($scope.paginationTotal %  $scope.paginationMax)
		
		var linksNumber = Math.floor($scope.paginationTotal /  $scope.paginationMax) + (($scope.paginationTotal %  $scope.paginationMax>0)?1:0);
		//console.log("# links "+ linksNumber);
		var currentPage = 0
		if($scope.paginationOffset>0) currentPage = Math.floor($scope.paginationTotal /  $scope.paginationOffset);
			
		$scope.pages = [];
		for(var i=0;i<linksNumber;i++) {
			$scope.pages.push(i);
		}
	}

	// ---------------------------
	//  SEARCH
	// ---------------------------	
	$scope.bibliographicResults = [];
	$scope.searchText ="";
	
	$scope.search = function(page) {
		$scope.paginationMax = $scope.paginationSizeSelection.name;
		var results;
	    $http({method: 'GET', url: '/Domeo/bibliography/searchBibliography'}).
		    success(function(data, status, headers, config) {
		    	results = data;
		    	
		    	$scope.paginationOffset = (page ? page*$scope.paginationMax:0);
				$scope.paginationTotal = results.length;

				$scope.bibliographicResults = [];
				for ( var i = $scope.paginationOffset; i < (Math.min($scope.paginationOffset+$scope.paginationMax,$scope.paginationTotal)); i++) {
					$scope.bibliographicResults.push(results[i]);
				}		
				$scope.refreshPagination();
				document.getElementById("selectAll").checked = false;
		    }).
		    error(function(data, status, headers, config) {
		    	results = mockupResults;
		    });
		
		console.log("search [term:" + $scope.searchText + ", page:" + (page?page:0) + ", max:" + $scope.paginationMax + "]")
	};
	
	$scope.urlOptions = function(provider, id, url) {
		alertify.alert("<span style='font-weight: bold;'>" + provider + "</span><br/><br/> ID: " + id + " <br/><br/> URL: <a target='_blank' href='" + url + "'>" + url + "</a><br><br>Annotate in Domeo");
	};

	
	// ---------------------------
	//  MACKUP DATA
	// ---------------------------
	var mockupResults = [ {
		selected : false,
		id: '234234324j23lk432jl23k23j223232',
	    url: 'http://www.ncbi.nlm.nih.gov/pmc/articles/PMC2700002/',
	    title: 'Abnormal motor phenotype in the SMNDelta7 mouse model of spinal muscular atrophy',
	    starred: false,
	    createdOn: '2013-12-09 17:36:33.0',
	    reference: {
	    	authors: "Butchbach ME, Edwards JD, Burghes AH",
	    	title: "Abnormal motor phenotype in the SMNDelta7 mouse model of spinal muscular atrophy.",
	    	info: "Neurobiology of disease. 2007 Aug ;Vol 27 Issue 2 :207-19"
	    }, 
	}, {
		selected : false,
		id: '234234324j23lk432jl23k23j223234',
	    url: 'http://www.jbiomedsem.com/content/2/S2/S4',
	    title: 'An open annotation ontology for science on web 3.0.',
	    starred: true,
	    createdOn: '2013-12-09 17:36:33.0',
	    reference: {
	    	authors: "Ciccarese P, Ocana M, Garcia Castro LJ, Das S, Clark T",
	    	title: "An open annotation ontology for science on web 3.0.",
	    	info: "Journal of biomedical semantics. 2011 ;Vol 2 Suppl 2 :S4 "
	    }
	}, {
		selected : false,
		id: '234234324j23lk432jl23k23j223236',
	    url: 'http://www.jbiomedsem.com/content/2/S2/S4',
	    title: 'eXframe: A Semantic Web Platform for Genomics Experiments.',
	    starred: true,
	    createdOn: '2013-12-09 17:36:33.0',
	    reference: {
	    	authors: "Merrill E, Corlosquet S, Ciccarese P, Clark T and Das S",
	    	title: "eXframe: A Semantic Web Platform for Genomics Experiments.",
	    	info: "Bio-Ontologies 2013. July 20, 2013, Berlin Germany."
	    }
	}];
}
