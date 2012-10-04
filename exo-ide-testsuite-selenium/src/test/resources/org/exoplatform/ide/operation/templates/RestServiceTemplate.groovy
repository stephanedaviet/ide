// test groovy file template
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam

@Path("/my-service")
public class HelloWorld {
  @GET
  @Path("helloworld/{name}")
  public String hello(@PathParam("name") String name) {
    return "Hello " + name
  }
}