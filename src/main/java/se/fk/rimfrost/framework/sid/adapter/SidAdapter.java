package se.fk.rimfrost.framework.sid.adapter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.apache5.connector.Apache5ConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fk.rimfrost.framework.sid.exception.SidException;
import se.fk.rimfrost.framework.sid.model.Idtyp;
import se.fk.rimfrost.sid.jaxrsspec.controllers.generatedsource.SidApi;

import java.util.List;

/**
 * Adapter for querying the SID (Skyddad IDentitet) service.
 *
 * <p>Manages a JAX-RS client lifecycle and translates domain model types
 * to SID API requests, mapping HTTP error responses to typed {@link SidException}s.
 */
@SuppressWarnings("unused")
@ApplicationScoped
public class SidAdapter
{
   @ConfigProperty(name = "sid.api.base-url")
   String sidBaseUrl;

   @Inject
   SidMapper sidMapper;

   private SidApi sidClient;

   private Client client;

   private static final Logger LOGGER = LoggerFactory.getLogger(SidAdapter.class);

   /**
    * Initialises the JAX-RS client and the proxy to the SID API.
    */
   @PostConstruct
   public void init()
   {
      ClientConfig clientConfig = new ClientConfig();
      clientConfig.connectorProvider(new Apache5ConnectorProvider());
      client = ClientBuilder.newClient(clientConfig);
      sidClient = WebResourceFactory.newResource(SidApi.class, client.target(sidBaseUrl));
   }

   /**
    * Closes the JAX-RS client on bean destruction.
    */
   @PreDestroy
   void destroy()
   {
      sidClient = null;

      if (client != null)
      {
         client.close();
         client = null;
      }
   }

   /**
    * Checks whether any of the given individuals have a protected identity (SID).
    *
    * @param individer the individuals to check; must be non-null and non-empty
    * @return {@code true} if at least one individual has a protected identity
    * @throws SidException if the SID service returns an error or an unexpected response
    */
   public boolean containsSid(List<Idtyp> individer) throws SidException
   {
      try
      {
         var request = sidMapper.toSidStatusRequest(individer);
         var response = sidClient.getSidStatus(request);

         if (response == null)
         {
            var message = "Received an unexpected null response while checking for SID status";

            LOGGER.error(message);
            throw new SidException(SidException.ErrorType.UNEXPECTED_ERROR, message);
         }

         return response.getSid();
      }
      catch (NotFoundException e)
      {
         var message = "Service path not found while attempting to check SID status";

         LOGGER.error(message, e);
         throw new SidException(SidException.ErrorType.NOT_FOUND, message, e);
      }
      catch (BadRequestException e)
      {
         var message = "Request was rejected as a bad request while attempting to check SID status";

         LOGGER.error(message, e);
         throw new SidException(SidException.ErrorType.BAD_REQUEST, message, e);
      }
      catch (ServiceUnavailableException e)
      {
         var message = "Request could not be handled by server";

         LOGGER.error(message, e);
         throw new SidException(SidException.ErrorType.SERVICE_UNAVAILABLE, message, e);
      }
      catch (ProcessingException | WebApplicationException e)
      {
         var message = "An unexpected error occurred while attempting to check SID status";

         LOGGER.error(message, e);
         throw new SidException(SidException.ErrorType.UNEXPECTED_ERROR, message, e);
      }
   }
}
