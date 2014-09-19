/**
 * 
 */
package org.ihtsdo.otf.refset.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mangofactory.swagger.configuration.SwaggerApiListingJsonSerializer;
import com.mangofactory.swagger.configuration.SwaggerResourceListingJsonSerializer;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;

/**Class to provide support to api-documentation json generation by Swagger annotation
 * https://github.com/martypitt/swagger-springmvc/issues/342
 * @author Episteme Partners
 *
 */
public class SwaggerModule extends SimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SwaggerModule() {

        super("SwaggerJacksonModule");
        
        addSerializer(ApiListing.class, new SwaggerApiListingJsonSerializer());
        addSerializer(ResourceListing.class, new SwaggerResourceListingJsonSerializer());


	}



    @Override
    public int hashCode()
    {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o;
    }

}
