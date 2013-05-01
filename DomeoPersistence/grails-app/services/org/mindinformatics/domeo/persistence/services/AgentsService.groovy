package org.mindinformatics.domeo.persistence.services

class AgentsService {

    def grailsApplication;
    
    def getThisSoftware(format='json', id='domeo') {
        def name = grailsApplication.metadata['app.name']
        def fullname = grailsApplication.metadata['app.fullname']
        def build = grailsApplication.metadata['app.build']
        def version = grailsApplication.metadata['app.version']

        StringBuffer sb = new StringBuffer();
        
        if(format.equals("json") && id.equals("domeo")) {
            sb.append('[');
          
            sb.append("  {");
            sb.append("    \"@id\": \"");
            sb.append("urn:domeo:software:"+name+"-"+version+"-"+build);
            sb.append("\",");
            sb.append("    \"@type\": \"");
            sb.append("foafx:Software");
            sb.append("\",");
            sb.append("    \"domeo:uuid\": \"");
            sb.append(name+"_"+version+"_"+build);
            sb.append("\",");
            sb.append("    \"foafx:name\": \"");
            sb.append(fullname);
            sb.append("\",");
            sb.append("    \"foafx:version\": \"");
            sb.append(version);
            sb.append("\",");
            sb.append("    \"foafx:build\": \"");
            sb.append(build);
            sb.append("\"");
            sb.append("  },");
            
            sb.append(']');
        }
    
        return sb.toString();
    }

}
