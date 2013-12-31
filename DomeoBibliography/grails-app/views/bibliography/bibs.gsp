<!doctype html>

<html ng-app>
<head>

<script type="text/javascript" src="${resource(dir: 'js/angularjs', file: 'angular.min.js', plugin: 'domeo-bibliography')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/angularjs', file: 'angular-resource.min.js', plugin: 'domeo-bibliography')}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'bibman.js', plugin: 'domeo-bibliography')}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'alertify.js', plugin: 'domeo-bibliography')}"></script>

<link rel="stylesheet" href="${resource(dir: 'css', file: 'reset.css', plugin: 'domeo-bibliography')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'bibman.css', plugin: 'domeo-bibliography')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css/alertify', file: 'alertify.core.css', plugin: 'domeo-bibliography')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css/alertify', file: 'alertify.bootstrap.css', plugin: 'domeo-bibliography')}" type="text/css">

<script>

</script>

</head>

<body  style="font-family: helvetica; font-size: 14px; font-weight:400;">
	<div ng-controller="BibmanCtrl" class="content" style="min-width: 600px;">
		<div class="list" style="width: 100%; background: white;">
			<div style="background: #333; color:#fff; padding: 5px;">
				[Starred:, WithReference:] 
			</div>
		</div>
		<div style="background: #ddd; padding: 8px;">
			Bibliography {{selectionInfo}}
			<span style="float: right; padding-right: 10px;">
				<a id="exportSelected" href="" ng-click="exportStaged()" style="text-decoration: none;"><img src="${resource(dir:'images',file:'export16x16.gif',plugin:'domeo-bibliography')}">  <span style="vertical-align: middle; padding-bottom: 5px; ">Export</span></a>
			</span> 
			<span style="float: right; padding-right: 10px;">
				<a id="exportSelected" href="" ng-click="shareStaged()" style="text-decoration: none;"><img src="${resource(dir:'images',file:'database-share16x16.png',plugin:'domeo-bibliography')}"> <span style="vertical-align: middle; padding-bottom: 5px; ">Share</span> |</a>
			</span> 
			<span style="float: right; padding-right: 10px;">
				<a id="exportSelected" href="" ng-click="clearStaged()"><img src="${resource(dir:'images',file:'database-minus16x16.png',plugin:'domeo-bibliography')}"> <span style="vertical-align: middle; padding-bottom: 5px; ">Clear Staged</span> |</a>
			</span>
			<span style="float: right; padding-right: 10px;">
				<a id="stageSelected" href="" ng-click="displayStaged()" style="text-decoration: none;"><img src="${resource(dir:'images',file:'database16x16.png',plugin:'domeo-bibliography')}"> <span id="stagedCounter" style="vertical-align: middle; padding-bottom: 5px; ">{{staged}}</span> <span style="vertical-align: middle; padding-bottom: 5px; ">Staged</span> |</a>
			</span>				
			<span style="float: right; padding-right: 10px;">
				<a id="stageSelected" href="" ng-click="stage()" style="text-decoration: none;"><img src="${resource(dir:'images',file:'database-plus16x16.png',plugin:'domeo-bibliography')}"> <span style="vertical-align: middle; padding-bottom: 5px; ">Stage</span></a> |
			</span>
		</div>
		<div style="background: #fff; padding: 5px;">
			 <form ng-submit="search()">
				<input type="text" ng-model="searchText" size="30" placeholder="search bibliography">
				<input class="btn-primary" type="submit" value="Search"> 
				<span ng-bind="paginationTotal"></span> Results
				<span style="float: right; padding-right: 10px">Display: 
				    <select data-ng-options="o.name for o in paginationSize" data-ng-model="paginationSizeSelection"></select>
				</span>
			</form>
		</div>
		<!-- 
		<div class="pagination" style="padding-left:10px; border-top: 0px;"> 
			<span ng-repeat="page in pages">
				<a href="" ng-click="search(page)">{{page}}</a>
			</span>
		</div>
		 -->
		<div style="padding:0px; padding-top: 10px;">
			<table class="tablelist">
				<thead>
					<tr>
						<th><input id="selectAll" type="checkbox" ng-click="selectAll($event)"></th>
						<th><img src="${resource(dir:'images',file:'star-hot16x16.png',plugin:'domeo-bibliography')}"></th>				
						<th>Title</th>
						<th>Reference</th>
						<th>Added on</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="bibitem in bibliographicResults" ng-class="$index % 2 == 0 && 'even' || 'odd'".>
						<td>
							<div ng-show="!bibitem.staged">
								<input type="checkbox" value="{{bibitem.id}}" ng-model="bibitem.selected" ng-click="toggleSelection(bibitem)">
							</div>
							<div ng-show="bibitem.staged">
								<input type="checkbox" value="{{bibitem.id}}" ng-model="bibitem.selected" ng-click="toggleSelection(bibitem)" disabled="true">
							</div>
						</td>
						<td>
							<div ng-show="bibitem.starred"><img src="${resource(dir:'images',file:'star-hot16x16.png',plugin:'domeo-bibliography')}"></div>
        					<div ng-show="!bibitem.starred"><img src="${resource(dir:'images',file:'star-cold16x16.png',plugin:'domeo-bibliography')}"></div>
						</td>
						<td>
							<span class="done-{{bibitem.selected}}">{{bibitem.title}}</span>
						</td>
						<td>
							{{bibitem.reference.authors}}
							<span class="done-{{bibitem.selected}}" style="font-weight: bold;">{{bibitem.reference.title}}</span>
							{{bibitem.reference.info}}
							
							<a ng-if="bibitem.reference.doi" ng-click="urlOptions('DOI', bibitem.reference.doi, 'http://www.ncbi.nlm.nih.gov/pubmed/'+bibitem.reference.doi)"
									href="" target="_blank" style="text-decoration: underline; color: blue;">
								{{bibitem.reference.doi}} 
							</a>
							&nbsp;
							<a ng-if="bibitem.reference.pmid" ng-click="urlOptions('PubMed', bibitem.reference.pmid, 'http://www.ncbi.nlm.nih.gov/pubmed/'+bibitem.reference.pmid)" 
									href="" style="text-decoration: underline; color: blue;">
								{{bibitem.reference.pmid}} 
							</a>
							&nbsp;
							<a ng-if="bibitem.reference.pmcid" ng-click="urlOptions('PubMedCentral', bibitem.reference.pmcid, 'http://www.ncbi.nlm.nih.gov/pubmed/'+bibitem.reference.pmcid)" 
									href="" style="text-decoration: underline; color: blue;">
								{{bibitem.reference.pmcid}} 
							</a>
						</td>
						<td>
							{{bibitem.createdOn}}
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div ng-if="pages.length>1">
			<div class="pagination" style="padding-left:10px;"> 
				<span ng-repeat="page in pages">
					<a href="" ng-click="search(page)">{{page}}</a>
				</span>
			</div>
		</div>
	</div>
</body>
</html>