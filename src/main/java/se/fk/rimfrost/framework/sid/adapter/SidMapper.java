package se.fk.rimfrost.framework.sid.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import se.fk.rimfrost.framework.sid.model.Idtyp;
import se.fk.rimfrost.sid.jaxrsspec.controllers.generatedsource.model.SidStatusRequest;

import java.util.List;

/**
 * Maps domain model types to SID API request models.
 */
@ApplicationScoped
public class SidMapper
{
   /**
    * Converts a list of {@link Idtyp} domain objects to a {@link SidStatusRequest}.
    *
    * @param individer the individuals to include in the request
    * @return a populated {@link SidStatusRequest}
    */
   public SidStatusRequest toSidStatusRequest(List<Idtyp> individer)
   {
      SidStatusRequest sidStatusRequest = new SidStatusRequest();
      sidStatusRequest.setIndivider(individer.stream().map(this::toIdtyp).toList());

      return sidStatusRequest;
   }

   private se.fk.rimfrost.sid.jaxrsspec.controllers.generatedsource.model.Idtyp toIdtyp(Idtyp modelIdtyp)
   {
      var idtyp = new se.fk.rimfrost.sid.jaxrsspec.controllers.generatedsource.model.Idtyp();
      idtyp.setTypId(modelIdtyp.typId());
      idtyp.setVarde(modelIdtyp.varde());
      return idtyp;
   }
}
