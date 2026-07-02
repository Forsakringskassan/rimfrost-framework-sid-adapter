package se.fk.rimfrost.framework.sid;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.component.QuarkusComponentTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.fk.rimfrost.framework.sid.adapter.SidAdapter;
import se.fk.rimfrost.framework.sid.adapter.SidMapper;
import se.fk.rimfrost.framework.sid.exception.SidException;
import se.fk.rimfrost.framework.sid.model.Idtyp;
import se.fk.rimfrost.framework.sid.model.ImmutableIdtyp;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusComponentTest(useSystemConfigSources = true, value =
{
      SidMapper.class
})
public class SidAdapterTest
{
   private static WireMockServer server;

   @Inject
   SidAdapter sidAdapter;

   @BeforeAll
   public static void setup()
   {
      server = new WireMockServer(
            options()
                  .dynamicPort()
                  .usingFilesUnderDirectory("src/test/resources"));
      server.start();

      System.setProperty("sid.api.base-url", server.baseUrl());
   }

   @AfterAll
   public static void tearDown()
   {
      if (server != null)
      {
         server.stop();
         server = null;
      }
   }

   @BeforeEach
   void resetStubs()
   {
      server.resetToDefaultMappings();
   }

   @Test
   void should_throw_with_error_type_bad_request_on_status_400()
   {
      server.stubFor(WireMock.post(WireMock.urlPathEqualTo("/sid/status"))
            .willReturn(WireMock.aResponse().withStatus(400)));

      var exception = assertThrows(SidException.class, () -> sidAdapter.containsSid(List.of(createIdtyp())));
      assertEquals(SidException.ErrorType.BAD_REQUEST, exception.getErrorType());
   }

   @Test
   void should_throw_with_error_type_not_found_on_status_404()
   {
      server.stubFor(WireMock.post(WireMock.urlPathEqualTo("/sid/status"))
            .willReturn(WireMock.aResponse().withStatus(404)));

      var exception = assertThrows(SidException.class, () -> sidAdapter.containsSid(List.of(createIdtyp())));
      assertEquals(SidException.ErrorType.NOT_FOUND, exception.getErrorType());
   }

   @Test
   void should_throw_with_error_type_service_unavailable_on_status_503()
   {
      server.stubFor(WireMock.post(WireMock.urlPathEqualTo("/sid/status"))
            .willReturn(WireMock.aResponse().withStatus(503)));

      var exception = assertThrows(SidException.class, () -> sidAdapter.containsSid(List.of(createIdtyp())));
      assertEquals(SidException.ErrorType.SERVICE_UNAVAILABLE, exception.getErrorType());
   }

   @Test
   void should_throw_with_error_type_unexpected_error_on_status_500()
   {
      server.stubFor(WireMock.post(WireMock.urlPathEqualTo("/sid/status"))
            .willReturn(WireMock.aResponse().withStatus(500)));

      var exception = assertThrows(SidException.class, () -> sidAdapter.containsSid(List.of(createIdtyp())));
      assertEquals(SidException.ErrorType.UNEXPECTED_ERROR, exception.getErrorType());
   }

   @Test
   void should_throw_with_error_type_unexpected_error_on_null_response()
   {
      server.stubFor(WireMock.post(WireMock.urlPathEqualTo("/sid/status"))
            .willReturn(WireMock.aResponse().withBody((String) null)));

      var exception = assertThrows(SidException.class, () -> sidAdapter.containsSid(List.of(createIdtyp())));
      assertEquals(SidException.ErrorType.UNEXPECTED_ERROR, exception.getErrorType());
   }

   @Test
   void should_return_expected_status_on_success() throws SidException
   {
      assertTrue(sidAdapter.containsSid(List.of(createIdtyp())));
   }

   private Idtyp createIdtyp()
   {
      return ImmutableIdtyp.builder().typId(UUID.randomUUID().toString()).varde(UUID.randomUUID().toString()).build();
   }
}
