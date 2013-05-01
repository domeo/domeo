package org.mindinformatics.services.connector.bioportal.textmining

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class BioPortalOntologyListParser {
	List<ExtendedRecord> parse(def xml){
		List<ExtendedRecord> records = []
		xml.data.list.ontologyBean.each{ontoXml ->
			ExtendedRecord ontoRecord = new  ExtendedRecord()
			(ontoRecord.properties).each{key,value->
				if(!['class', 'metaClass'].contains(key)){
					ontoRecord[key] = ontoXml[key]
				}
			}
			records << ontoRecord
		}
		records
	}
}
