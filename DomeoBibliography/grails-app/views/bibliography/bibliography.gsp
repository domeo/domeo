<!DOCTYPE html>
<%-- by Paolo Ciccarese --%>

<html>
  	<head>
    	<meta name="layout" content="domeo-layout-secured" /> 
    	<script type="text/javascript">
			var staged = new Array();
			var cache = new Array();
    	
	    	$( document ).ready(function() {
	    		$( "#selectAll" ).bind({
		    		click: function() {		    			
		    			$( ".referenceSelection" ).each(function( index ) {
			    			if($('#selectAll').is(':checked')) {
			    				selectAll();
			    			} else {
			    				deselectAll();
				    		}
		    				
		    				//alert('select all ' + index + ' - ' + this.name + ' - ' + $(this).is(':checked'));
		    			});
		    		},
		    		mouseenter: function() {
		    		// Do something on mouseenter
		    		}
	    		});
	    		deselectAll();
	    	});


	    	function selectAll() {
	    		$( ".referenceSelection" ).each(function( index ) {
	    			$(this).attr('checked','checked');
    			});
		    }
	    	
	    	function deselectAll() {
	    		$( ".referenceSelection" ).each(function( index ) {
	    			$(this).removeAttr('checked');
    			});
		    }

		    function exportSelected() {
			    var counter = 0;
			    var bibliography = new Array();
		    	$( ".referenceSelection" ).each(function( index ) {
	    			if($(this).is(':checked')) {
	    				bibliography[counter] = this.name;
	    				counter++;
	    			} else {
	    			
		    		}	
    			});
		    	alert("Export " + counter + " elements: " + bibliography);
			}

		    function stageSelected() {
			    var counter = 0;
			    var bibliography = new Array();
		    	$( ".referenceSelection" ).each(function( index ) {
	    			if($(this).is(':checked')) {
	    				bibliography[counter] = this.name;
	    				counter++;
	    			} else {
	    				
		    		}	
    			});
		    	alert("Stage " + counter + " elements: " + bibliography);
			}
    	</script>  
    	<style>
  table {
    border-collapse: collapse;
    font-size: 13px;
    width: 100%;
}
table.tablelist {
}
tr {
    border: 0 none;
}
tr > td:first-child, tr > th:first-child {
    padding-left: 1.25em;
}
tr > td:last-child, tr > th:last-child {
    padding-right: 1.25em;
}
td, th {
    line-height: 1.5em;
    padding: 0.5em 0.6em;
    text-align: left;
    vertical-align: top;
}
th {
    background-color: #EFEFEF;
    background-image: -moz-linear-gradient(center top , #FFFFFF, #EAEAEA);
    color: #666666;
    font-weight: bold;
    line-height: 1.7em;
    padding: 0.2em 0.6em;
}
thead th {
    white-space: nowrap;
}
th a {
    display: block;
    text-decoration: none;
}
th a:link, th a:visited {
    color: #666666;
}
th a:hover, th a:focus {
    color: #333333;
}
th.sortable a {
    background-position: right center;
    background-repeat: no-repeat;
    padding-right: 1.1em;
}
th.asc a {
    background-image: url("../../images/skin/sorted_asc.gif");
}
th.desc a {
    background-image: url("../../images/skin/sorted_desc.gif");
}
.odd {
    background: none repeat scroll 0 0 #F7F7F7;
}
.even {
    background: none repeat scroll 0 0 #FFFFFF;
}  	
    	</style>	
  	</head>
	<body style="padding: 0; border: 0; margin: 0;">
		<div class="content">
			<div class="list" style="width: 100%; background: white;">
				<%-- 
				<div style="background: #333; color:#fff; padding: 5px;">
					[Starred:${starred}, WithReference:${withReference}] 
				</div>
				--%>
				<div style="background: #eee; padding: 5px;">
					Bibliography <g:select id="active" name="security" from="${["All","My bibliography","Group A","Group b"]}" value="${params.security}" />
					<span style="float: right; padding-right: 10px;"><a id="exportSelected" style="text-decoration: none;" href="javascript:shareSelected()"><img src="${resource(dir:'images',file:'export16x16.gif',plugin:'domeo-bibliography')}"> Export</a></span> 
					<span style="float: right; padding-right: 10px;"><a id="exportSelected" style="text-decoration: none;" href="javascript:exportSelected()"><img src="${resource(dir:'images',file:'database-share16x16.png',plugin:'domeo-bibliography')}"> Share |</a></span> 
					<span style="float: right; padding-right: 10px;"><img src="${resource(dir:'images',file:'database-minus16x16.png',plugin:'domeo-bibliography')}"> Clear Staged |</span>
					<span style="float: right; padding-right: 10px;"><img src="${resource(dir:'images',file:'database16x16.png',plugin:'domeo-bibliography')}"> (<span id="stagedCounter">0</span>) Staged |</span>				
					<span style="float: right; padding-right: 10px;"><a id="stageSelected" href="javascript:stageSelected()" style="text-decoration: none;"><img src="${resource(dir:'images',file:'database-plus16x16.png',plugin:'domeo-bibliography')}"> Stage</a> |</span>
				</div>
				<div style="background: #fff; padding: 5px;">
					<g:form action="search">
						<g:textField name="search" value="${params.search}" style="width:200px" /> <span style="vertical-align: bottom">
						<g:actionSubmitImage value="search" action="search" src="${resource(dir: 'images', file:'search16x16.png',plugin:'domeo-bibliography')}" />
						<span style="float: right; padding-right: 10px">Display: <g:select id="active" name="max" from="${[10,20,50,100]}" value="${params.max}" /></span>
						<%-- Access: <g:select id="active" name="active" from="${["Private", "Groups", "Public"]}" value="${userInstance?.active}" /> --%>
					</g:form>
				</div>
				<div class="paginateButtons">
					<g:paginate next="Forward" prev="Back" controller="bibliography" action="search" total="${bibliographicItemsCount}" />
				</div>
				<div style="padding:5px;">
					<table class="tablelist">
						<thead>
							<tr>
								<th><g:checkBox id="selectAll" name="selectAll" value="${false}" /></th>
								<th><img src="${resource(dir:'images',file:'star-hot16x16.png',plugin:'domeo-bibliography')}"></th>
								<%-- <th><img src="${resource(dir:'images',file:'security-level16x16.gif',plugin:'domeo-bibliography')}"></th> --%>
								<g:sortableColumn property="title" title="${message(code: 'agentPerson.id.label', default: 'Title')}" />
								<g:sortableColumn property="reference" title="${message(code: 'agentPerson.id.reference', default: 'Reference')}" />
								<g:sortableColumn property="dateCreated" title="${message(code: 'agentPerson.id.label', default: 'Creation Date')}" />
							</tr>
						</thead>
						<tbody>
						<g:each in="${bibliographicItems}" status="i" var="item">
							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
								<td>
									<g:checkBox class="referenceSelection" name="${item.id}" value="${false}" />
								</td>
								<td>
									<g:if test="${item.starred==true}">
										<img src="${resource(dir:'images',file:'star-hot16x16.png',plugin:'domeo-bibliography')}">
									</g:if>
									<g:else>
										<img src="${resource(dir:'images',file:'star-cold16x16.png',plugin:'domeo-bibliography')}">
									</g:else>
								</td>
								<%--
								<td>
									-
								</td>
								--%>
								<td>
									<a href="${item.url}" target="_blank">${item.title}</a>
								</td>
								<td>
									<g:if test="${item.reference}">
										${item.reference.authorNames}
										<span style="font-weight: bold;">${item.reference.title}</span>
										${item.reference.publicationInfo}
									</g:if>
									<g:else>
										none
									</g:else>
								</td>
								<td>
									${item.dateCreated}
								</td>
							</tr>
						</g:each>	
					</table>	
				</div>
				&nbsp;Total: ${bibliographicItemsCount}
			</div>
		</div>
	</body>
</html>