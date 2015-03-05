/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.refset.service.termserver;

import javax.annotation.PostConstruct;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *Terminology server's rest client which try to connect to specified endpoint with default authentication.
 */
@Component
public class TermClient extends RestTemplate {

	
	@Value("${ts.username}")
	private String userName;
	
	@Value("${ts.password}")
	private String password;
	
	@Value("${ts.server.host}")
	private String host;
	
	@PostConstruct 
	void addAuthProvider() {
		CredentialsProvider authProvider = new BasicCredentialsProvider();
		
        AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        
		authProvider.setCredentials(authScope, new UsernamePasswordCredentials(userName, password));
		
        HttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(authProvider).build();
        
        this.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
		this.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		this.getMessageConverters().add(new StringHttpMessageConverter());

	}
	
	public String getHost() {
		
		return host;
	}
}
