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

   Logger LOGGER = LoggerFactory.getLogger(SidAdapter.class);

   @PostConstruct
   public void init()
   {
      ClientConfig clientConfig = new ClientConfig();
      clientConfig.connectorProvider(new Apache5ConnectorProvider());
      client = ClientBuilder.newClient(clientConfig);
      sidClient = WebResourceFactory.newResource(SidApi.class, client.target(sidBaseUrl));
   }

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
         throw new SidException(SidException.ErrorType.NOT_FOUND, message);
      }
      catch (BadRequestException e)
      {
         var message = "Request was rejected as a bad request while attempting to check SID status";

         LOGGER.error(message, e);
         throw new SidException(SidException.ErrorType.BAD_REQUEST, message);
      }
      catch (ServiceUnavailableException e)
      {
         var message = "Request could not be handled by server";

         LOGGER.error(message, e);
         throw new SidException(SidException.ErrorType.SERVICE_UNAVAILABLE, message);
      }
      catch (ProcessingException | WebApplicationException e)
      {
         var message = "An unexpected error occurred while attempting to check SID status";

         LOGGER.error(message, e);
         throw new SidException(SidException.ErrorType.UNEXPECTED_ERROR, message);
      }
   }
}
