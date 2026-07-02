package se.fk.rimfrost.framework.sid.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import se.fk.rimfrost.framework.sid.model.Idtyp;
import se.fk.rimfrost.sid.jaxrsspec.controllers.generatedsource.model.SidStatusRequest;

import java.util.List;

@ApplicationScoped
public class SidMapper
{
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
