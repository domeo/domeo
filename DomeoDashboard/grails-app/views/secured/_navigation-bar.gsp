	<nav class="navbar navbar-w3r navbar-fixed-top" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<g:link controller="secured" action="home" class="navbar-brand">${grailsApplication.config.domeo.shared.dashboard.title}</g:link>
			</div>
			<div class="navbar-collapse collapse navbar-right">
				<ul class="nav navbar-nav">
					<li><g:link controller="secured" action="search">Home</g:link></li>
					<li><g:link controller="secure" action="annotator">Annotator</g:link></li>
					<li><g:link controller="secured" action="browse">Browse</g:link></li>
					<li><g:link controller="secured" action="search">Search</g:link></li>
					
	
					<%-- 
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">Browse <b class="caret"></b></a>
						<ul class="dropdown-menu" role="menu">
							<li class="socials"><a href="https://twitter.com/share"
								class="twitter-share-button">Annotations</a>
							<li class="socials"><a href="https://twitter.com/share"
								class="twitter-share-button">Bibliography</a></li>
	
						</ul></li>
						--%>
					<li><g:link controller="dashboard" action="index">Dashboard</g:link></li>
					<li><a href="#contact"><img src="${resource(dir:'images/secure',file:'exit.png',plugin:'users-module')}" title="Logout" /> </a></li>
					
				</ul>
	
				<%-- 
		          <form class="navbar-form navbar-right">
		            <div class="form-group">
		              <input type="text" placeholder="Email" class="form-control">
		            </div>
		            <div class="form-group">
		              <input type="password" placeholder="Password" class="form-control">
		            </div>
		            <button type="submit" class="btn btn-success">Sign in</button>
		          </form>
	          --%>
			</div>
		</div>
	</nav>