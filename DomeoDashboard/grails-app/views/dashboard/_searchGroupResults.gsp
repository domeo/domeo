<div id="results" style="display:none;" class="sectioncontainer">
				<div class="list" >
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="name" title="${message(code: 'group.id.label', default: 'Name')}" />
						<g:sortableColumn property="shortname" title="${message(code: 'group.id.label', default: 'shortname')}" />
						<g:sortableColumn property="description" title="${message(code: 'group.id.label', default: 'Description')}" />
						<g:sortableColumn property="dateCreated" title="${message(code: 'group.id.label', default: 'Created On')}" />
						<g:sortableColumn property="status" title="${message(code: 'group.id.label', default: 'Status')}" />
						<g:sortableColumn property="groupsCount" title="${message(code: 'group.id.label', default: '#Members')}" />
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="content">
				</tbody>
			</table>
			<div class="paginateButtons">
		   		<g:paginate total="1" />
			</div>
		</div>
</div>
