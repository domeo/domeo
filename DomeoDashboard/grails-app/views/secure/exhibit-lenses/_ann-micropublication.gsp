<div ex:role="lens" ex:itemTypes="ao:MicroPublicationAnnotation" style="display: none" width="100%">
		<table width="740px" class="barContainer">
			<tr>
				<td width="600px">
					<div class="topBar">
						<div class="titleBar"><span ex:content=".label"></span> 
							created on <span ex:content=".createdOn"></span> by 
				       		<a ex:if-exists=".createdByUri" ex:href-subcontent="http://www.google.com/search/?q={{.createdByUri}}">
				       			<span ex:content=".createdByName"></span></a>
				       		<br/>
				       	</div>
				       	<div class="provenanceBar">
				       		Last saved on <span ex:content=".lastSavedOn"></span> <span ex:if-exists=".version">with version <span ex:content=".version"></span></span>
				       	</div>
 					</div>
				</td>
				<td>
					<div class="miscBar">
			       		<span ex:if-exists=".commentsCounter">
			       			<img src="${resource(dir:'images/secure',file:'comments16x16.png',plugin:'users-module')}"/> <span ex:content=".commentsCounter"></span> Comments
			       		</span>
			       		<!--  <img src="${resource(dir:'images/secure',file:'comment16x16.png',plugin:'users-module')}"/>  Leave a comment -->
			       	</div>
				</td>
			</tr>
	    </table>
       	<div class="annbody">
       		<div ex:content=".content" class="annbody-content"></div>
       	</div>
       	<div class="contextTitle">context</div>
       	<div class="context">
            <div ex:if-exists=".textQuoteSelector" class="context-content">
       			...
	       		<span ex:content=".prefix" class="prefix"></span>
	       		<span ex:content=".match" class="match"></span>
	       		<span ex:content=".suffix" class="suffix"></span>
	       		...
	       	</div>
	       	<div  ex:if-exists=".imageInDocumentSelector" class="context-content">
	       		<img ex:if-exists=".image" ex:src-content=".image">
	       	</div>
       	</div>
       	<br/>
	</div>